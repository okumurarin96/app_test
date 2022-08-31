package com.stringee.app.dialog_fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.material.textfield.TextInputEditText;
import com.stringee.app.R.id;
import com.stringee.app.R.layout;
import com.stringee.app.common.Utils;
import com.stringee.messaging.listeners.CallbackListener;

import java.util.Map;

public class SendChatTranscriptFragment extends BaseBottomSheetDialogFragment {
    private TextInputEditText etEmail;
    private TextInputEditText etDomain;
    private CallbackListener<Map> listener;

    public void setCallBack(CallbackListener<Map> listener) {
        this.listener = listener;
    }

    @androidx.annotation.NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layout.fragment_send_chat_transcript, container);

        ImageButton btnBack = view.findViewById(id.btn_back);
        btnBack.setOnClickListener(this);
        ImageButton btnDone = view.findViewById(id.btn_done);
        btnDone.setOnClickListener(this);
        etEmail = view.findViewById(id.et_email);
        etDomain = view.findViewById(id.et_domain);
        return view;
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == id.btn_done) {
            if (listener != null) {
                Map map = new java.util.HashMap();
                map.put("email", Utils.getEditableText(etEmail.getText(), true));
                map.put("domain", Utils.getEditableText(etDomain.getText(), true));
                listener.onSuccess(map);
            }
            dismiss();
        } else if (vId == id.btn_back) {
            dismiss();
        }
    }
}
