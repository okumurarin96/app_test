package com.stringee.app.dialog_fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stringee.app.R.id;
import com.stringee.app.R.layout;
import com.stringee.app.adapter.UserIdAdapter;
import com.stringee.app.listener.ItemClickListener;
import com.stringee.app.model.UserId;
import com.stringee.messaging.Conversation;
import com.stringee.messaging.User;
import com.stringee.messaging.User.Role;
import com.stringee.messaging.listeners.CallbackListener;

import java.util.ArrayList;
import java.util.List;

public class SetRoleFragment extends BaseBottomSheetDialogFragment {
    private RadioButton btnAdmin;
    private RadioButton btnMember;
    private UserIdAdapter adapter;
    private List<UserId> userIds = new ArrayList<>();
    private CallbackListener<UserId> listener;
    private Conversation conversation;

    public void setCallBack(Conversation conversation, CallbackListener<UserId> listener) {
        this.listener = listener;
        this.conversation = conversation;
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layout.fragment_set_role, container);

        ImageButton btnBack = view.findViewById(id.btn_back);
        btnBack.setOnClickListener(this);
        ImageButton btnDone = view.findViewById(id.btn_done);
        btnDone.setOnClickListener(this);
        btnAdmin = view.findViewById(id.btn_admin);
        btnMember = view.findViewById(id.btn_member);

        RecyclerView rvParticipant = view.findViewById(id.rv_participant);
        rvParticipant.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvParticipant.setHasFixedSize(true);

        for (User user : conversation.getParticipants()) {
            UserId userId = new UserId(user.getUserId());
            userId.setRole(user.getRole());
            userIds.add(userId);
        }
        UserId firstUser = userIds.get(0);
        firstUser.setSelected(true);
        updateView(firstUser);

        adapter = new UserIdAdapter(requireActivity(), userIds);
        adapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                super.onClick(view, position);
                for (int i = 0; i < userIds.size(); i++) {
                    UserId userId = userIds.get(i);
                    if (userId.isSelected()) {
                        userId.setSelected(false);
                        adapter.notifyItemChanged(i);
                        break;
                    }
                }
                UserId userId = userIds.get(position);
                userId.setSelected(true);
                updateView(userId);
                adapter.notifyItemChanged(position);
            }
        });
        rvParticipant.setAdapter(adapter);

        return view;
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == id.btn_done) {
            if (listener != null) {
                for (UserId userId : userIds) {
                    if (userId.isSelected()) {
                        userId.setRole(btnAdmin.isEnabled() ? Role.ADMIN : Role.MEMBER);
                        listener.onSuccess(userId);
                    }
                }
                dismiss();
            }
        } else if (vId == id.btn_back) {
            dismiss();
        }
    }

    private void updateView(UserId userId) {
        btnAdmin.setEnabled(userId.getRole() == Role.ADMIN);
        btnMember.setEnabled(userId.getRole() == Role.MEMBER);
    }
}
