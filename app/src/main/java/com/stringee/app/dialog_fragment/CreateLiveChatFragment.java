package com.stringee.app.dialog_fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.stringee.app.R.id;
import com.stringee.app.R.layout;
import com.stringee.app.adapter.QueueAdapter;
import com.stringee.app.common.Common;
import com.stringee.app.common.Constant;
import com.stringee.app.common.PrefUtils;
import com.stringee.app.common.Utils;
import com.stringee.app.listener.ItemClickListener;
import com.stringee.app.model.QueueObject;
import com.stringee.exception.StringeeError;
import com.stringee.messaging.ChatProfile;
import com.stringee.messaging.Queue;
import com.stringee.messaging.listeners.CallbackListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateLiveChatFragment extends BaseBottomSheetDialogFragment {
    private TextInputEditText etCustomData;
    private QueueAdapter adapter;
    private List<QueueObject> queues = new ArrayList<>();
    private CallbackListener<Map> listener;

    public void setCallBack(CallbackListener<Map> listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layout.fragment_create_live_chat, container);

        ImageButton btnBack = view.findViewById(id.btn_back);
        btnBack.setOnClickListener(this);
        ImageButton btnDone = view.findViewById(id.btn_done);
        btnDone.setOnClickListener(this);

        etCustomData = view.findViewById(id.et_custom_data);

        RecyclerView rvQueue = view.findViewById(id.rv_queue);
        rvQueue.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvQueue.setHasFixedSize(true);

        Common.client.getChatProfile(PrefUtils.getInstance(requireContext()).getString(Constant.PREF_WIDGET_KEY, ""), new CallbackListener<ChatProfile>() {
            @Override
            public void onSuccess(ChatProfile chatProfile) {
                for (int i = 0; i < chatProfile.getQueues().size(); i++) {
                    Queue queue = chatProfile.getQueues().get(i);
                    QueueObject queueObject = new QueueObject(queue);
                    if (i == 0) {
                        queueObject.setSelected(true);
                    }
                    queues.add(queueObject);
                }
                adapter = new QueueAdapter(requireActivity(), queues);
                adapter.setOnItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        super.onClick(view, position);
                        for (int i = 0; i < queues.size(); i++) {
                            QueueObject queueObject = queues.get(i);
                            if (queueObject.isSelected()) {
                                queueObject.setSelected(false);
                                adapter.notifyItemChanged(i);
                                break;
                            }
                        }
                        queues.get(position).setSelected(!queues.get(position).isSelected());
                        adapter.notifyItemChanged(position);
                    }
                });
                rvQueue.setAdapter(adapter);
                Utils.updateLog(requireContext(), "getChatProfile success:" + Utils.convertObjectToStringJSON(chatProfile));
            }

            @Override
            public void onError(StringeeError stringeeError) {
                super.onError(stringeeError);
                Utils.updateLog(requireContext(), "getChatProfile error:" + stringeeError.getMessage());
            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == id.btn_done) {
            if (listener != null) {
                Map map = new HashMap();
                for (QueueObject queueObject : queues) {
                    if (queueObject.isSelected()) {
                        map.put("queue", queueObject.getQueue().getId());
                        break;
                    }
                }
                map.put("custom_data", Utils.getEditableText(etCustomData.getText(), true));
                listener.onSuccess(map);
            }
            dismiss();
        } else if (vId == id.btn_back) {
            dismiss();
        }
    }
}
