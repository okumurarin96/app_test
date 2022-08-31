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

public class GetMessageOptionFragment extends BaseBottomSheetDialogFragment {
    private TextInputEditText etCount;
    private TextInputEditText etSequence;
    private CallbackListener<Map> listener;
    private boolean hasSequence;

    public void setCallBack(boolean hasSequence, CallbackListener<Map> listener) {
        this.hasSequence = hasSequence;
        this.listener = listener;
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layout.fragment_get_message_option, container);

        ImageButton btnBack = view.findViewById(id.btn_back);
        btnBack.setOnClickListener(this);
        ImageButton btnDone = view.findViewById(id.btn_done);
        btnDone.setOnClickListener(this);
        etCount = view.findViewById(id.et_count);
        etSequence = view.findViewById(id.et_sequence);
        if (hasSequence) {
            etSequence.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == id.btn_done) {
            if (listener != null) {
                Map map = new HashMap();
                map.put("count", Utils.getEditableText(etCount.getText(), true));
                map.put("sequence", Utils.getEditableText(etSequence.getText(), true));
                listener.onSuccess(map);
            }
            dismiss();
        } else if (vId == id.btn_back) {
            dismiss();
        }
    }
}
