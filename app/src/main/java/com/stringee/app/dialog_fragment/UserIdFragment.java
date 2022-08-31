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
import com.stringee.app.adapter.UserIdAdapter;
import com.stringee.app.common.Constant;
import com.stringee.app.common.PrefUtils;
import com.stringee.app.common.Utils;
import com.stringee.app.listener.ItemClickListener;
import com.stringee.app.model.UserId;
import com.stringee.messaging.listeners.CallbackListener;

import java.util.ArrayList;
import java.util.List;

public class UserIdFragment extends BaseBottomSheetDialogFragment {
    private RecyclerView rvUserId;
    private ImageButton btnAdd;
    private TextInputEditText etId;
    private View vAdd;
    private UserIdAdapter adapter;
    private List<UserId> userIds = new ArrayList<>();
    private boolean isAdd = false;
    private CallbackListener<List<UserId>> listener;

    public void setCallBack(CallbackListener<List<UserId>> listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layout.fragment_user_id, container);

        ImageButton btnBack = view.findViewById(id.btn_back);
        btnBack.setOnClickListener(this);
        btnAdd = view.findViewById(id.btn_add);
        btnAdd.setOnClickListener(this);
        ImageButton btnDone = view.findViewById(id.btn_done);
        btnDone.setOnClickListener(this);

        vAdd = view.findViewById(id.v_add);
        etId = view.findViewById(id.et_id);

        rvUserId = view.findViewById(id.rv_user_id);
        rvUserId.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvUserId.setHasFixedSize(true);

        userIds = Utils.getListFromStringJSON(PrefUtils.getInstance(requireContext()).getString(Constant.PREF_USER_ID, null), UserId.class);
        if (Utils.isListEmpty(userIds)) {
            userIds = new ArrayList<>();
        }

        adapter = new UserIdAdapter(requireActivity(), userIds);
        adapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                UserId userId = userIds.get(position);
                userId.setSelected(!userId.isSelected());
                adapter.notifyItemChanged(position);
            }

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
                                userIds.remove(position);
                                adapter.notifyItemRemoved(position);
                                saveUserId();
                                return true;
                            }
                            return false;
                        }
                );
                popupMenu.show();
            }
        });
        rvUserId.setAdapter(adapter);

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
                    Utils.reportMessage(requireContext(), "user id cannot empty");
                    return;
                }
                userIds.add(new UserId(etId.getText().toString().trim()));
                adapter.notifyItemInserted(userIds.size() - 1);
                etId.setText("");
                saveUserId();
                isAdd = false;
                updateView();
            } else {
                if (listener != null) {
                    saveUserId();
                    listener.onSuccess(userIds);
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
        rvUserId.setVisibility(isAdd ? View.GONE : View.VISIBLE);
        btnAdd.setVisibility(isAdd ? View.GONE : View.VISIBLE);
    }

    private void saveUserId() {
        PrefUtils.getInstance(requireContext()).putString(Constant.PREF_USER_ID, Utils.convertObjectToStringJSON(userIds));
    }
}
