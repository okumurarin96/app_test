package com.stringee.app.dialog_fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.stringee.app.R.id;
import com.stringee.app.R.layout;
import com.stringee.app.activity.MainActivity;
import com.stringee.app.adapter.SocketAddressAdapter;
import com.stringee.app.common.Common;
import com.stringee.app.common.Constant;
import com.stringee.app.common.PrefUtils;
import com.stringee.app.common.Utils;
import com.stringee.app.model.ServerAddress;
import com.stringee.common.SocketAddress;
import com.stringee.messaging.listeners.CallbackListener;

import java.util.ArrayList;
import java.util.List;

public class ConnectFragment extends BaseBottomSheetDialogFragment {
    private EditText etToken;
    private RecyclerView rvSocketAddress;
    private TextInputEditText etBaseAPIUrl;
    private TextInputEditText etStringeeXUrl;
    private List<ServerAddress> serverAddresses;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layout.fragment_connect, container, false);

        etToken = view.findViewById(id.et_token);
        etBaseAPIUrl = view.findViewById(id.et_base_api_url);
        etStringeeXUrl = view.findViewById(id.et_stringeex_url);
        ImageButton btnBack = view.findViewById(id.btn_back);
        btnBack.setOnClickListener(this);
        ImageButton btnEdit = view.findViewById(id.btn_edit);
        btnEdit.setOnClickListener(this);
        ImageButton btnDone = view.findViewById(id.btn_done);
        btnDone.setOnClickListener(this);

        serverAddresses = Utils.getListFromStringJSON(PrefUtils.getInstance(requireContext()).getString(Constant.PREF_SERVER_ADDRESS, null), ServerAddress.class);
        if (Utils.isListEmpty(serverAddresses)) {
            serverAddresses = new ArrayList<>();
        }
        rvSocketAddress = view.findViewById(id.rv_server_address);
        rvSocketAddress.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvSocketAddress.setHasFixedSize(true);

        initServerAdapter(serverAddresses);

        etToken.setText(PrefUtils.getInstance(requireContext()).getString(Constant.PREF_TOKEN, ""));
        etBaseAPIUrl.setText(PrefUtils.getInstance(requireContext()).getString(Constant.PREF_BASE_API_URL, ""));
        etStringeeXUrl.setText(PrefUtils.getInstance(requireContext()).getString(Constant.PREF_STRINGEEX_BASE_URL, ""));

        return view;
    }

    @Override
    public void onClick(View view) {
        int vId = view.getId();
        if (vId == id.btn_back) {
            dismiss();
        } else if (vId == id.btn_edit) {
            SocketAddressesFragment socketAddressesFragment = (SocketAddressesFragment) getParentFragmentManager().findFragmentByTag("SocketAddressesFragment");
            if (socketAddressesFragment != null) {
                socketAddressesFragment.dismiss();
            }

            socketAddressesFragment = new SocketAddressesFragment();
            socketAddressesFragment.setCallBack(new CallbackListener<List<ServerAddress>>() {
                @Override
                public void onSuccess(List<ServerAddress> serverAddresses) {
                    initServerAdapter(serverAddresses);
                }
            });
            socketAddressesFragment.show(getParentFragmentManager().beginTransaction(), "SocketAddressesFragment");
        } else if (vId == id.btn_done) {
            if (Utils.isStringEmpty(etToken.getText())) {
                Utils.reportMessage(requireContext(), "Token cannot empty");
                return;
            }
            if (Utils.isStringEmpty(etBaseAPIUrl.getText())) {
                PrefUtils.getInstance(requireContext()).putString(Constant.PREF_BASE_API_URL, etBaseAPIUrl.getText().toString().trim());
            }
            if (Utils.isStringEmpty(etStringeeXUrl.getText())) {
                PrefUtils.getInstance(requireContext()).putString(Constant.PREF_STRINGEEX_BASE_URL, etStringeeXUrl.getText().toString().trim());
            }
            PrefUtils.getInstance(requireContext()).putString(Constant.PREF_TOKEN, etToken.getText().toString().trim());

            if (!Utils.isListEmpty(serverAddresses)) {
                List<SocketAddress> socketAddresses = new ArrayList<>();
                for (ServerAddress serverAddress : serverAddresses) {
                    if (serverAddress.isSelected()) {
                        socketAddresses.add(serverAddress.getSocketAddress());
                    }
                }
                Common.client.setHost(socketAddresses);
            }
            if (!Utils.isStringEmpty(etBaseAPIUrl.getText())) {
                Common.client.setBaseAPIUrl(etBaseAPIUrl.getText().toString().trim());
            }
            if (!Utils.isStringEmpty(etStringeeXUrl.getText())) {
                Common.client.setStringeeXBaseUrl(etStringeeXUrl.getText().toString().trim());
            }
            Common.client.connect(etToken.getText().toString().trim());
            Common.isCustomer = false;
            Utils.updateLog(requireContext(), "connect success");

            dismiss();
        }
    }

    private void initServerAdapter(List<ServerAddress> serverAddresses) {
        this.serverAddresses = serverAddresses;
        SocketAddressAdapter adapter = new SocketAddressAdapter(requireContext(), serverAddresses);
        adapter.setSelectable(false);
        rvSocketAddress.setAdapter(adapter);
    }
}
