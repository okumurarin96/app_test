package com.stringee.app.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener;
import com.google.android.material.tabs.TabLayout.Tab;
import com.google.android.material.tabs.TabLayoutMediator;
import com.stringee.StringeeClient;
import com.stringee.app.R.drawable;
import com.stringee.app.R.id;
import com.stringee.app.R.layout;
import com.stringee.app.R.string;
import com.stringee.app.adapter.StringAdapter;
import com.stringee.app.adapter.ViewPagerAdapter;
import com.stringee.app.common.Common;
import com.stringee.app.common.Notify;
import com.stringee.app.common.Utils;
import com.stringee.app.fragment.ObjectActionFragment;
import com.stringee.app.fragment.TabCallFragment;
import com.stringee.app.fragment.TabChatFragment;
import com.stringee.app.fragment.TabGeneralFragment;
import com.stringee.call.StringeeCall;
import com.stringee.call.StringeeCall2;
import com.stringee.exception.StringeeError;
import com.stringee.listener.StatusListener;
import com.stringee.listener.StringeeConnectionListener;
import com.stringee.messaging.ChatRequest;
import com.stringee.messaging.Conversation;
import com.stringee.messaging.StringeeChange;
import com.stringee.messaging.User;
import com.stringee.messaging.listeners.ChangeEventListener;
import com.stringee.messaging.listeners.LiveChatEventListener;
import com.stringee.messaging.listeners.UserTypingEventListener;

import org.json.JSONObject;

