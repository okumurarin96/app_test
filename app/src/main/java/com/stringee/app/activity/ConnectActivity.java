package com.stringee.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stringee.app.R.id;
import com.stringee.app.R.layout;
import com.stringee.app.adapter.SocketAddressAdapter;
import com.stringee.app.common.Constant;
import com.stringee.app.common.PrefUtils;
import com.stringee.app.common.Utils;
import com.stringee.app.dialog_fragment.SocketAddressesFragment;
import com.stringee.app.model.ServerAddress;
import com.stringee.messaging.listeners.CallbackListener;

import java.util.ArrayList;
import java.util.List;

public class ConnectActivity extends BaseActivity {
    private EditText etToken;
    private RecyclerView rvSocketAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_connect);

        etToken = findViewById(id.et_token);
        ImageButton btnEdit = findViewById(id.btn_edit);
        btnEdit.setOnClickListener(this);
        Button btnConnect = findViewById(id.btn_connect);
        btnConnect.setOnClickListener(this);

        List<ServerAddress> serverAddresses = Utils.getListFromStringJSON(PrefUtils.getInstance(this).getString(Constant.PREF_SERVER_ADDRESS, null), ServerAddress.class);
        if (Utils.isListEmpty(serverAddresses)) {
            serverAddresses = new ArrayList<>();
        }
        rvSocketAddress = findViewById(id.rv_server_address);
        rvSocketAddress.setLayoutManager(new LinearLayoutManager(this));
        rvSocketAddress.setHasFixedSize(true);

        initServerAdapter(serverAddresses);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int vId = view.getId();
        if (vId == id.btn_edit) {
            SocketAddressesFragment socketAddressesFragment = (SocketAddressesFragment) getSupportFragmentManager().findFragmentByTag("SocketAddressesFragment");
            if (socketAddressesFragment != null) {
                socketAddressesFragment.dismiss();
            }

            socketAddressesFragment = new SocketAddressesFragment();
            socketAddressesFragment.setOnSelectServerAddress(new CallbackListener<List<ServerAddress>>() {
                @Override
                public void onSuccess(List<ServerAddress> serverAddresses) {
                    initServerAdapter(serverAddresses);
                }
            });
            socketAddressesFragment.show(getSupportFragmentManager().beginTransaction(), "SocketAddressesFragment");
        } else if (vId == id.btn_connect) {
            if (Utils.isTextEmpty(etToken.getText())) {
                Utils.reportMessage(this, "Token cannot empty");
                return;
            }
            PrefUtils.getInstance(this).getString(Constant.PREF_TOKEN, etToken.getText().toString());
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    private void initServerAdapter(List<ServerAddress> serverAddresses) {
        List<ServerAddress> selectedServerAddresses = new ArrayList<>();
        for (ServerAddress serverAddress : serverAddresses) {
            if (serverAddress.isSelected()) {
                selectedServerAddresses.add(serverAddress);
            }
        }
        SocketAddressAdapter adapter = new SocketAddressAdapter(this, selectedServerAddresses);
        adapter.setSelectable(false);
        rvSocketAddress.setAdapter(adapter);
    }
}