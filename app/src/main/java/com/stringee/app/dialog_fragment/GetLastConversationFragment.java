package com.stringee.app.dialog_fragment;

import android.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.stringee.app.R.array;
import com.stringee.app.R.id;
import com.stringee.app.R.layout;
import com.stringee.app.adapter.ChannelTypeAdapter;
import com.stringee.app.common.Utils;
import com.stringee.app.listener.ItemClickListener;
import com.stringee.app.model.ChannelType;
import com.stringee.messaging.Conversation;
import com.stringee.messaging.ConversationFilter;
import com.stringee.messaging.ConversationFilter.ConversationFilterChatSupportStatus;
import com.stringee.messaging.listeners.CallbackListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetLastConversationFragment extends BaseBottomSheetDialogFragment {
    private TextInputEditText etCount;
    private RadioButton btnDeleted;
    private RadioButton btnUnread;
    private View vStatus;
    private ChannelTypeAdapter adapter;
    private List<ChannelType> channelTypes = new ArrayList<>();
    private CallbackListener<Map> listener;
    private ConversationFilterChatSupportStatus chatSupportStatus = ConversationFilterChatSupportStatus.ALL;

    public void setCallBack(CallbackListener<Map> listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layout.fragment_get_last_conversation, container);

        ImageButton btnBack = view.findViewById(id.btn_back);
        btnBack.setOnClickListener(this);
        ImageButton btnDone = view.findViewById(id.btn_done);
        btnDone.setOnClickListener(this);
        btnDeleted = view.findViewById(id.btn_deleted);
        btnUnread = view.findViewById(id.btn_unread);
        etCount = view.findViewById(id.et_count);

        vStatus = view.findViewById(id.v_status);
        Spinner spStatus = view.findViewById(id.sp_status);

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(requireContext(), array.chat_support_status_array, R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spStatus.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String status = (String) arrayAdapter.getItem(i);
                switch (status) {
                    case "Current":
                        chatSupportStatus = ConversationFilterChatSupportStatus.CURRENT_CHAT;
                        break;
                    case "Past":
                        chatSupportStatus = ConversationFilterChatSupportStatus.PAST_CHAT;
                        break;
                    case "All":
                        chatSupportStatus = ConversationFilterChatSupportStatus.ALL;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spStatus.setAdapter(arrayAdapter);

        RecyclerView rvChannelType = view.findViewById(id.rv_channel_type);
        rvChannelType.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvChannelType.setHasFixedSize(true);

        channelTypes.add(new ChannelType(Conversation.ChannelType.NORMAL));
        channelTypes.add(new ChannelType(Conversation.ChannelType.LIVECHAT));
        channelTypes.add(new ChannelType(Conversation.ChannelType.FACEBOOK));
        channelTypes.add(new ChannelType(Conversation.ChannelType.ZALO));

        adapter = new ChannelTypeAdapter(requireActivity(), channelTypes);
        adapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                super.onClick(view, position);
                channelTypes.get(position).setSelected(!channelTypes.get(position).isSelected());
                adapter.notifyItemChanged(position);
                updateView();
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
                ConversationFilter filter = new ConversationFilter();
                filter.setChannelTypes(channelTypeList);
                filter.setFilterChatStatus(chatSupportStatus);
                filter.setDeleted(btnDeleted.isChecked());
                filter.setUnread(btnUnread.isChecked());
                map.put("count", Utils.getEditableText(etCount.getText(), true));
                map.put("filter", filter);
                listener.onSuccess(map);
            }
            dismiss();
        } else if (vId == id.btn_back) {
            dismiss();
        }
    }

    private void updateView() {
        boolean isShow = false;
        for (ChannelType channelType : channelTypes) {
            if (channelType.getChannelType() != Conversation.ChannelType.NORMAL) {
                if (channelType.isSelected()) {
                    isShow = true;
                    break;
                }
            }
        }
        vStatus.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }
}