public class MainActivity extends BaseActivity implements StringeeConnectionListener, ChangeEventListener, LiveChatEventListener, UserTypingEventListener {
    private RecyclerView rvLog;
    private ImageButton btnLog;
    private ImageButton btnBack;
    private TextView tvTitle;
    private StringAdapter adapter;
    private BroadcastReceiver updateLogReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);

        Common.client = new StringeeClient(this);
        Common.client.setConnectionListener(this);
        Common.client.setChangeEventListener(this);
        Common.client.setLiveChatEventListener(this);
        Common.client.setUserTypingEventListener(this);
        tvTitle = findViewById(id.tv_title);

        btnBack = findViewById(id.btn_back);
        btnBack.setOnClickListener(this);

        btnLog = findViewById(id.btn_log);
        btnLog.setOnClickListener(this);

        rvLog = findViewById(id.rv_log);
        rvLog.setLayoutManager(new LinearLayoutManager(this));
        rvLog.setHasFixedSize(true);
        adapter = new StringAdapter(this, Common.logs);
        rvLog.setAdapter(adapter);

        TabLayout tabLayout = findViewById(id.tabs);
        ViewPager2 viewPager = findViewById(id.v_pager);

        viewPager.setOffscreenPageLimit(3);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPagerAdapter.addFragment(new TabGeneralFragment());
        viewPagerAdapter.addFragment(new TabCallFragment());
        viewPagerAdapter.addFragment(new TabChatFragment());

        viewPager.setAdapter(viewPagerAdapter);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setIcon(drawable.tab_general);
                    break;
                case 1:
                    tab.setIcon(drawable.tab_call);
                    break;
                case 2:
                    tab.setIcon(drawable.tab_chat);
                    break;
            }
        });
        tabLayoutMediator.attach();
        tabLayout.addOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {

            }

            @Override
            public void onTabUnselected(Tab tab) {
                if (tab.getPosition() == 2) {
                    removeFragment("ObjectActionFragment");
                }
            }

            @Override
            public void onTabReselected(Tab tab) {

            }
        });

        updateLogReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                runOnUiThread(() -> {
                    adapter.notifyDataSetChanged();
                    rvLog.smoothScrollToPosition(Common.logs.size() - 1);
                });
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(updateLogReceiver, new IntentFilter(Notify.UPDATE_LOG.getValue()));

        Common.launcher = registerForActivityResult(new StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_CANCELED)
                        if (result.getData() != null) {
                            if (result.getData().getAction() != null && result.getData().getAction().equals("open_app_setting")) {
                                Builder builder = new Builder(this);
                                builder.setTitle(string.app_name);
                                builder.setMessage("Permissions must be granted for the call");
                                builder.setPositiveButton("Ok", (dialogInterface, id) -> {
                                    dialogInterface.cancel();
                                });
                                builder.setNegativeButton("Settings", (dialogInterface, id) -> {
                                    dialogInterface.cancel();
                                    // open app setting
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                });
                                builder.create().show();
                            }
                        }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (updateLogReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(updateLogReceiver);
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int vId = view.getId();
        if (vId == id.btn_log) {
            rvLog.animate()
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            btnLog.setImageResource(rvLog.getVisibility() == View.VISIBLE ? drawable.ic_up : drawable.ic_down);
                        }

                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                        }
                    }).start();
            rvLog.setVisibility(rvLog.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        } else if (vId == id.btn_back) {
            onBackPressed();
//            removeFragment("ObjectActionFragment");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public <T> void showObjectActionFragment(T object) {
        btnBack.setVisibility(View.VISIBLE);
        ObjectActionFragment fragment = new ObjectActionFragment(object);
        addFragment(fragment, "ObjectActionFragment");
    }

    @Override
    public void onConnectionConnected(StringeeClient stringeeClient, boolean isReconnecting) {
        runOnUiThread(() -> {
            Utils.updateLog(MainActivity.this, "onConnectionConnected: " + stringeeClient.getUserId());
            tvTitle.setText(Common.client.getUserId());
        });
    }

    @Override
    public void onConnectionDisconnected(StringeeClient stringeeClient, boolean isReconnecting) {
        runOnUiThread(() -> {
            Utils.updateLog(MainActivity.this, "onConnectionDisconnected");
            tvTitle.setText("Disconnected");
        });
    }

    @Override
    public void onIncomingCall(StringeeCall stringeeCall) {
        Utils.runOnUiThread(() -> {
            Utils.updateLog(MainActivity.this, "onIncomingCall: callId - " + stringeeCall.getCallId() + ", from - " + stringeeCall.getFrom() + ", to - " + stringeeCall.getTo());
            if (Common.isInCall) {
                stringeeCall.reject(new StatusListener() {
                    @Override
                    public void onSuccess() {

                    }
                });
            } else {
                Common.callsMap.put(stringeeCall.getCallId(), stringeeCall);
                Intent intent = new Intent(this, IncomingCallActivity.class);
                intent.putExtra("call_id", stringeeCall.getCallId());
                Common.launcher.launch(intent);
            }
        });
    }

    @Override
    public void onIncomingCall2(StringeeCall2 stringeeCall2) {
        Utils.runOnUiThread(() -> {
            Utils.updateLog(MainActivity.this, "onIncomingCall2: callId - " + stringeeCall2.getCallId() + ", from - " + stringeeCall2.getFrom() + ", to - " + stringeeCall2.getTo());
            if (Common.isInCall) {
                stringeeCall2.reject(new StatusListener() {
                    @Override
                    public void onSuccess() {

                    }
                });
            } else {
                Common.calls2Map.put(stringeeCall2.getCallId(), stringeeCall2);
                Intent intent = new Intent(this, IncomingCall2Activity.class);
                intent.putExtra("call_id", stringeeCall2.getCallId());
                Common.launcher.launch(intent);
            }
        });
    }

    @Override
    public void onConnectionError(StringeeClient stringeeClient, StringeeError stringeeError) {
        Utils.runOnUiThread(() -> {
            Utils.updateLog(MainActivity.this, "onConnectionError: " + stringeeError.getMessage());
            tvTitle.setText("Connect error");
        });
    }

    @Override
    public void onRequestNewToken(StringeeClient stringeeClient) {
        Utils.runOnUiThread(() -> Utils.updateLog(MainActivity.this, "onRequestNewToken"));
    }

    @Override
    public void onCustomMessage(String s, JSONObject jsonObject) {
        Utils.runOnUiThread(() -> Utils.updateLog(MainActivity.this, "onCustomMessage: " + s));
    }

    @Override
    public void onTopicMessage(String s, JSONObject jsonObject) {
        Utils.runOnUiThread(() -> Utils.updateLog(MainActivity.this, "onTopicMessage: " + s));
    }

    @Override
    public void onChangeEvent(StringeeChange stringeeChange) {
        Utils.runOnUiThread(() -> Utils.updateLog(MainActivity.this, "onChangeEvent: type - " + stringeeChange.getObjectType() + ", change type - " + stringeeChange.getChangeType()));
    }

    @Override
    public void onReceiveChatRequest(ChatRequest chatRequest) {
        Utils.runOnUiThread(() -> Utils.updateLog(MainActivity.this, "onReceiveChatRequest: " + chatRequest.getConvId()));
    }

    @Override
    public void onReceiveTransferChatRequest(ChatRequest chatRequest) {
        Utils.runOnUiThread(() -> Utils.updateLog(MainActivity.this, "onReceiveTransferChatRequest: " + chatRequest.getConvId()));
    }

    @Override
    public void onHandleOnAnotherDevice(ChatRequest chatRequest, ChatRequest.State state) {
        Utils.runOnUiThread(() -> Utils.updateLog(MainActivity.this, "onHandleOnAnotherDevice: " + chatRequest.getConvId() + ", state - " + state));
    }

    @Override
    public void onTimeoutAnswerChat(ChatRequest chatRequest) {
        Utils.runOnUiThread(() -> Utils.updateLog(MainActivity.this, "onTimeoutAnswerChat: " + chatRequest.getConvId()));
    }

    @Override
    public void onTimeoutInQueue(Conversation conversation) {
        Utils.runOnUiThread(() -> Utils.updateLog(MainActivity.this, "onTimeoutInQueue: " + conversation.getId()));
    }

    @Override
    public void onConversationEnded(Conversation conversation, User user) {
        Utils.runOnUiThread(() -> Utils.updateLog(MainActivity.this, "onConversationEnded: " + conversation.getId() + ", by - " + user.getUserId()));
    }

    @Override
    public void onTyping(Conversation conversation, User user) {
        Utils.runOnUiThread(() -> Utils.updateLog(MainActivity.this, "onTyping: " + conversation.getId() + ", by - " + user.getUserId()));
    }

    @Override
    public void onEndTyping(Conversation conversation, User user) {
        Utils.runOnUiThread(() -> Utils.updateLog(MainActivity.this, "onEndTyping: " + conversation.getId() + ", by - " + user.getUserId()));
    }
}