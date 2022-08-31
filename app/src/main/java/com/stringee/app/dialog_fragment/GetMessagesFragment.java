package com.stringee.app.dialog_fragment;

import android.os.Bundle;
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
import com.stringee.app.adapter.StringAdapter;
import com.stringee.app.common.Utils;
import com.stringee.app.listener.ItemClickListener;
import com.stringee.messaging.listeners.CallbackListener;

import java.util.ArrayList;
import java.util.List;

public class GetMessagesFragment extends BaseBottomSheetDialogFragment {
    private ImageButton btnAdd;
    private TextInputEditText etId;
    private View vAdd;
    private View vMessage;
    private StringAdapter adapter;
    private List<String> messageIds = new ArrayList<>();
    private boolean isAdd = false;
    private CallbackListener<List<String>> listener;

    public void setCallBack(CallbackListener<List<String>> listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layout.fragment_get_messages, container);

        ImageButton btnBack = view.findViewById(id.btn_back);
        btnBack.setOnClickListener(this);
        btnAdd = view.findViewById(id.btn_add);
        btnAdd.setOnClickListener(this);
        ImageButton btnDone = view.findViewById(id.btn_done);
        btnDone.setOnClickListener(this);

        vAdd = view.findViewById(id.v_add);
        vMessage = view.findViewById(id.v_message);
        etId = view.findViewById(id.et_id);

        RecyclerView rvCustomMessage = view.findViewById(id.rv_message);
        rvCustomMessage.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvCustomMessage.setHasFixedSize(true);

        adapter = new StringAdapter(requireActivity(), messageIds);
        adapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onLongClick(View view, int position) {
                super.onLongClick(view, position);
                PopupMenu popupMenu = new PopupMenu(requireContext(), view);
                popupMenu.setGravity(android.view.Gravity.END);
                popupMenu.getMenuInflater().inflate(menu.menu_delete, popupMenu.getMenu());
                popupMenu.setForceShowIcon(true);
                popupMenu.setOnMenuItemClickListener(item -> {
                            int itemId = item.getItemId();
                            if (itemId == id.menu_delete) {
                                messageIds.remove(position);
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
                if (Utils.isStringEmpty(etId.getText())) {
                    Utils.reportMessage(requireContext(), "id cannot empty");
                    return;
                }
                messageIds.add(etId.getText().toString());
                adapter.notifyItemInserted(messageIds.size() - 1);
                etId.setText("");
                isAdd = false;
                updateView();
            } else {
                if (listener != null) {
                    listener.onSuccess(messageIds);
                }
                dismiss();
            }
        } else if (vId == id.btn_back) {
            if (isAdd) {
                etId.setText("");
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
