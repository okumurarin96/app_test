package com.stringee.app.dialog_fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;

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
import com.stringee.messaging.ConversationOptions;
import com.stringee.messaging.User;
import com.stringee.messaging.listeners.CallbackListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateConversationFragment extends BaseBottomSheetDialogFragment {
    private TextInputEditText etName;
    private TextInputEditText etCustomData;
    private TextInputEditText etId;
    private RadioButton btnGroup;
    private RadioButton btnDistinct;
    private View vConvInfo;
    private View vAdd;
    private boolean isAdd = false;
    private StringAdapter adapter;
    private List<String> userIds = new ArrayList<>();
    private CallbackListener<Map> listener;

    public void setCallBack(CallbackListener<Map> listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layout.fragment_create_conversation, container);

        ImageButton btnBack = view.findViewById(id.btn_back);
        btnBack.setOnClickListener(this);
        ImageButton btnDone = view.findViewById(id.btn_done);
        btnDone.setOnClickListener(this);
        ImageButton btnEdit = view.findViewById(id.btn_edit);
        btnEdit.setOnClickListener(this);
        btnGroup = view.findViewById(id.btn_group);
        btnDistinct = view.findViewById(id.btn_distinct);

        vAdd = view.findViewById(id.v_add);
        vConvInfo = view.findViewById(id.v_conv_info);
        etName = view.findViewById(id.et_name);
        etCustomData = view.findViewById(id.et_custom_data);
        etId = view.findViewById(id.et_id);

        RecyclerView rvParticipant = view.findViewById(id.rv_participant);
        rvParticipant.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvParticipant.setHasFixedSize(true);

        adapter = new StringAdapter(requireActivity(), userIds);
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
                                CreateConversationFragment.this.userIds.remove(position);
                                adapter.notifyItemRemoved(position);
                                return true;
                            }
                            return false;
                        }
                );
                popupMenu.show();
            }
        });
        rvParticipant.setAdapter(adapter);

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
                if (Utils.isStringEmpty(etId.getText())) {
                    Utils.reportMessage(requireContext(), "id cannot empty");
                    return;
                }
                userIds.add(etId.getText().toString().trim());
                adapter.notifyItemInserted(userIds.size() - 1);
                etId.setText("");
                isAdd = false;
                updateView();
            } else {
                if (Utils.isListEmpty(userIds)) {
                    Utils.reportMessage(requireContext(), "no participant");
                    return;
                }
                if (listener != null) {
                    Map map = new HashMap();
                    List<User> users = new java.util.ArrayList<>();
                    for (String userId : userIds) {
                        users.add(new User(userId));
                    }
                    map.put("participant", users);
                    ConversationOptions options = new ConversationOptions();
                    options.setName(Utils.getEditableText(etName.getText(), false));
                    options.setGroup(btnGroup.isChecked());
                    options.setDistinct(btnDistinct.isChecked());
                    options.setCustomData(Utils.getEditableText(etCustomData.getText(), false));
                    map.put("options", options);
                    listener.onSuccess(map);
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
        vConvInfo.setVisibility(isAdd ? View.GONE : View.VISIBLE);
    }
}
