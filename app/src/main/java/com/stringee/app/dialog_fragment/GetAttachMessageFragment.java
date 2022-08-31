package com.stringee.app.dialog_fragment;

import android.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputEditText;
import com.stringee.app.R.array;
import com.stringee.app.R.id;
import com.stringee.app.R.layout;
import com.stringee.app.common.Utils;
import com.stringee.messaging.Message.Type;
import com.stringee.messaging.listeners.CallbackListener;

import java.util.HashMap;
import java.util.Map;

public class GetAttachMessageFragment extends BaseBottomSheetDialogFragment {
    private TextInputEditText etCount;
    private TextInputEditText etStart;
    private CallbackListener<Map> listener;
    private Type type;

    public void setCallBack(CallbackListener<Map> listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layout.fragment_get_attach_message, container);

        android.widget.ImageButton btnBack = view.findViewById(id.btn_back);
        btnBack.setOnClickListener(this);
        android.widget.ImageButton btnDone = view.findViewById(id.btn_done);
        btnDone.setOnClickListener(this);
        etCount = view.findViewById(id.et_count);
        etStart = view.findViewById(id.et_start);

        Spinner spType = view.findViewById(id.sp_type);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(requireContext(), array.attach_message_array, R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spType.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String status = (String) arrayAdapter.getItem(i);
                switch (status) {
                    case "PHOTO":
                        type = Type.PHOTO;
                        break;
                    case "VIDEO":
                        type = Type.VIDEO;
                        break;
                    case "AUDIO":
                        type = Type.AUDIO;
                        break;
                    case "FILE":
                        type = Type.FILE;
                        break;
                    case "LINK":
                        type = Type.LINK;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spType.setAdapter(arrayAdapter);

        return view;
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == id.btn_done) {
            if (listener != null) {
                if (type == null) {
                    Utils.reportMessage(requireContext(), "type cannot empty");
                    return;
                }
                Map map = new HashMap();
                map.put("count", Utils.getEditableText(etCount.getText(), true));
                map.put("start", Utils.getEditableText(etStart.getText(), true));
                map.put("type", type);
                listener.onSuccess(map);
            }
            dismiss();
        } else if (vId == id.btn_back) {
            dismiss();
        }
    }
}
