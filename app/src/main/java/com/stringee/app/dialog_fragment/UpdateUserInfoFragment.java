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
import com.stringee.app.common.Common;
import com.stringee.app.common.Utils;
import com.stringee.app.listener.ItemClickListener;
import com.stringee.app.model.JSONModel;
import com.stringee.messaging.User;
import com.stringee.messaging.UserInfo;
import com.stringee.messaging.listeners.CallbackListener;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class UpdateUserInfoFragment extends BaseBottomSheetDialogFragment {
    private TextInputEditText etName;
    private TextInputEditText etEmail;
    private TextInputEditText etPhone;
    private TextInputEditText etAvatar;
    private TextInputEditText etKey;
    private TextInputEditText etValue;
    private View vUserInfo;
    private View vAdd;
    private boolean isAdd = false;
    private JSONModelAdapter adapter;
    private List<JSONModel> userAgents = new ArrayList<>();
    private CallbackListener<UserInfo> listener;

    public void setCallBack(CallbackListener<UserInfo> listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layout.fragment_update_user, container);

        ImageButton btnBack = view.findViewById(id.btn_back);
        btnBack.setOnClickListener(this);
        ImageButton btnDone = view.findViewById(id.btn_done);
        btnDone.setOnClickListener(this);
        ImageButton btnEdit = view.findViewById(id.btn_edit);
        btnEdit.setOnClickListener(this);

        vAdd = view.findViewById(id.v_add);
        vUserInfo = view.findViewById(id.v_user_info);
        etName = view.findViewById(id.et_name);
        etEmail = view.findViewById(id.et_email);
        etPhone = view.findViewById(id.et_phone);
        etAvatar = view.findViewById(id.et_avatar);
        etKey = view.findViewById(id.et_key);
        etValue = view.findViewById(id.et_value);

        List<String> userIds = new ArrayList<>();
        userIds.add(Common.client.getUserId());
        Common.client.getUserInfo(userIds, new CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                User user = users.get(0);
                etName.setText(user.getName());
                etEmail.setText(user.getEmail());
                etPhone.setText(user.getPhone());
                etAvatar.setText(user.getAvatarUrl());
                try {
                    userAgents = Utils.jsonToJSONModel(user.getUserAgent());
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        RecyclerView rvUserAgent = view.findViewById(id.rv_user_agent);
        rvUserAgent.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvUserAgent.setHasFixedSize(true);

        adapter = new JSONModelAdapter(requireActivity(), userAgents);
        adapter.setOnItemClickListener(new ItemClickListener() {
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
                                userAgents.remove(position);
                                adapter.notifyItemRemoved(position);
                                return true;
                            }
                            return false;
                        }
                );
                popupMenu.show();
            }
        });
        rvUserAgent.setAdapter(adapter);

        return view;
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == id.btn_edit) {
            isAdd = true;
            updateView();
        } else if (vId == id.btn_done) {
            if (isAdd) {
                if (Utils.isStringEmpty(etKey.getText())) {
                    Utils.reportMessage(requireContext(), "key cannot empty");
                    return;
                }
                userAgents.add(new JSONModel(Utils.getEditableText(etKey.getText(),false), Utils.getEditableText(etValue.getText(),false)));
                adapter.notifyItemInserted(userAgents.size() - 1);
                etKey.setText("");
                etValue.setText("");
                isAdd = false;
                updateView();
            } else {
                if (listener != null) {
                    UserInfo userInfo = new com.stringee.messaging.UserInfo();
                    userInfo.setName(Utils.getEditableText(etName.getText(),false));
                    userInfo.setEmail(Utils.getEditableText(etEmail.getText(),false));
                    userInfo.setPhone(Utils.getEditableText(etPhone.getText(),false));
                    userInfo.setAvatarUrl(Utils.getEditableText(etAvatar.getText(),false));
                    try {
                        userInfo.setUserAgent(Utils.jsonModelToJSON(userAgents));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    listener.onSuccess(userInfo);
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
        vUserInfo.setVisibility(isAdd ? View.GONE : View.VISIBLE);
    }
}
