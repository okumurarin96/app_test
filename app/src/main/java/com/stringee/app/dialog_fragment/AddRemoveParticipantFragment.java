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
import com.stringee.app.common.Utils;
import com.stringee.app.listener.ItemClickListener;
import com.stringee.app.model.UserId;
import com.stringee.messaging.Conversation;
import com.stringee.messaging.User;
import com.stringee.messaging.listeners.CallbackListener;

import java.util.ArrayList;
import java.util.List;

public class AddRemoveParticipantFragment extends BaseBottomSheetDialogFragment {
    private ImageButton btnAdd;
    private TextInputEditText etId;
    private View vAdd;
    private View vParticipant;
    private UserIdAdapter adapter;
    private List<UserId> participants = new ArrayList<>();
    private boolean isAdd = false;
    private CallbackListener<List<User>> listener;
    private boolean isRemove;
    private Conversation conversation;

    public void setCallBack(boolean isRemove, Conversation conversation, CallbackListener<List<User>> listener) {
        this.listener = listener;
        this.isRemove = isRemove;
        this.conversation = conversation;
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layout.fragment_add_remove_participant, container);

        ImageButton btnBack = view.findViewById(id.btn_back);
        btnBack.setOnClickListener(this);
        btnAdd = view.findViewById(id.btn_add);
        btnAdd.setOnClickListener(this);
        ImageButton btnDone = view.findViewById(id.btn_done);
        btnDone.setOnClickListener(this);

        vAdd = view.findViewById(id.v_add);
        vParticipant = view.findViewById(id.v_participant);
        etId = view.findViewById(id.et_id);

        RecyclerView rvCustomMessage = view.findViewById(id.rv_participant);
        rvCustomMessage.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvCustomMessage.setHasFixedSize(true);

        if (isRemove) {
            for (User user : conversation.getParticipants()) {
                participants.add(new UserId(user.getUserId()));
            }
            btnAdd.setVisibility(View.GONE);
        }

        adapter = new UserIdAdapter(requireActivity(), participants);
        adapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onLongClick(View view, int position) {
                super.onLongClick(view, position);
                if (!isRemove) {
                    PopupMenu popupMenu = new PopupMenu(requireContext(), view);
                    popupMenu.setGravity(Gravity.END);
                    popupMenu.getMenuInflater().inflate(menu.menu_delete, popupMenu.getMenu());
                    popupMenu.setForceShowIcon(true);
                    popupMenu.setOnMenuItemClickListener(item -> {
                                int itemId = item.getItemId();
                                if (itemId == id.menu_delete) {
                                    participants.remove(position);
                                    adapter.notifyItemRemoved(position);
                                    return true;
                                }
                                return false;
                            }
                    );
                    popupMenu.show();
                }
            }

            @Override
            public void onClick(View view, int position) {
                super.onClick(view, position);
                UserId userId = participants.get(position);
                userId.setSelected(!userId.isSelected());
                adapter.notifyItemChanged(position);
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
                participants.add(new UserId(etId.getText().toString().trim()));
                adapter.notifyItemInserted(participants.size() - 1);
                etId.setText("");
                isAdd = false;
                updateView();
            } else {
                if (listener != null) {
                    List<User> users = new ArrayList<>();
                    for (UserId userId : participants) {
                        if (userId.isSelected()) {
                            users.add(new User(userId.getId()));
                        }
                    }
                    listener.onSuccess(users);
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
        vParticipant.setVisibility(isAdd ? View.GONE : View.VISIBLE);
        btnAdd.setVisibility(isAdd ? View.GONE : View.VISIBLE);
    }
}
