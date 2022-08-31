package com.stringee.app.dialog_fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputEditText;
import com.stringee.app.R.id;
import com.stringee.app.R.layout;
import com.stringee.app.common.Utils;
import com.stringee.messaging.listeners.CallbackListener;

public class InputSingleStringFragment extends BaseBottomSheetDialogFragment {
    private TextInputEditText etId;
    private CallbackListener<String> listener;
    private final String title;
    private final String hint;

    public InputSingleStringFragment(String title, String hint) {
        this.title = title;
        this.hint = hint;
    }

    public void setCallBack(CallbackListener<String> listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layout.fragment_input_single_string, container);

        ImageButton btnBack = view.findViewById(id.btn_back);
        btnBack.setOnClickListener(this);
        ImageButton btnDone = view.findViewById(id.btn_done);
        btnDone.setOnClickListener(this);
        TextView tvTitle = view.findViewById(id.tv_title);
        tvTitle.setText(title);

        etId = view.findViewById(id.et_id);
        etId.setHint(hint);

        return view;
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == id.btn_done) {
            if (listener != null) {
                listener.onSuccess(Utils.getEditableText(etId.getText(), true));
            }
            dismiss();
        } else if (vId == id.btn_back) {
            dismiss();
        }
    }
}
