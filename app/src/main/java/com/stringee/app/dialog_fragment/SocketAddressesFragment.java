package com.stringee.app.dialog_fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stringee.app.R.id;
import com.stringee.app.R.layout;
import com.stringee.app.adapter.SocketAddressAdapter;
import com.stringee.app.common.Constant;
import com.stringee.app.common.PrefUtils;
import com.stringee.app.common.Utils;
import com.stringee.app.model.ServerAddress;
import com.stringee.common.SocketAddress;

import java.util.ArrayList;
import java.util.List;

public class SocketAddressesFragment extends com.stringee.app.dialog_fragment.BaseBottomSheetDialogFragment {
    private RecyclerView rvSocketAddress;
    private View vAdd;
    private SocketAddressAdapter adapter;
    private List<ServerAddress> serverAddresses = new ArrayList<>();

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layout.fragment_socket_address, null);

        ImageButton btnBack = view.findViewById(id.btn_back);
        btnBack.setOnClickListener(this);
        ImageButton btnAdd = view.findViewById(id.btn_add);
        btnAdd.setOnClickListener(this);
        ImageButton btnDone = view.findViewById(id.btn_done);
        btnDone.setOnClickListener(this);

        vAdd = view.findViewById(id.v_add);

        serverAddresses = Utils.getListFromStringJSON(PrefUtils.getInstance(requireContext()).getString(Constant.PREF_SOCKET_ADDRESS, null), ServerAddress.class);
        if (Utils.isListEmpty(serverAddresses)) {
            serverAddresses = new ArrayList<>();
            serverAddresses.add(new ServerAddress(new SocketAddress("test3.stringee.com", 9879)));
            serverAddresses.add(new ServerAddress(new SocketAddress("test217.stringee.com", 39879)));
        }

        rvSocketAddress = view.findViewById(id.rv_socket_address);
        rvSocketAddress.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvSocketAddress.setHasFixedSize(true);

        adapter = new SocketAddressAdapter(requireActivity(), serverAddresses);
        adapter.setOnItemClickListener((view1, position) -> {
            ServerAddress serverAddress = serverAddresses.get(position);
            serverAddress.setSelected(!serverAddress.isSelected());
        });
        rvSocketAddress.setAdapter(adapter);

        return view;
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == id.btn_add) {
            vAdd.setVisibility(android.view.View.VISIBLE);
            rvSocketAddress.setVisibility(android.view.View.GONE);
        } else if (vId == id.btn_done) {
            vAdd.setVisibility(android.view.View.GONE);
            rvSocketAddress.setVisibility(android.view.View.VISIBLE);
        } else if (vId == id.btn_back) {

        }
    }

    private void saveAddress() {
        PrefUtils.getInstance(requireContext()).putString(Constant.PREF_SOCKET_ADDRESS, Utils.convertObjectToStringJSON(serverAddresses));
    }

    private void saveSelectedAddress() {
        List<ServerAddress> selectedServerAddresses = new ArrayList<>();
        for (ServerAddress serverAddress : serverAddresses) {
            if (serverAddress.isSelected()) {
                selectedServerAddresses.add(serverAddress);
            }
        }
        PrefUtils.getInstance(requireContext()).putString(Constant.PREF_SELECTED_SOCKET_ADDRESS, Utils.convertObjectToStringJSON(selectedServerAddresses));
    }
}
