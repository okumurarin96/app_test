package com.stringee.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.stringee.app.R.id;
import com.stringee.app.R.layout;
import com.stringee.app.activity.MainActivity;
import com.stringee.app.common.Common;
import com.stringee.app.common.Utils;
import com.stringee.app.dialog_fragment.AddRemoveParticipantFragment;
import com.stringee.app.dialog_fragment.GetMessageOptionFragment;
import com.stringee.app.dialog_fragment.GetMessagesFragment;
import com.stringee.app.dialog_fragment.InputSingleStringFragment;
import com.stringee.app.dialog_fragment.ListStringeeObjectFragment;
import com.stringee.app.dialog_fragment.SendChatTranscriptFragment;
import com.stringee.app.dialog_fragment.SetRoleFragment;
import com.stringee.app.model.UserId;
import com.stringee.exception.StringeeError;
import com.stringee.listener.StatusListener;
import com.stringee.messaging.ChatRequest;
import com.stringee.messaging.Conversation;
import com.stringee.messaging.Message;
import com.stringee.messaging.Message.Type;
import com.stringee.messaging.User;
import com.stringee.messaging.User.Role;
import com.stringee.messaging.listeners.CallbackListener;

import java.util.List;
import java.util.Map;

public class ObjectActionFragment<T> extends BaseFragment {
    private Conversation conversation;
    private ChatRequest chatRequest;
    private View vConversation;
    private View vChatRequest;
    private View vSupport;
    private View vAgent;
    private Button btnEndChat;
    private Button btnContinue;

    public ObjectActionFragment(T object) {
        if (object instanceof Conversation) {
            this.conversation = (Conversation) object;
        }
        if (object instanceof ChatRequest) {
            this.chatRequest = (ChatRequest) object;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layout.fragment_tab_chat, container, false);

        vConversation = view.findViewById(id.v_conversation);
        vChatRequest = view.findViewById(id.v_chat_request);
        vSupport = view.findViewById(id.v_support);
        vAgent = view.findViewById(id.v_agent);

        view.findViewById(id.btn_delete).setOnClickListener(this);
        view.findViewById(id.btn_send_msg).setOnClickListener(this);
        view.findViewById(id.btn_get_local_msg).setOnClickListener(this);
        view.findViewById(id.btn_get_last_msg).setOnClickListener(this);
        view.findViewById(id.btn_get_msg_after).setOnClickListener(this);
        view.findViewById(id.btn_get_msg_before).setOnClickListener(this);
        view.findViewById(id.btn_add_participant).setOnClickListener(this);
        view.findViewById(id.btn_remove_participant).setOnClickListener(this);
        view.findViewById(id.btn_delete_msg).setOnClickListener(this);
        view.findViewById(id.btn_update_conv).setOnClickListener(this);
        view.findViewById(id.btn_set_role).setOnClickListener(this);
        view.findViewById(id.btn_get_msg).setOnClickListener(this);
        view.findViewById(id.btn_begin_typing).setOnClickListener(this);
        view.findViewById(id.btn_end_typing).setOnClickListener(this);
        view.findViewById(id.btn_get_attachment_msg).setOnClickListener(this);
        view.findViewById(id.btn_mark_all_read).setOnClickListener(this);
        btnEndChat = view.findViewById(id.btn_end_chat);
        btnEndChat.setOnClickListener(this);
        view.findViewById(id.btn_transfer).setOnClickListener(this);
        view.findViewById(id.btn_send_transcript).setOnClickListener(this);
        btnContinue = view.findViewById(id.btn_continue);
        btnContinue.setOnClickListener(this);
        view.findViewById(id.btn_accept).setOnClickListener(this);
        view.findViewById(id.btn_reject).setOnClickListener(this);

        if (chatRequest != null) {
            vConversation.setVisibility(View.GONE);
            vChatRequest.setVisibility(View.VISIBLE);
        } else if (conversation != null) {
            if (conversation.getChannelType() != Conversation.ChannelType.NORMAL) {
                vSupport.setVisibility(View.VISIBLE);
                if (!Common.isCustomer) {
                    vAgent.setVisibility(View.VISIBLE);
                    btnContinue.setVisibility(conversation.isEnded() ? View.VISIBLE : View.GONE);
                    btnEndChat.setVisibility(conversation.isEnded() ? View.GONE : View.VISIBLE);
                }
            }
        }

        return view;
    }


