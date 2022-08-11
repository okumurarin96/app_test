package com.stringee.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.stringee.app.R.id;
import com.stringee.app.R.layout;
import com.stringee.app.common.Utils;
import com.stringee.app.dialog_fragment.SocketAddressesFragment;
import com.stringee.common.SocketAddress;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ConnectActivity extends BaseActivity {
    private EditText etToken;
    private View vAddress;
    private Button btnConnect;
    private List<SocketAddress> socketAddressList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_connect);

        etToken = findViewById(id.et_token);
        vAddress = findViewById(id.v_address);
        vAddress.setOnClickListener(this);
        btnConnect = findViewById(id.btn_connect);
        btnConnect.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int vId = view.getId();
        if (vId == id.v_address) {
            SocketAddressesFragment socketAddressesFragment = (SocketAddressesFragment) getSupportFragmentManager().findFragmentByTag("SocketAddressesFragment");
            if (socketAddressesFragment != null) {
                socketAddressesFragment.dismiss();
            }

            socketAddressesFragment = new SocketAddressesFragment();
            socketAddressesFragment.show(getSupportFragmentManager().beginTransaction(), "SocketAddressesFragment");
        } else if (vId == id.btn_connect) {
            if (Utils.isTextEmpty(etToken.getText().toString())) {
                Utils.reportMessage(this, "Token cannot ");
                return;
            }
            Intent intent = new Intent(this, com.stringee.app.activity.MainActivity.class);
            intent.putExtra("token", etToken.getText().toString());
            if (!Utils.isListEmpty(socketAddressList)) {
                intent.putExtra("socketAddress", (Serializable) socketAddressList);
            }
            startActivity(intent);
        }
    }
}