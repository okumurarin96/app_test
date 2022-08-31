package com.stringee.app.dialog_fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputEditText;
import com.stringee.app.R.id;
import com.stringee.app.R.layout;
import com.stringee.app.common.Utils;
import com.stringee.messaging.listeners.CallbackListener;

import java.util.HashMap;
import java.util.Map;

public class CreateLiveChatTicketFragment extends BaseBottomSheetDialogFragment {
    private TextInputEditText etName;
    private TextInputEditText etEmail;
    private TextInputEditText etPhone;
    private TextInputEditText etNote;
    private CallbackListener<Map> listener;

    public void setCallBack(CallbackListener<Map> listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layout.fragment_create_live_chat_ticket, container);

        ImageButton btnBack = view.findViewById(id.btn_back);
        btnBack.setOnClickListener(this);
        ImageButton btnDone = view.findViewById(id.btn_done);
        btnDone.setOnClickListener(this);

        etName = view.findViewById(id.et_name);
        etEmail = view.findViewById(id.et_email);
        etPhone = view.findViewById(id.et_phone);
        etNote = view.findViewById(id.et_note);

        return view;
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == id.btn_done) {
            if (listener != null) {
                Map map = new HashMap();
                map.put("name", Utils.getEditableText(etName.getText(), true));
                map.put("email", Utils.getEditableText(etEmail.getText(), true));
                map.put("phone", Utils.getEditableText(etPhone.getText(), true));
                map.put("note", Utils.getEditableText(etNote.getText(), true));
                listener.onSuccess(map);
            }
            dismiss();
        } else if (vId == id.btn_back) {
            dismiss();
        }
    }
}
