package com.stringee.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stringee.app.R.id;
import com.stringee.app.R.layout;
import com.stringee.app.common.Common;
import com.stringee.app.common.Constant;
import com.stringee.app.common.PrefUtils;
import com.stringee.app.common.Utils;
import com.stringee.app.dialog_fragment.CreateConversationFragment;
import com.stringee.app.dialog_fragment.CreateLiveChatFragment;
import com.stringee.app.dialog_fragment.GetConversationWithTimeFragment;
import com.stringee.app.dialog_fragment.GetLastConversationFragment;
import com.stringee.app.dialog_fragment.GetLocalConversationFragment;
import com.stringee.app.dialog_fragment.InputSingleStringFragment;
import com.stringee.app.dialog_fragment.ListStringeeObjectFragment;
import com.stringee.exception.StringeeError;
import com.stringee.listener.StatusListener;
import com.stringee.messaging.ChatRequest;
import com.stringee.messaging.Conversation;
import com.stringee.messaging.Conversation.ChannelType;
import com.stringee.messaging.ConversationFilter;
import com.stringee.messaging.ConversationOptions;
import com.stringee.messaging.User;
import com.stringee.messaging.listeners.CallbackListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TabChatFragment extends BaseFragment {

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layout.fragment_tab_chat, container, false);

        view.findViewById(id.btn_create_conversation).setOnClickListener(this);
        view.findViewById(id.btn_get_local_conv).setOnClickListener(this);
        view.findViewById(id.btn_get_local_conv_by_channel).setOnClickListener(this);
        view.findViewById(id.btn_get_last_conv).setOnClickListener(this);
        view.findViewById(id.btn_get_conv_after).setOnClickListener(this);
        view.findViewById(id.btn_get_conv_before).setOnClickListener(this);
        view.findViewById(id.btn_clear_db).setOnClickListener(this);
        view.findViewById(id.btn_get_conv).setOnClickListener(this);
        view.findViewById(id.btn_get_chat_request).setOnClickListener(this);
        view.findViewById(id.btn_get_total_unread).setOnClickListener(this);
        view.findViewById(id.btn_get_conv_by_user_id).setOnClickListener(this);
        view.findViewById(id.btn_create_live_chat).setOnClickListener(this);
        view.findViewById(id.btn_create_live_chat_ticket).setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View view) {
        int vId = view.getId();
        if (!Common.client.isConnected()) {
            return;
        }
        if (vId == id.btn_create_conversation) {
            CreateConversationFragment fragment = (CreateConversationFragment) getParentFragmentManager().findFragmentByTag("CreateConversationFragment");
            if (fragment != null) {
                fragment.dismiss();
            }

            fragment = new CreateConversationFragment();
            fragment.setCallBack(new CallbackListener<Map>() {
                @Override
                public void onSuccess(Map map) {
                    List<User> participants = (List<User>) map.get("participant");
                    ConversationOptions options = (ConversationOptions) map.get("options");
                    createConversation(participants, options);
                }
            });
            fragment.show(getParentFragmentManager().beginTransaction(), "CreateConversationFragment");
        } else if (vId == id.btn_get_local_conv) {
            getLocalConversation();
        } else if (vId == id.btn_get_local_conv_by_channel) {
            GetLocalConversationFragment fragment = (GetLocalConversationFragment) getParentFragmentManager().findFragmentByTag("GetLocalConversationFragment");
            if (fragment != null) {
                fragment.dismiss();
            }
            fragment = new GetLocalConversationFragment();
            fragment.setCallBack(new com.stringee.messaging.listeners.CallbackListener<Map>() {
                @Override
                public void onSuccess(Map map) {
                    boolean isEnded = (boolean) map.get("ended");
                    List<Conversation.ChannelType> channelTypes = (List<ChannelType>) map.get("channel_type");
                    getLocalConversationByChannel(isEnded, channelTypes);
                }
            });
            fragment.show(getParentFragmentManager().beginTransaction(), "GetLocalConversationFragment");
        } else if (vId == id.btn_get_last_conv) {
            GetLastConversationFragment fragment = (GetLastConversationFragment) getParentFragmentManager().findFragmentByTag("GetLastConversationFragment");
            if (fragment != null) {
                fragment.dismiss();
            }
            fragment = new GetLastConversationFragment();
            fragment.setCallBack(new com.stringee.messaging.listeners.CallbackListener<Map>() {
                @Override
                public void onSuccess(Map map) {
                    ConversationFilter filter = (ConversationFilter) map.get("filter");
                    int count = Integer.parseInt((String) map.get("count"));
                    getLastConversations(count, filter);
                }
            });
            fragment.show(getParentFragmentManager().beginTransaction(), "GetLastConversationFragment");
        } else if (vId == id.btn_get_conv_after) {
            GetConversationWithTimeFragment fragment = (GetConversationWithTimeFragment) getParentFragmentManager().findFragmentByTag("GetConversationWithTimeFragment");
            if (fragment != null) {
                fragment.dismiss();
            }
            fragment = new GetConversationWithTimeFragment();
            fragment.setCallBack(new com.stringee.messaging.listeners.CallbackListener<Map>() {
                @Override
                public void onSuccess(Map map) {
                    ConversationFilter filter = (ConversationFilter) map.get("filter");
                    int count = Integer.parseInt((String) map.get("count"));
                    long updateAt = (Long) map.get("update_at");
                    getConversationsAfter(updateAt, count, filter);
                }
            });
            fragment.show(getParentFragmentManager().beginTransaction(), "GetConversationWithTimeFragment");
        } else if (vId == id.btn_get_conv_before) {
            GetConversationWithTimeFragment fragment = (GetConversationWithTimeFragment) getParentFragmentManager().findFragmentByTag("GetConversationWithTimeFragment");
            if (fragment != null) {
                fragment.dismiss();
            }
            fragment = new GetConversationWithTimeFragment();
            fragment.setCallBack(new com.stringee.messaging.listeners.CallbackListener<Map>() {
                @Override
                public void onSuccess(Map map) {
                    ConversationFilter filter = (ConversationFilter) map.get("filter");
                    int count = Integer.parseInt((String) map.get("count"));
                    long updateAt = (Long) map.get("update_at");
                    getConversationsBefore(updateAt, count, filter);
                }
            });
            fragment.show(getParentFragmentManager().beginTransaction(), "GetConversationWithTimeFragment");
        } else if (vId == id.btn_clear_db) {
            Common.client.clearDb();
            Utils.updateLog(requireContext(), "clearDb success");
        } else if (vId == id.btn_get_conv) {
            InputSingleStringFragment fragment = (InputSingleStringFragment) getParentFragmentManager().findFragmentByTag("InputSingleStringFragment");
            if (fragment != null) {
                fragment.dismiss();
            }
            fragment = new InputSingleStringFragment("Conversation id", "Conversation id");
            fragment.setCallBack(new CallbackListener<String>() {
                @Override
                public void onSuccess(String convId) {
                    getConversation(convId);
                }
            });
            fragment.show(getParentFragmentManager().beginTransaction(), "InputSingleStringFragment");
        } else if (vId == id.btn_get_chat_request) {
            getChatRequest();
        } else if (vId == id.btn_get_total_unread) {
            getTotalUnreadConversations();
        } else if (vId == id.btn_get_conv_by_user_id) {
            InputSingleStringFragment fragment = (InputSingleStringFragment) getParentFragmentManager().findFragmentByTag("InputSingleStringFragment");
            if (fragment != null) {
                fragment.dismiss();
            }
            fragment = new InputSingleStringFragment("User id", "User id");
            fragment.setCallBack(new CallbackListener<String>() {
                @Override
                public void onSuccess(String userId) {
                    getConversationByUserId(userId);
                }
            });
            fragment.show(getParentFragmentManager().beginTransaction(), "InputSingleStringFragment");
        } else if (vId == id.btn_create_live_chat) {
            CreateLiveChatFragment fragment = (CreateLiveChatFragment) getParentFragmentManager().findFragmentByTag("CreateLiveChatFragment");
            if (fragment != null) {
                fragment.dismiss();
            }
            fragment = new CreateLiveChatFragment();
            fragment.setCallBack(new CallbackListener<Map>() {
                @Override
                public void onSuccess(Map map) {
                    createLiveChat((String) map.get("queue"), (String) map.get("custom_data"));
                }
            });
            fragment.show(getParentFragmentManager().beginTransaction(), "CreateLiveChatFragment");
        } else if (vId == id.btn_create_live_chat_ticket) {
            CreateLiveChatFragment fragment = (CreateLiveChatFragment) getParentFragmentManager().findFragmentByTag("CreateLiveChatFragment");
            if (fragment != null) {
                fragment.dismiss();
            }
            fragment = new CreateLiveChatFragment();
            fragment.setCallBack(new CallbackListener<Map>() {
                @Override
                public void onSuccess(Map map) {
                    createLiveChatTicket(PrefUtils.getInstance(requireContext()).getString(Constant.PREF_WIDGET_KEY, ""), (String) map.get("name"), (String) map.get("email"), (String) map.get("phone"), (String) map.get("note"));
                }
            });
            fragment.show(getParentFragmentManager().beginTransaction(), "CreateLiveChatFragment");
        }
    }

    public void createConversation(List<User> users, ConversationOptions options) {
        if (!Common.client.isConnected()) {
            return;
        }
        Common.client.createConversation(users, options, new CallbackListener<Conversation>() {
            @Override
            public void onSuccess(Conversation conversation) {
                List<Conversation> conversations = new ArrayList<>();
                conversations.add(conversation);
                showListConversation(conversations);
                Utils.updateLog(requireContext(), "createConversation success: " + Utils.convertObjectToStringJSON(conversation));
            }

            @Override
            public void onError(StringeeError stringeeError) {
                super.onError(stringeeError);
                Utils.updateLog(requireContext(), "createConversation error:" + stringeeError.getMessage());
            }
        });
    }

    public void getLocalConversation() {
        if (!Common.client.isConnected()) {
            return;
        }
        Common.client.getLocalConversations(Common.client.getUserId(), new CallbackListener<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                showListConversation(conversations);
                Utils.updateLog(requireContext(), "getLocalConversations success: " + Utils.convertObjectToStringJSON(conversations));
            }

            @Override
            public void onError(StringeeError stringeeError) {
                super.onError(stringeeError);
                Utils.updateLog(requireContext(), "getLocalConversations error:" + stringeeError.getMessage());
            }
        });
    }

    public void getLocalConversationByChannel(boolean isEnded, List<Conversation.ChannelType> channelTypes) {
        if (!Common.client.isConnected()) {
            return;
        }
        Common.client.getLocalConversationsByChannelType(Common.client.getUserId(), isEnded, channelTypes, new CallbackListener<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                showListConversation(conversations);
                Utils.updateLog(requireContext(), "getLocalConversationByChannel success: " + Utils.convertObjectToStringJSON(conversations));
            }

            @Override
            public void onError(StringeeError stringeeError) {
                super.onError(stringeeError);
                Utils.updateLog(requireContext(), "getLocalConversationByChannel error:" + stringeeError.getMessage());
            }
        });
    }

    public void getLastConversations(int count, ConversationFilter conversationFilter) {
        if (!Common.client.isConnected()) {
            return;
        }
        Common.client.getLastConversations(count, conversationFilter, new CallbackListener<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                showListConversation(conversations);
                Utils.updateLog(requireContext(), "getLastConversations success: " + Utils.convertObjectToStringJSON(conversations));
            }

            @Override
            public void onError(StringeeError stringeeError) {
                super.onError(stringeeError);
                Utils.updateLog(requireContext(), "getLastConversations error:" + stringeeError.getMessage());
            }
        });
    }

    public void getConversationsAfter(long updateAt, int count, ConversationFilter conversationFilter) {
        if (!Common.client.isConnected()) {
            return;
        }
        Common.client.getConversationsAfter(updateAt, count, conversationFilter, new CallbackListener<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                showListConversation(conversations);
                Utils.updateLog(requireContext(), "getConversationsAfter success: " + Utils.convertObjectToStringJSON(conversations));
            }

            @Override
            public void onError(StringeeError stringeeError) {
                super.onError(stringeeError);
                Utils.updateLog(requireContext(), "getConversationsAfter error:" + stringeeError.getMessage());
            }
        });
    }

    public void getConversationsBefore(long updateAt, int count, ConversationFilter conversationFilter) {
        if (!Common.client.isConnected()) {
            return;
        }
        Common.client.getConversationsBefore(updateAt, count, conversationFilter, new CallbackListener<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                showListConversation(conversations);
                Utils.updateLog(requireContext(), "getConversationsBefore success: " + Utils.convertObjectToStringJSON(conversations));
            }

            @Override
            public void onError(StringeeError stringeeError) {
                super.onError(stringeeError);
                Utils.updateLog(requireContext(), "getConversationsBefore error:" + stringeeError.getMessage());
            }
        });
    }

    public void getConversation(String convId) {
        if (!Common.client.isConnected()) {
            return;
        }
        Common.client.getConversationFromServer(convId, new CallbackListener<Conversation>() {
            @Override
            public void onSuccess(Conversation conversation) {
                List<Conversation> conversations = new ArrayList<>();
                conversations.add(conversation);
                showListConversation(conversations);
                Utils.updateLog(requireContext(), "getConversation success: " + Utils.convertObjectToStringJSON(conversation));
            }

            @Override
            public void onError(StringeeError stringeeError) {
                super.onError(stringeeError);
                Utils.updateLog(requireContext(), "getConversation error:" + stringeeError.getMessage());
            }
        });
    }

    public void getChatRequest() {
        if (!Common.client.isConnected()) {
            return;
        }
        Common.client.getChatRequests(new CallbackListener<List<ChatRequest>>() {
            @Override
            public void onSuccess(List<ChatRequest> chatRequests) {
                showListChatRequest(chatRequests);
                Utils.updateLog(requireContext(), "getChatRequests success: " + Utils.convertObjectToStringJSON(chatRequests));
            }

            @Override
            public void onError(StringeeError stringeeError) {
                super.onError(stringeeError);
                Utils.updateLog(requireContext(), "getChatRequests error:" + stringeeError.getMessage());
            }
        });
    }

    public void getTotalUnreadConversations() {
        if (!Common.client.isConnected()) {
            return;
        }
        Common.client.getTotalUnread(new CallbackListener<Integer>() {
            @Override
            public void onSuccess(Integer total) {
                Utils.updateLog(requireContext(), "getTotalUnread success: " + total);
            }

            @Override
            public void onError(StringeeError stringeeError) {
                super.onError(stringeeError);
                Utils.updateLog(requireContext(), "getTotalUnread error:" + stringeeError.getMessage());
            }
        });
    }

    public void getConversationByUserId(String id) {
        if (!Common.client.isConnected()) {
            return;
        }
        Common.client.getConversationByUserId(id, new CallbackListener<Conversation>() {
            @Override
            public void onSuccess(Conversation conversation) {
                List<Conversation> conversations = new ArrayList<>();
                conversations.add(conversation);
                showListConversation(conversations);
                Utils.updateLog(requireContext(), "getConversationByUserId success: " + Utils.convertObjectToStringJSON(conversation));
            }

            @Override
            public void onError(StringeeError stringeeError) {
                super.onError(stringeeError);
                Utils.updateLog(requireContext(), "getConversationByUserId error:" + stringeeError.getMessage());
            }
        });
    }

    public void createLiveChat(String queueId, String customData) {
        if (!Common.client.isConnected()) {
            return;
        }
        Common.client.createLiveChat(queueId, customData, new CallbackListener<Conversation>() {
            @Override
            public void onSuccess(Conversation conversation) {
                List<Conversation> conversations = new ArrayList<>();
                conversations.add(conversation);
                showListConversation(conversations);
                Utils.updateLog(requireContext(), "createLiveChat success: " + Utils.convertObjectToStringJSON(conversation));
            }

            @Override
            public void onError(StringeeError stringeeError) {
                super.onError(stringeeError);
                Utils.updateLog(requireContext(), "createLiveChat error:" + stringeeError.getMessage());
            }
        });
    }

    public void createLiveChatTicket(String key, String name, String email, String phone, String note) {
        if (!Common.client.isConnected()) {
            return;
        }
        Common.client.createLiveChatTicket(key, name, email, phone, note, new StatusListener() {
            @Override
            public void onSuccess() {
                Utils.updateLog(requireContext(), "createLiveChatTicket success");
            }

            @Override
            public void onError(StringeeError stringeeError) {
                super.onError(stringeeError);
                Utils.updateLog(requireContext(), "createLiveChatTicket error:" + stringeeError.getMessage());
            }
        });
    }

    private void showListConversation(List<Conversation> conversations) {
        ListStringeeObjectFragment listConversationFragment = (ListStringeeObjectFragment) getParentFragmentManager().findFragmentByTag("ListStringeeObjectFragment");
        if (listConversationFragment != null) {
            listConversationFragment.dismiss();
        }
        listConversationFragment = new ListStringeeObjectFragment(conversations);
        listConversationFragment.show(getParentFragmentManager().beginTransaction(), "ListStringeeObjectFragment");
    }

    private void showListChatRequest(List<ChatRequest> chatRequests) {
        ListStringeeObjectFragment listConversationFragment = (ListStringeeObjectFragment) getParentFragmentManager().findFragmentByTag("ListStringeeObjectFragment");
        if (listConversationFragment != null) {
            listConversationFragment.dismiss();
        }
        listConversationFragment = new ListStringeeObjectFragment(chatRequests);
        listConversationFragment.show(getParentFragmentManager().beginTransaction(), "ListStringeeObjectFragment");
    }
}
