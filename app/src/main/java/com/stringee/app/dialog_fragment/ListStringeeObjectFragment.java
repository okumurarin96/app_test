package com.stringee.app.dialog_fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stringee.app.R.id;
import com.stringee.app.R.layout;
import com.stringee.app.R.menu;
import com.stringee.app.activity.MainActivity;
import com.stringee.app.adapter.StringeeObjectAdapter;
import com.stringee.app.common.Common;
import com.stringee.app.common.Utils;
import com.stringee.app.listener.ItemClickListener;
import com.stringee.exception.StringeeError;
import com.stringee.listener.StatusListener;
import com.stringee.messaging.ChatRequest;
import com.stringee.messaging.Conversation;
import com.stringee.messaging.Message;
import com.stringee.messaging.listeners.CallbackListener;

import java.util.ArrayList;
import java.util.List;

public class ListStringeeObjectFragment<T> extends BaseDialogFragment {
    private List<T> objectList = new ArrayList<>();
    private StringeeObjectAdapter adapter;

    public ListStringeeObjectFragment(List<T> objectList) {
        this.objectList = objectList;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(false);
        View view = getLayoutInflater().inflate(layout.fragment_list_stringee_object, null);
        ImageButton btnBack = view.findViewById(id.btn_back);
        btnBack.setOnClickListener(this);

        RecyclerView rvConversation = view.findViewById(id.rv_conversation);
        rvConversation.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvConversation.setHasFixedSize(true);

        adapter = new StringeeObjectAdapter(requireContext(), objectList);
        adapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                super.onClick(view, position);
                T object = objectList.get(position);
                if (object instanceof Conversation) {
                    Utils.updateLog(requireContext(), "selected conversation: " + ((Conversation) object).getId());
                    ((MainActivity) requireActivity()).showObjectActionFragment(object);
                } else if (object instanceof ChatRequest) {
                    Utils.updateLog(requireContext(), "selected chat request: " + ((ChatRequest) object).getConvId());
                    ((MainActivity) requireActivity()).showObjectActionFragment(object);
                }
                dismiss();
            }

            @Override
            public void onLongClick(View view, int position) {
                super.onLongClick(view, position);
                T object = objectList.get(position);
                if (object instanceof Message) {
                    Message message = (Message) object;
                    PopupMenu popupMenu = new PopupMenu(requireContext(), view);
                    popupMenu.setGravity(Gravity.END);
                    popupMenu.getMenuInflater().inflate(menu.menu_message, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(item -> {
                                int itemId = item.getItemId();
                                if (!Common.client.isConnected()) {
                                    return false;
                                }
                                if (itemId == id.menu_mark_read) {
                                    message.markAsRead(Common.client, new StatusListener() {
                                        @Override
                                        public void onSuccess() {
                                            dismiss();
                                            Utils.updateLog(requireContext(), "markAsRead success");
                                        }

                                        @Override
                                        public void onError(StringeeError stringeeError) {
                                            super.onError(stringeeError);
                                            Utils.updateLog(requireContext(), "markAsRead error:" + stringeeError.getMessage());
                                        }
                                    });
                                    return true;
                                } else if (itemId == id.menu_pin) {
                                    message.pinOrUnpin(Common.client, true, new StatusListener() {
                                        @Override
                                        public void onSuccess() {
                                            dismiss();
                                            Utils.updateLog(requireContext(), "pin success");
                                        }

                                        @Override
                                        public void onError(StringeeError stringeeError) {
                                            super.onError(stringeeError);
                                            Utils.updateLog(requireContext(), "pin error:" + stringeeError.getMessage());
                                        }
                                    });
                                    return true;
                                } else if (itemId == id.menu_unpin) {
                                    message.pinOrUnpin(Common.client, false, new StatusListener() {
                                        @Override
                                        public void onSuccess() {
                                            dismiss();
                                            Utils.updateLog(requireContext(), "unpin success");
                                        }

                                        @Override
                                        public void onError(StringeeError stringeeError) {
                                            super.onError(stringeeError);
                                            Utils.updateLog(requireContext(), "unpin error:" + stringeeError.getMessage());
                                        }
                                    });
                                    return true;
                                } else if (itemId == id.menu_edit) {
                                    InputSingleStringFragment fragment = (InputSingleStringFragment) getParentFragmentManager().findFragmentByTag("InputSingleStringFragment");
                                    if (fragment != null) {
                                        fragment.dismiss();
                                    }
                                    fragment = new InputSingleStringFragment("Edit message", "New content");
                                    fragment.setCallBack(new CallbackListener<String>() {
                                        @Override
                                        public void onSuccess(String content) {
                                            if (!Common.client.isConnected()) {
                                                return;
                                            }
                                            message.edit(Common.client, content, new StatusListener() {
                                                @Override
                                                public void onSuccess() {
                                                    dismiss();
                                                    Utils.updateLog(requireContext(), "edit success");
                                                }

                                                @Override
                                                public void onError(StringeeError stringeeError) {
                                                    super.onError(stringeeError);
                                                    Utils.updateLog(requireContext(), "edit error:" + stringeeError.getMessage());
                                                }
                                            });
                                        }
                                    });
                                    fragment.show(getParentFragmentManager().beginTransaction(), "InputSingleStringFragment");
                                    return true;
                                }
                                return false;
                            }
                    );
                    popupMenu.show();
                }
            }
        });
        rvConversation.setAdapter(adapter);
        return new Builder(requireActivity()).setView(view).create();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == id.btn_back) {
            dismiss();
        }
    }
}
