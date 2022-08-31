package com.stringee.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textfield.TextInputEditText;
import com.stringee.app.R.id;
import com.stringee.app.R.layout;
import com.stringee.app.activity.OutgoingCall2Activity;
import com.stringee.app.activity.OutgoingCallActivity;
import com.stringee.app.common.Common;
import com.stringee.app.common.Utils;

public class TabCallFragment extends BaseFragment {
    private TextInputEditText etTo;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layout.fragment_tab_call, container, false);
        etTo = view.findViewById(id.et_to);
        view.findViewById(id.btn_voice_call).setOnClickListener(this);
        view.findViewById(id.btn_video_call).setOnClickListener(this);
        view.findViewById(id.btn_voice_call2).setOnClickListener(this);
        view.findViewById(id.btn_video_call2).setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View view) {
        int vId = view.getId();
        if (vId == id.btn_voice_call) {
            makeCall(false, true);
        } else if (vId == id.btn_video_call) {
            makeCall(true, true);
        } else if (vId == id.btn_voice_call2) {
            makeCall(false, false);
        } else if (vId == id.btn_video_call2) {
            makeCall(true, false);
        }
    }

    private void makeCall(boolean isVideoCall, boolean isStringeeCall) {
        if (!Common.client.isConnected()) {
            Utils.reportMessage(requireContext(), "client is not connected");
            return;
        }
        if (Utils.isStringEmpty(etTo.getText())) {
            Utils.reportMessage(requireContext(), "to cannot empty");
            return;
        }

        String to = etTo.getText().toString();
        Intent intent;
        if (isStringeeCall) {
            intent = new Intent(requireContext(), OutgoingCallActivity.class);
        } else {
            intent = new Intent(requireContext(), OutgoingCall2Activity.class);
        }
        intent.putExtra("from", Common.client.getUserId());
        intent.putExtra("to", to);
        intent.putExtra("is_video_call", isVideoCall);
        Common.launcher.launch(intent);
    }
}
