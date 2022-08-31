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
import com.stringee.app.adapter.ChannelTypeAdapter;
import com.stringee.app.model.ChannelType;
import com.stringee.messaging.Conversation;
import com.stringee.messaging.listeners.CallbackListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetLocalConversationFragment extends BaseBottomSheetDialogFragment {
    private RadioButton btnEnded;
    private ChannelTypeAdapter adapter;
    private List<ChannelType> channelTypes = new ArrayList<>();
    private CallbackListener<Map> listener;

    public void setCallBack(CallbackListener<Map> listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layout.fragment_get_local_conversation, container);

        ImageButton btnBack = view.findViewById(id.btn_back);
        btnBack.setOnClickListener(this);
        ImageButton btnDone = view.findViewById(id.btn_done);
        btnDone.setOnClickListener(this);
        btnEnded = view.findViewById(id.btn_ended);

        RecyclerView rvChannelType = view.findViewById(id.rv_channel_type);
        rvChannelType.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvChannelType.setHasFixedSize(true);

        channelTypes.add(new ChannelType(Conversation.ChannelType.NORMAL));
        channelTypes.add(new ChannelType(Conversation.ChannelType.LIVECHAT));
        channelTypes.add(new ChannelType(Conversation.ChannelType.FACEBOOK));
        channelTypes.add(new ChannelType(Conversation.ChannelType.ZALO));

        adapter = new ChannelTypeAdapter(requireActivity(), channelTypes);
        adapter.setOnItemClickListener(new com.stringee.app.listener.ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                super.onClick(view, position);
                channelTypes.get(position).setSelected(!channelTypes.get(position).isSelected());
                adapter.notifyItemChanged(position);
            }
        });
        rvChannelType.setAdapter(adapter);

        return view;
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == id.btn_done) {
            if (listener != null) {
                Map map = new HashMap();
                List<Conversation.ChannelType> channelTypeList = new ArrayList<>();
                for (ChannelType channelType : channelTypes) {
                    if (channelType.isSelected()) {
                        channelTypeList.add(channelType.getChannelType());
                    }
                }
                map.put("ended", btnEnded.isChecked());
                map.put("channel_type", channelTypeList);
                listener.onSuccess(map);
            }
            dismiss();
        } else if (vId == id.btn_back) {
            dismiss();
        }
    }
}