    @Override
    public void onClick(View view) {
        int vId = view.getId();
        if (!Common.client.isConnected()) {
            return;
        }
        if (vId == id.btn_delete) {
            conversation.delete(Common.client, new StatusListener() {
                @Override
                public void onSuccess() {
                    Utils.updateLog(requireContext(), "delete success");
                    ((MainActivity) requireActivity()).removeFragment("ObjectActionFragment");
                }

                @Override
                public void onError(StringeeError stringeeError) {
                    super.onError(stringeeError);
                    Utils.updateLog(requireContext(), "delete error:" + stringeeError.getMessage());
                }
            });
        } else if (vId == id.btn_send_msg) {
            InputSingleStringFragment fragment = (InputSingleStringFragment) getParentFragmentManager().findFragmentByTag("InputSingleStringFragment");
            if (fragment != null) {
                fragment.dismiss();
            }
            fragment = new InputSingleStringFragment("Send message", "Message");
            fragment.setCallBack(new CallbackListener<String>() {
                @Override
                public void onSuccess(String msg) {
                    if (!Common.client.isConnected()) {
                        return;
                    }
                    Message message = new Message(msg);
                    conversation.sendMessage(Common.client, message, new StatusListener() {
                        @Override
                        public void onSuccess() {
                            Utils.updateLog(requireContext(), "sendMessage success");
                        }

                        @Override
                        public void onError(StringeeError stringeeError) {
                            super.onError(stringeeError);
                            Utils.updateLog(requireContext(), "sendMessage error:" + stringeeError.getMessage());
                        }
                    });
                }
            });
            fragment.show(getParentFragmentManager().beginTransaction(), "InputSingleStringFragment");
        } else if (vId == id.btn_get_local_msg) {
            GetMessageOptionFragment fragment = (GetMessageOptionFragment) getParentFragmentManager().findFragmentByTag("GetMessageOptionFragment");
            if (fragment != null) {
                fragment.dismiss();
            }
            fragment = new GetMessageOptionFragment();
            fragment.setCallBack(false, new CallbackListener<Map>() {
                @Override
                public void onSuccess(Map map) {
                    if (!Common.client.isConnected()) {
                        return;
                    }
                    int count = Integer.parseInt((String) map.get("count"));
                    conversation.getLocalMessages(Common.client, count, new CallbackListener<List<Message>>() {
                        @Override
                        public void onSuccess(List<Message> messages) {
                            showListMessage(messages);
                            Utils.updateLog(requireContext(), "getLocalMessages success: " + Utils.convertObjectToStringJSON(messages));
                        }

                        @Override
                        public void onError(StringeeError stringeeError) {
                            super.onError(stringeeError);
                            Utils.updateLog(requireContext(), "getLocalMessages error:" + stringeeError.getMessage());
                        }
                    });
                }
            });
            fragment.show(getParentFragmentManager().beginTransaction(), "GetMessageOptionFragment");
        } else if (vId == id.btn_get_last_msg) {
            GetMessageOptionFragment fragment = (GetMessageOptionFragment) getParentFragmentManager().findFragmentByTag("GetMessageOptionFragment");
            if (fragment != null) {
                fragment.dismiss();
            }
            fragment = new GetMessageOptionFragment();
            fragment.setCallBack(false, new CallbackListener<Map>() {
                @Override
                public void onSuccess(Map map) {
                    if (!Common.client.isConnected()) {
                        return;
                    }
                    int count = Integer.parseInt((String) map.get("count"));
                    conversation.getLastMessages(Common.client, count, new CallbackListener<List<Message>>() {
                        @Override
                        public void onSuccess(List<Message> messages) {
                            showListMessage(messages);
                            Utils.updateLog(requireContext(), "getLastMessages success: " + Utils.convertObjectToStringJSON(messages));
                        }

                        @Override
                        public void onError(StringeeError stringeeError) {
                            super.onError(stringeeError);
                            Utils.updateLog(requireContext(), "getLastMessages error:" + stringeeError.getMessage());
                        }
                    });
                }
            });
            fragment.show(getParentFragmentManager().beginTransaction(), "GetMessageOptionFragment");
        } else if (vId == id.btn_get_msg_after) {
            GetMessageOptionFragment fragment = (GetMessageOptionFragment) getParentFragmentManager().findFragmentByTag("GetMessageOptionFragment");
            if (fragment != null) {
                fragment.dismiss();
            }
            fragment = new GetMessageOptionFragment();
            fragment.setCallBack(true, new CallbackListener<Map>() {
                @Override
                public void onSuccess(Map map) {
                    if (!Common.client.isConnected()) {
                        return;
                    }
                    int count = Integer.parseInt((String) map.get("count"));
                    long sequence = Long.parseLong((String) map.get("sequence"));
                    conversation.getMessagesAfter(Common.client, sequence, count, new CallbackListener<List<Message>>() {
                        @Override
                        public void onSuccess(List<Message> messages) {
                            showListMessage(messages);
                            Utils.updateLog(requireContext(), "getMessagesAfter success: " + Utils.convertObjectToStringJSON(messages));
                        }

                        @Override
                        public void onError(StringeeError stringeeError) {
                            super.onError(stringeeError);
                            Utils.updateLog(requireContext(), "getMessagesAfter error:" + stringeeError.getMessage());
                        }
                    });
                }
            });
            fragment.show(getParentFragmentManager().beginTransaction(), "GetMessageOptionFragment");
        } else if (vId == id.btn_get_msg_before) {
            GetMessageOptionFragment fragment = (GetMessageOptionFragment) getParentFragmentManager().findFragmentByTag("GetMessageOptionFragment");
            if (fragment != null) {
                fragment.dismiss();
            }
            fragment = new GetMessageOptionFragment();
            fragment.setCallBack(true, new CallbackListener<Map>() {
                @Override
                public void onSuccess(Map map) {
                    if (!Common.client.isConnected()) {
                        return;
                    }
                    int count = Integer.parseInt((String) map.get("count"));
                    long sequence = Long.parseLong((String) map.get("sequence"));
                    conversation.getMessagesBefore(Common.client, sequence, count, new CallbackListener<List<Message>>() {
                        @Override
                        public void onSuccess(List<Message> messages) {
                            showListMessage(messages);
                            Utils.updateLog(requireContext(), "getMessagesBefore success: " + Utils.convertObjectToStringJSON(messages));
                        }

                        @Override
                        public void onError(StringeeError stringeeError) {
                            super.onError(stringeeError);
                            Utils.updateLog(requireContext(), "getMessagesBefore error:" + stringeeError.getMessage());
                        }
                    });
                }
            });
            fragment.show(getParentFragmentManager().beginTransaction(), "GetMessageOptionFragment");
        } else if (vId == id.btn_add_participant) {
            AddRemoveParticipantFragment fragment = (AddRemoveParticipantFragment) getParentFragmentManager().findFragmentByTag("AddRemoveParticipantFragment");
            if (fragment != null) {
                fragment.dismiss();
            }
            fragment = new AddRemoveParticipantFragment();
            fragment.setCallBack(false, conversation, new CallbackListener<List<User>>() {
                @Override
                public void onSuccess(List<User> users) {
                    if (!Common.client.isConnected()) {
                        return;
                    }
                    conversation.addParticipants(Common.client, users, new CallbackListener<List<User>>() {
                        @Override
                        public void onSuccess(List<User> participants) {
                            updateConversation(conversation.getId());
                            Utils.updateLog(requireContext(), "addParticipants success: " + Utils.convertObjectToStringJSON(participants));
                        }

                        @Override
                        public void onError(StringeeError stringeeError) {
                            super.onError(stringeeError);
                            Utils.updateLog(requireContext(), "addParticipants error:" + stringeeError.getMessage());
                        }
                    });
                }
            });
            fragment.show(getParentFragmentManager().beginTransaction(), "AddRemoveParticipantFragment");
        } else if (vId == id.btn_remove_participant) {
            AddRemoveParticipantFragment fragment = (AddRemoveParticipantFragment) getParentFragmentManager().findFragmentByTag("AddRemoveParticipantFragment");
            if (fragment != null) {
                fragment.dismiss();
            }
            fragment = new AddRemoveParticipantFragment();
            fragment.setCallBack(true, conversation, new CallbackListener<List<User>>() {
                @Override
                public void onSuccess(List<User> users) {
                    if (!Common.client.isConnected()) {
                        return;
                    }
                    conversation.removeParticipants(Common.client, users, new CallbackListener<List<User>>() {
                        @Override
                        public void onSuccess(List<User> participants) {
                            updateConversation(conversation.getId());
                            Utils.updateLog(requireContext(), "removeParticipants success: " + Utils.convertObjectToStringJSON(participants));
                        }

                        @Override
                        public void onError(StringeeError stringeeError) {
                            super.onError(stringeeError);
                            Utils.updateLog(requireContext(), "removeParticipants error:" + stringeeError.getMessage());
                        }
                    });
                }
            });
            fragment.show(getParentFragmentManager().beginTransaction(), "AddRemoveParticipantFragment");
        } else if (vId == id.btn_delete_msg) {
            InputSingleStringFragment fragment = (InputSingleStringFragment) getParentFragmentManager().findFragmentByTag("InputSingleStringFragment");
            if (fragment != null) {
                fragment.dismiss();
            }
            fragment = new InputSingleStringFragment("Delete message", "Message id");
            fragment.setCallBack(new CallbackListener<String>() {
                @Override
                public void onSuccess(String msgId) {
                    if (!Common.client.isConnected()) {
                        return;
                    }
                    conversation.getMessages(Common.client, new String[]{msgId}, new CallbackListener<List<Message>>() {
                        @Override
                        public void onSuccess(List<Message> messages) {
                            Utils.updateLog(requireContext(), "getMessages success: " + Utils.convertObjectToStringJSON(messages));
                            if (!Common.client.isConnected()) {
                                return;
                            }
                            conversation.deleteMessages(Common.client, messages, new StatusListener() {
                                @Override
                                public void onSuccess() {
                                    Utils.updateLog(requireContext(), "deleteMessages success");
                                }

                                @Override
                                public void onError(StringeeError stringeeError) {
                                    super.onError(stringeeError);
                                    Utils.updateLog(requireContext(), "sendMessage error:" + stringeeError.getMessage());
                                }
                            });
                        }

                        @Override
                        public void onError(StringeeError stringeeError) {
                            super.onError(stringeeError);
                            Utils.updateLog(requireContext(), "getMessages error:" + stringeeError.getMessage());
                        }
                    });
                }
            });
            fragment.show(getParentFragmentManager().beginTransaction(), "InputSingleStringFragment");
        } else if (vId == id.btn_update_conv) {
            InputSingleStringFragment fragment = (InputSingleStringFragment) getParentFragmentManager().findFragmentByTag("InputSingleStringFragment");
            if (fragment != null) {
                fragment.dismiss();
            }
            fragment = new InputSingleStringFragment("Update conversation", "Name");
            fragment.setCallBack(new CallbackListener<String>() {
                @Override
                public void onSuccess(String name) {
                    if (!Common.client.isConnected()) {
                        return;
                    }
                    conversation.updateConversation(Common.client, name, null, new StatusListener() {
                        @Override
                        public void onSuccess() {
                            updateConversation(conversation.getId());
                            Utils.updateLog(requireContext(), "updateConversation success");
                        }

                        @Override
                        public void onError(StringeeError stringeeError) {
                            super.onError(stringeeError);
                            Utils.updateLog(requireContext(), "updateConversation error:" + stringeeError.getMessage());
                        }
                    });
                }
            });
            fragment.show(getParentFragmentManager().beginTransaction(), "InputSingleStringFragment");
        } else if (vId == id.btn_set_role) {
            SetRoleFragment fragment = (SetRoleFragment) getParentFragmentManager().findFragmentByTag("SetRoleFragment");
            if (fragment != null) {
                fragment.dismiss();
            }
            fragment = new SetRoleFragment();
            fragment.setCallBack(conversation, new CallbackListener<UserId>() {
                @Override
                public void onSuccess(UserId userId) {
                    if (!Common.client.isConnected()) {
                        return;
                    }
                    if (userId.getRole() == Role.ADMIN) {
                        conversation.setAsAdmin(Common.client, userId.getId(), new StatusListener() {
                            @Override
                            public void onSuccess() {
                                updateConversation(conversation.getId());
                                Utils.updateLog(requireContext(), "setAsAdmin success");
                            }

                            @Override
                            public void onError(StringeeError stringeeError) {
                                super.onError(stringeeError);
                                Utils.updateLog(requireContext(), "setAsAdmin error:" + stringeeError.getMessage());
                            }
                        });
                    } else {
                        conversation.setAsMember(Common.client, userId.getId(), new StatusListener() {
                            @Override
                            public void onSuccess() {
                                updateConversation(conversation.getId());
                                Utils.updateLog(requireContext(), "setAsMember success");
                            }

                            @Override
                            public void onError(StringeeError stringeeError) {
                                super.onError(stringeeError);
                                Utils.updateLog(requireContext(), "setAsMember error:" + stringeeError.getMessage());
                            }
                        });
                    }
                }
            });
            fragment.show(getParentFragmentManager().beginTransaction(), "SetRoleFragment");
        } else if (vId == id.btn_get_msg) {
            GetMessagesFragment fragment = (GetMessagesFragment) getParentFragmentManager().findFragmentByTag("GetMessagesFragment");
            if (fragment != null) {
                fragment.dismiss();
            }
            fragment = new GetMessagesFragment();
            fragment.setCallBack(new CallbackListener<List<String>>() {
                @Override
                public void onSuccess(List<String> msgIds) {
                    if (!Common.client.isConnected()) {
                        return;
                    }
                    conversation.getMessages(Common.client, msgIds.toArray(new String[0]), new CallbackListener<List<Message>>() {
                        @Override
                        public void onSuccess(List<Message> messages) {
                            showListMessage(messages);
                            Utils.updateLog(requireContext(), "getMessages success: " + Utils.convertObjectToStringJSON(messages));
                        }

                        @Override
                        public void onError(StringeeError stringeeError) {
                            super.onError(stringeeError);
                            Utils.updateLog(requireContext(), "getMessages error:" + stringeeError.getMessage());
                        }
                    });
                }
            });
            fragment.show(getParentFragmentManager().beginTransaction(), "GetMessagesFragment");
        } else if (vId == id.btn_begin_typing) {
            conversation.beginTyping(Common.client, new StatusListener() {
                @Override
                public void onSuccess() {
                    Utils.updateLog(requireContext(), "beginTyping success");
                }

                @Override
                public void onError(StringeeError stringeeError) {
                    super.onError(stringeeError);
                    Utils.updateLog(requireContext(), "beginTyping error:" + stringeeError.getMessage());
                }
            });
        } else if (vId == id.btn_end_typing) {
            conversation.endTyping(Common.client, new StatusListener() {
                @Override
                public void onSuccess() {
                    Utils.updateLog(requireContext(), "endTyping success");
                }

                @Override
                public void onError(StringeeError stringeeError) {
                    super.onError(stringeeError);
                    Utils.updateLog(requireContext(), "endTyping error:" + stringeeError.getMessage());
                }
            });
        } else if (vId == id.btn_get_attachment_msg) {
            GetMessageOptionFragment fragment = (GetMessageOptionFragment) getParentFragmentManager().findFragmentByTag("GetMessageOptionFragment");
            if (fragment != null) {
                fragment.dismiss();
            }
            fragment = new GetMessageOptionFragment();
            fragment.setCallBack(true, new CallbackListener<Map>() {
                @Override
                public void onSuccess(Map map) {
                    if (!Common.client.isConnected()) {
                        return;
                    }
                    Type type = (Type) map.get("type");
                    int count = Integer.parseInt((String) map.get("count"));
                    int start = Integer.parseInt((String) map.get("start"));
                    conversation.getAttachmentMessages(Common.client, type, start, count, new CallbackListener<List<Message>>() {
                        @Override
                        public void onSuccess(List<Message> messages) {
                            showListMessage(messages);
                            Utils.updateLog(requireContext(), "getAttachmentMessages success: " + Utils.convertObjectToStringJSON(messages));
                        }

                        @Override
                        public void onError(StringeeError stringeeError) {
                            super.onError(stringeeError);
                            Utils.updateLog(requireContext(), "getAttachmentMessages error:" + stringeeError.getMessage());
                        }
                    });
                }
            });
            fragment.show(getParentFragmentManager().beginTransaction(), "GetMessageOptionFragment");
        } else if (vId == id.btn_mark_all_read) {
            conversation.markAllAsRead(Common.client, new StatusListener() {
                @Override
                public void onSuccess() {
                    Utils.updateLog(requireContext(), "markAllAsRead success");
                }

                @Override
                public void onError(StringeeError stringeeError) {
                    super.onError(stringeeError);
                    Utils.updateLog(requireContext(), "markAllAsRead error:" + stringeeError.getMessage());
                }
            });
        } else if (vId == id.btn_end_chat) {
            conversation.endChat(Common.client, new StatusListener() {
                @Override
                public void onSuccess() {
                    Utils.updateLog(requireContext(), "endChat success");
                    ((MainActivity) requireActivity()).removeFragment("ObjectActionFragment");
                }

                @Override
                public void onError(StringeeError stringeeError) {
                    super.onError(stringeeError);
                    Utils.updateLog(requireContext(), "endChat error:" + stringeeError.getMessage());
                }
            });
        } else if (vId == id.btn_transfer) {
            InputSingleStringFragment fragment = (InputSingleStringFragment) getParentFragmentManager().findFragmentByTag("InputSingleStringFragment");
            if (fragment != null) {
                fragment.dismiss();
            }
            fragment = new InputSingleStringFragment("Transfer to", "To");
            fragment.setCallBack(new CallbackListener<String>() {
                @Override
                public void onSuccess(String userId) {
                    if (!Common.client.isConnected()) {
                        return;
                    }
                    for (User user : conversation.getParticipants()) {
                        if (!user.getUserId().equals(Common.client.getUserId())) {
                            conversation.transferTo(Common.client, userId, user.getUserId(), user.getName(), new StatusListener() {
                                @Override
                                public void onSuccess() {
                                    Utils.updateLog(requireContext(), "transferTo success");
                                }

                                @Override
                                public void onError(StringeeError stringeeError) {
                                    super.onError(stringeeError);
                                    Utils.updateLog(requireContext(), "transferTo error:" + stringeeError.getMessage());
                                }
                            });
                            break;
                        }
                    }
                }
            });
            fragment.show(getParentFragmentManager().beginTransaction(), "InputSingleStringFragment");
        } else if (vId == id.btn_send_transcript) {
            SendChatTranscriptFragment fragment = (SendChatTranscriptFragment) getParentFragmentManager().findFragmentByTag("SendChatTranscriptFragment");
            if (fragment != null) {
                fragment.dismiss();
            }
            fragment = new SendChatTranscriptFragment();
            fragment.setCallBack(new CallbackListener<Map>() {
                @Override
                public void onSuccess(Map map) {
                    if (!Common.client.isConnected()) {
                        return;
                    }
                    String email = (String) map.get("email");
                    String domain = (String) map.get("domain");
                    conversation.sendChatTranscriptTo(Common.client, email, domain, new StatusListener() {
                        @Override
                        public void onSuccess() {
                            Utils.updateLog(requireContext(), "sendChatTranscriptTo success");
                        }

                        @Override
                        public void onError(StringeeError stringeeError) {
                            super.onError(stringeeError);
                            Utils.updateLog(requireContext(), "sendChatTranscriptTo error:" + stringeeError.getMessage());
                        }
                    });
                }
            });
            fragment.show(getParentFragmentManager().beginTransaction(), "SendChatTranscriptFragment");
        } else if (vId == id.btn_continue) {
            conversation.continueChatting(Common.client, new CallbackListener<Conversation>() {
                @Override
                public void onSuccess(Conversation conversation) {
                    Utils.updateLog(requireContext(), "continueChatting success");
                    updateConversation(conversation.getId());
                }

                @Override
                public void onError(StringeeError stringeeError) {
                    super.onError(stringeeError);
                    Utils.updateLog(requireContext(), "continueChatting error:" + stringeeError.getMessage());
                }
            });
        } else if (vId == id.btn_accept) {
            chatRequest.accept(Common.client, new CallbackListener<Conversation>() {
                @Override
                public void onSuccess(Conversation conversation) {
                    Utils.updateLog(requireContext(), "reject success");
                    updateConversation(conversation.getId());
                }

                @Override
                public void onError(StringeeError stringeeError) {
                    super.onError(stringeeError);
                    Utils.updateLog(requireContext(), "accept error:" + stringeeError.getMessage());
                }
            });
        } else if (vId == id.btn_reject) {
            chatRequest.reject(Common.client, new StatusListener() {
                @Override
                public void onSuccess() {
                    Utils.updateLog(requireContext(), "reject success");
                    ((MainActivity) requireActivity()).removeFragment("ObjectActionFragment");
                }

                @Override
                public void onError(StringeeError stringeeError) {
                    super.onError(stringeeError);
                    Utils.updateLog(requireContext(), "reject error:" + stringeeError.getMessage());
                }
            });
        }
    }

    private void updateConversation(String id) {
        Common.client.getConversationFromServer(id, new CallbackListener<Conversation>() {
            @Override
            public void onSuccess(Conversation conv) {
                conversation = conv;
                vConversation.setVisibility(View.VISIBLE);
                vChatRequest.setVisibility(View.GONE);
                if (conversation.getChannelType() != Conversation.ChannelType.NORMAL) {
                    vSupport.setVisibility(View.VISIBLE);
                    if (!Common.isCustomer) {
                        vAgent.setVisibility(View.VISIBLE);
                        btnContinue.setVisibility(conversation.isEnded() ? View.VISIBLE : View.GONE);
                        btnEndChat.setVisibility(conversation.isEnded() ? View.GONE : View.VISIBLE);
                    }
                }
            }
        });
    }

    private void showListMessage(List<Message> message) {
        ListStringeeObjectFragment fragment = (ListStringeeObjectFragment) getParentFragmentManager().findFragmentByTag("ListStringeeObjectFragment");
        if (fragment != null) {
            fragment.dismiss();
        }
        fragment = new ListStringeeObjectFragment(message);
        fragment.show(getParentFragmentManager().beginTransaction(), "ListStringeeObjectFragment");
    }
}
