package com.stringee.app.dialog_fragment;

import android.os.Bundle;
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
import com.stringee.app.adapter.SocketAddressAdapter;
import com.stringee.app.common.Constant;
import com.stringee.app.common.PrefUtils;
import com.stringee.app.common.Utils;
import com.stringee.app.listener.ItemClickListener;
import com.stringee.app.model.ServerAddress;
import com.stringee.common.SocketAddress;
import com.stringee.messaging.listeners.CallbackListener;

import java.util.ArrayList;
import java.util.List;

public class SocketAddressesFragment extends BaseBottomSheetDialogFragment {
    private RecyclerView rvSocketAddress;
    private ImageButton btnAdd;
    private TextInputEditText etIp;
    private TextInputEditText etPort;
    private View vAdd;
    private SocketAddressAdapter adapter;
    private List<ServerAddress> serverAddresses = new ArrayList<>();
    private boolean isAdd = false;
    private CallbackListener<List<ServerAddress>> listener;

    public void setCallBack(CallbackListener<List<ServerAddress>> listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layout.fragment_socket_address, container);

        ImageButton btnBack = view.findViewById(id.btn_back);
        btnBack.setOnClickListener(this);
        btnAdd = view.findViewById(id.btn_add);
        btnAdd.setOnClickListener(this);
        ImageButton btnDone = view.findViewById(id.btn_done);
        btnDone.setOnClickListener(this);

        vAdd = view.findViewById(id.v_add);
        etIp = view.findViewById(id.et_ip);
        etPort = view.findViewById(id.et_port);

        serverAddresses = Utils.getListFromStringJSON(PrefUtils.getInstance(requireContext()).getString(Constant.PREF_SERVER_ADDRESS, null), ServerAddress.class);
        if (Utils.isListEmpty(serverAddresses)) {
            serverAddresses = new ArrayList<>();
            serverAddresses.add(new ServerAddress(new SocketAddress("test3.stringee.com", 9879)));
            serverAddresses.add(new ServerAddress(new SocketAddress("test217.stringee.com", 39879)));
        }

        rvSocketAddress = view.findViewById(id.rv_server_address);
        rvSocketAddress.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvSocketAddress.setHasFixedSize(true);

        adapter = new SocketAddressAdapter(requireActivity(), serverAddresses);
        adapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                ServerAddress serverAddress = serverAddresses.get(position);
                serverAddress.setSelected(!serverAddress.isSelected());
                adapter.notifyItemChanged(position);
            }

            @Override
            public void onLongClick(View view, int position) {
                super.onLongClick(view, position);
                PopupMenu popupMenu = new PopupMenu(requireContext(), view);
                popupMenu.setGravity(android.view.Gravity.END);
                popupMenu.getMenuInflater().inflate(menu.menu_delete, popupMenu.getMenu());
                popupMenu.setForceShowIcon(true);
                popupMenu.setOnMenuItemClickListener(item -> {
                            int itemId = item.getItemId();
                            if (itemId == id.menu_delete) {
                                serverAddresses.remove(position);
                                adapter.notifyItemRemoved(position);
                                saveServerAddress();
                                return true;
                            }
                            return false;
                        }
                );
                popupMenu.show();
            }
        });
        adapter.setSelectable(true);
        rvSocketAddress.setAdapter(adapter);

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
                if (Utils.isStringEmpty(etIp.getText()) || Utils.isStringEmpty(etPort.getText())) {
                    Utils.reportMessage(requireContext(), "Ip or port cannot empty");
                    return;
                }
                serverAddresses.add(new ServerAddress(new SocketAddress(etIp.getText().toString(), Integer.parseInt(etPort.getText().toString()))));
                adapter.notifyItemInserted(serverAddresses.size() - 1);
                etIp.setText("");
                etPort.setText("");
                saveServerAddress();
                isAdd = false;
                updateView();
            } else {
                if (listener != null) {
                    saveServerAddress();
                    listener.onSuccess(serverAddresses);
                }
                dismiss();
            }
        } else if (vId == id.btn_back) {
            if (isAdd) {
                etIp.setText("");
                etPort.setText("");
                isAdd = false;
                updateView();
            } else {
                dismiss();
            }
        }
    }

    private void updateView() {
        vAdd.setVisibility(isAdd ? View.VISIBLE : View.GONE);
        rvSocketAddress.setVisibility(isAdd ? View.GONE : View.VISIBLE);
        btnAdd.setVisibility(isAdd ? View.GONE : View.VISIBLE);
    }

    private void saveServerAddress() {
        PrefUtils.getInstance(requireContext()).putString(Constant.PREF_SERVER_ADDRESS, Utils.convertObjectToStringJSON(serverAddresses));
    }
}
