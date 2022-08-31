package com.stringee.app.dialog_fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.stringee.app.R.id;
import com.stringee.app.R.layout;
import com.stringee.app.R.menu;
import com.stringee.app.adapter.JSONModelAdapter;
import com.stringee.app.common.Utils;
import com.stringee.app.model.JSONModel;
import com.stringee.messaging.listeners.CallbackListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomMessageFragment extends BaseBottomSheetDialogFragment {
    private ImageButton btnAdd;
    private TextInputEditText etTo;
    private TextInputEditText etKey;
    private TextInputEditText etValue;
    private View vAdd;
    private View vMessage;
    private JSONModelAdapter adapter;
    private List<JSONModel> customMessages = new ArrayList<>();
    private boolean isAdd = false;
    private CallbackListener<Map> listener;

    public void setCallBack(CallbackListener<Map> listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layout.fragment_custom_message, container);

        ImageButton btnBack = view.findViewById(id.btn_back);
        btnBack.setOnClickListener(this);
        btnAdd = view.findViewById(id.btn_add);
        btnAdd.setOnClickListener(this);
        ImageButton btnDone = view.findViewById(id.btn_done);
        btnDone.setOnClickListener(this);

        vAdd = view.findViewById(id.v_add);
        vMessage = view.findViewById(id.v_message);
        etTo = view.findViewById(id.et_to);
        etKey = view.findViewById(id.et_key);
        etValue = view.findViewById(id.et_value);

        RecyclerView rvCustomMessage = view.findViewById(id.rv_custom_message);
        rvCustomMessage.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvCustomMessage.setHasFixedSize(true);

        adapter = new JSONModelAdapter(requireActivity(), customMessages);
        adapter.setOnItemClickListener(new com.stringee.app.listener.ItemClickListener() {
            @Override
            public void onLongClick(View view, int position) {
                super.onLongClick(view, position);
                PopupMenu popupMenu = new PopupMenu(requireContext(), view);
                popupMenu.setGravity(Gravity.END);
                popupMenu.getMenuInflater().inflate(menu.menu_delete, popupMenu.getMenu());
                popupMenu.setForceShowIcon(true);
                popupMenu.setOnMenuItemClickListener(item -> {
                            int itemId = item.getItemId();
                            if (itemId == id.menu_delete) {
                                customMessages.remove(position);
                                adapter.notifyItemRemoved(position);
                                return true;
                            }
                            return false;
                        }
                );
                popupMenu.show();
            }
        });
        rvCustomMessage.setAdapter(adapter);

        return view;
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == id.btn_add) {
            isAdd = true;
            updateView();
        } else if (vId == id.btn_done) {
            if (isAdd) {
                if (Utils.isStringEmpty(etKey.getText())) {
                    Utils.reportMessage(requireContext(), "key cannot empty");
                    return;
                }
                customMessages.add(new JSONModel(etKey.getText().toString(), etValue.getText() != null ? etValue.getText().toString() : ""));
                adapter.notifyItemInserted(customMessages.size() - 1);
                etKey.setText("");
                etValue.setText("");
                isAdd = false;
                updateView();
            } else {
                if (Utils.isStringEmpty(etTo.getText())) {
                    Utils.reportMessage(requireContext(), "to cannot empty");
                    return;
                }
                if (listener != null) {
                    try {
                        JSONObject msg = new JSONObject();
                        for (JSONModel customMessage : customMessages) {
                            msg.put(customMessage.getKey(), customMessage.getValue());
                        }
                        Map map = new HashMap();
                        map.put("to", etTo.getText().toString());
                        map.put("msg", msg);
                        listener.onSuccess(map);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                dismiss();
            }
        } else if (vId == id.btn_back) {
            if (isAdd) {
                etKey.setText("");
                etValue.setText("");
                isAdd = false;
                updateView();
            } else {
                dismiss();
            }
        }
    }

    private void updateView() {
        vAdd.setVisibility(isAdd ? View.VISIBLE : View.GONE);
        vMessage.setVisibility(isAdd ? View.GONE : View.VISIBLE);
        btnAdd.setVisibility(isAdd ? View.GONE : View.VISIBLE);
    }
}
