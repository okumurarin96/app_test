package com.stringee.app.activity;

import android.Manifest.permission;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.stringee.app.R.drawable;
import com.stringee.app.R.id;
import com.stringee.app.R.layout;
import com.stringee.app.common.Common;
import com.stringee.app.common.Utils;
import com.stringee.call.StringeeCall2;
import com.stringee.common.StringeeAudioManager;
import com.stringee.exception.StringeeError;
import com.stringee.listener.StatusListener;
import com.stringee.video.StringeeVideoTrack;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OutgoingCall2Activity extends BaseActivity {
    private FrameLayout vLocal;
    private FrameLayout vRemote;
    private TextView tvState;
    private ImageButton btnMute;
    private ImageButton btnSpeaker;
    private ImageButton btnVideo;
    private ImageButton btnEnd;
    private ImageButton btnSwitch;
    private View vControl;

    private StringeeCall2 stringeeCall2;
    private StringeeAudioManager audioManager;
    private String from;
    private String to;
    private boolean isVideoCall;
    private boolean isMute = false;
    private boolean isSpeaker = false;
    private boolean isVideo = false;
    private boolean isPermissionGranted = true;

    private StringeeCall2.MediaState mMediaState;
    private StringeeCall2.SignalingState mSignalingState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //add Flag for show on lockScreen and disable keyguard
        getWindow().addFlags(LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | LayoutParams.FLAG_DISMISS_KEYGUARD
                | LayoutParams.FLAG_KEEP_SCREEN_ON
                | LayoutParams.FLAG_TURN_SCREEN_ON);

        if (VERSION.SDK_INT >= VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        }

        setContentView(layout.activity_outgoing_call);

        Common.isInCall = true;

        from = getIntent().getStringExtra("from");
        to = getIntent().getStringExtra("to");
        isVideoCall = getIntent().getBooleanExtra("is_video_call", false);

        initView();

        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            List<String> lstPermissions = new ArrayList<>();

            if (ContextCompat.checkSelfPermission(this, permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                lstPermissions.add(permission.RECORD_AUDIO);
            }

            if (isVideoCall) {
                if (ContextCompat.checkSelfPermission(this, permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    lstPermissions.add(permission.CAMERA);
                }
            }

            if (VERSION.SDK_INT >= VERSION_CODES.S) {
                if (ContextCompat.checkSelfPermission(this, permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    lstPermissions.add(permission.BLUETOOTH_CONNECT);
                }
            }

            if (lstPermissions.size() > 0) {
                String[] permissions = new String[lstPermissions.size()];
                for (int i = 0; i < lstPermissions.size(); i++) {
                    permissions[i] = lstPermissions.get(i);
                }
                ActivityCompat.requestPermissions(this, permissions, Common.REQUEST_PERMISSION_CALL);
                return;
            }
        }

        makeCall();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isGranted = false;
        if (grantResults.length > 0) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    isGranted = false;
                    break;
                } else {
                    isGranted = true;
                }
            }
        }
        if (requestCode == Common.REQUEST_PERMISSION_CALL) {
            if (!isGranted) {
                isPermissionGranted = false;
                tvState.setText("Ended");
                dismissLayout();
            } else {
                isPermissionGranted = true;
                makeCall();
            }
        }
    }

    private void initView() {
        vLocal = findViewById(id.v_local);
        vRemote = findViewById(id.v_remote);

        vControl = findViewById(id.v_control);

        TextView tvTo = findViewById(id.tv_to);
        tvTo.setText(to);
        tvState = findViewById(id.tv_state);

        btnMute = findViewById(id.btn_mute);
        btnMute.setOnClickListener(this);
        btnSpeaker = findViewById(id.btn_speaker);
        btnSpeaker.setOnClickListener(this);
        btnVideo = findViewById(id.btn_video);
        btnVideo.setOnClickListener(this);
        btnSwitch = findViewById(id.btn_switch);
        btnSwitch.setOnClickListener(this);
        btnEnd = findViewById(id.btn_end);
        btnEnd.setOnClickListener(this);

        isSpeaker = isVideoCall;
        btnSpeaker.setBackgroundResource(isSpeaker ? drawable.btn_speaker_on : drawable.btn_speaker_off);

        isVideo = isVideoCall;
        btnVideo.setImageResource(isVideo ? drawable.btn_video : drawable.btn_video_off);

        btnVideo.setVisibility(isVideo ? View.VISIBLE : View.GONE);
        btnSwitch.setVisibility(isVideo ? View.VISIBLE : View.GONE);
    }

    private void makeCall() {
        //create audio manager to control audio device
        audioManager = StringeeAudioManager.create(OutgoingCall2Activity.this);
        audioManager.start((selectedAudioDevice, availableAudioDevices) ->
                Utils.updateLog(OutgoingCall2Activity.this, "selectedAudioDevice: " + selectedAudioDevice + " - availableAudioDevices: " + availableAudioDevices));
        audioManager.setSpeakerphoneOn(isVideoCall);

        //make a call
        stringeeCall2 = new StringeeCall2(Common.client, from, to);
        stringeeCall2.setVideoCall(isVideoCall);

        stringeeCall2.setCallListener(new StringeeCall2.StringeeCallListener() {
            @Override
            public void onSignalingStateChange(StringeeCall2 stringeeCall2, final StringeeCall2.SignalingState signalingState, String reason, int sipCode, String sipReason) {
                runOnUiThread(() -> {
                    Utils.updateLog(OutgoingCall2Activity.this, "onSignalingStateChange: " + signalingState);
                    mSignalingState = signalingState;
                    switch (signalingState) {
                        case CALLING:
                            tvState.setText("Outgoing call");
                            break;
                        case RINGING:
                            tvState.setText("Ringing");
                            break;
                        case ANSWERED:
                            tvState.setText("Starting");
                            if (mMediaState == StringeeCall2.MediaState.CONNECTED) {
                                tvState.setText("Started");
                            }
                            break;
                        case BUSY:
                            tvState.setText("Busy");
                            endCall();
                            break;
                        case ENDED:
                            tvState.setText("Ended");
                            endCall();
                            break;
                    }
                });
            }

            @Override
            public void onError(StringeeCall2 stringeeCall2, int code, String desc) {
                runOnUiThread(() -> {
                    Utils.updateLog(OutgoingCall2Activity.this, "onError: " + desc);
                    Utils.reportMessage(OutgoingCall2Activity.this, desc);
                    tvState.setText("Ended");
                    dismissLayout();
                });
            }

            @Override
            public void onHandledOnAnotherDevice(StringeeCall2 stringeeCall2, StringeeCall2.SignalingState signalingState, String desc) {
            }

            @Override
            public void onMediaStateChange(StringeeCall2 stringeeCall2, final StringeeCall2.MediaState mediaState) {
                runOnUiThread(() -> {
                    Utils.updateLog(OutgoingCall2Activity.this, "onMediaStateChange: " + mediaState);
                    mMediaState = mediaState;
                    if (mediaState == StringeeCall2.MediaState.CONNECTED) {
                        if (mSignalingState == StringeeCall2.SignalingState.ANSWERED) {
                            tvState.setText("Started");
                        }
                    }
                });
            }

            @Override
            public void onLocalStream(final StringeeCall2 stringeeCall2) {
                runOnUiThread(() -> {
                    Utils.updateLog(OutgoingCall2Activity.this, "onLocalStream");
                    if (stringeeCall2.isVideoCall()) {
                        vLocal.removeAllViews();
                        vLocal.addView(stringeeCall2.getLocalView());
                        stringeeCall2.renderLocalView(true);
                    }
                });
            }

            @Override
            public void onRemoteStream(final StringeeCall2 stringeeCall2) {
                runOnUiThread(() -> {
                    Utils.updateLog(OutgoingCall2Activity.this, "onRemoteStream");
                    if (stringeeCall2.isVideoCall()) {
                        vRemote.removeAllViews();
                        vRemote.addView(stringeeCall2.getRemoteView());
                        stringeeCall2.renderRemoteView(false);
                    }
                });
            }

            @Override
            public void onVideoTrackAdded(StringeeVideoTrack stringeeVideoTrack) {
                runOnUiThread(() -> Utils.updateLog(OutgoingCall2Activity.this, "onVideoTrackAdded: " + stringeeVideoTrack.getId()));
            }

            @Override
            public void onVideoTrackRemoved(StringeeVideoTrack stringeeVideoTrack) {
                runOnUiThread(() -> Utils.updateLog(OutgoingCall2Activity.this, "onVideoTrackRemoved: " + stringeeVideoTrack.getId()));
            }

            @Override
            public void onCallInfo(StringeeCall2 stringeeCall2, final JSONObject jsonObject) {
                runOnUiThread(() -> Utils.updateLog(OutgoingCall2Activity.this, "onCallInfo: " + jsonObject.toString()));
            }

            @Override
            public void onTrackMediaStateChange(String from, StringeeVideoTrack.MediaType mediaType, boolean enable) {
                runOnUiThread(() -> Utils.updateLog(OutgoingCall2Activity.this, "onTrackMediaStateChange: from - " + from + ", mediaType - " + mediaType + " - " + enable));
            }
        });

        stringeeCall2.makeCall(new StatusListener() {
            @Override
            public void onSuccess() {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case id.btn_mute:
                isMute = !isMute;
                btnMute.setBackgroundResource(isMute ? drawable.btn_mute : drawable.btn_mic);
                if (stringeeCall2 != null) {
                    stringeeCall2.mute(isMute);
                }
                break;
            case id.btn_speaker:
                isSpeaker = !isSpeaker;
                btnSpeaker.setBackgroundResource(isSpeaker ? drawable.btn_speaker_on : drawable.btn_speaker_off);
                if (audioManager != null) {
                    audioManager.setSpeakerphoneOn(isSpeaker);
                }
                break;
            case id.btn_end:
                tvState.setText("Ended");
                endCall();
                break;
            case id.btn_video:
                isVideo = !isVideo;
                btnVideo.setImageResource(isVideo ? drawable.btn_video : drawable.btn_video_off);
                if (stringeeCall2 != null) {
                    stringeeCall2.enableVideo(isVideo);
                }
                break;
            case id.btn_switch:
                if (stringeeCall2 != null) {
                    stringeeCall2.switchCamera(new StatusListener() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(StringeeError stringeeError) {
                            super.onError(stringeeError);
                            runOnUiThread(() -> {
                                Log.d("Stringee", "switchCamera error: " + stringeeError.getMessage());
                                Utils.reportMessage(OutgoingCall2Activity.this, stringeeError.getMessage());
                            });
                        }
                    });
                }
                break;
        }
    }

    private void endCall() {
        stringeeCall2.hangup(new StatusListener() {
            @Override
            public void onSuccess() {

            }
        });
        dismissLayout();
    }

    private void dismissLayout() {
        if (audioManager != null) {
            audioManager.stop();
            audioManager = null;
        }
        vControl.setVisibility(View.GONE);
        btnEnd.setVisibility(View.GONE);
        btnSwitch.setVisibility(View.GONE);
        Utils.postDelayed(() -> {
            Common.isInCall = false;
            if (!isPermissionGranted) {
                Intent intent = new Intent();
                intent.setAction("open_app_setting");
                setResult(RESULT_CANCELED, intent);
            }
            finish();
        }, 1000);
    }
}