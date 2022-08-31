package com.stringee.app.dialog_fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.stringee.app.R.id;
import com.stringee.app.R.layout;
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

public class ConnectAsCustomerFragment extends BaseBottomSheetDialogFragment {
    private RecyclerView rvSocketAddress;
    private TextInputEditText etWidgetKey;
    private TextInputEditText etName;
    private TextInputEditText etEmail;
    private TextInputEditText etBaseAPIUrl;
    private TextInputEditText etStringeeXUrl;
    private List<ServerAddress> serverAddresses;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layout.fragment_connect_as_customer, container, false);

        etWidgetKey = view.findViewById(id.et_widget_key);
        etName = view.findViewById(id.et_name);
        etEmail = view.findViewById(id.et_email);
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

        etWidgetKey.setText(PrefUtils.getInstance(requireContext()).getString(Constant.PREF_WIDGET_KEY, ""));
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
            socketAddressesFragment.setCallBack(new com.stringee.messaging.listeners.CallbackListener<List<ServerAddress>>() {
                @Override
                public void onSuccess(List<ServerAddress> serverAddresses) {
                    initServerAdapter(serverAddresses);
                }
            });
            socketAddressesFragment.show(getParentFragmentManager().beginTransaction(), "SocketAddressesFragment");
        } else if (vId == id.btn_done) {
            if (Utils.isStringEmpty(etWidgetKey.getText())) {
                Utils.reportMessage(requireContext(), "Widget key cannot empty");
                return;
            }
            if (Utils.isStringEmpty(etBaseAPIUrl.getText())) {
                PrefUtils.getInstance(requireContext()).putString(Constant.PREF_BASE_API_URL, etBaseAPIUrl.getText().toString().trim());
            }
            if (Utils.isStringEmpty(etStringeeXUrl.getText())) {
                PrefUtils.getInstance(requireContext()).putString(Constant.PREF_STRINGEEX_BASE_URL, etStringeeXUrl.getText().toString().trim());
            }
            String widgetKey = etWidgetKey.getText().toString();
            PrefUtils.getInstance(requireContext()).putString(Constant.PREF_WIDGET_KEY, widgetKey);

            Common.client.getLiveChatToken(widgetKey, Utils.getEditableText(etName.getText(), true), Utils.getEditableText(etEmail.getText(), true), new CallbackListener<String>() {
                @Override
                public void onSuccess(String token) {
                    Utils.updateLog(requireContext(), "getChatLiveChatToken success");
                    PrefUtils.getInstance(requireContext()).putString(Constant.PREF_TOKEN, token);
                    if (!Utils.isListEmpty(serverAddresses)) {
                        List<SocketAddress> socketAddresses = new ArrayList<>();
                        for (ServerAddress serverAddress : serverAddresses) {
                            if (serverAddress.isSelected()) {
                                socketAddresses.add(serverAddress.getSocketAddress());
                            }
                        }
                        Common.client.setHost(socketAddresses);
                    }
                    String baseAPIUrl = PrefUtils.getInstance(requireContext()).getString(Constant.PREF_BASE_API_URL, "");
                    if (!Utils.isStringEmpty(baseAPIUrl)) {
                        Common.client.setBaseAPIUrl(baseAPIUrl);
                    }
                    String stringeeXBaseUrl = PrefUtils.getInstance(requireContext()).getString(Constant.PREF_STRINGEEX_BASE_URL, "");
                    if (!Utils.isStringEmpty(stringeeXBaseUrl)) {
                        Common.client.setStringeeXBaseUrl(stringeeXBaseUrl);
                    }
                    Common.client.connect(token);
                    Common.isCustomer = true;
                    Utils.updateLog(requireContext(), "connect success");
                }

                @Override
                public void onError(com.stringee.exception.StringeeError stringeeError) {
                    super.onError(stringeeError);
                    Utils.updateLog(requireContext(), "getChatLiveChatToken error:" + stringeeError.getMessage());
                }
            });
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
