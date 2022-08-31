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
import com.stringee.app.dialog_fragment.ConnectAsCustomerFragment;
import com.stringee.app.dialog_fragment.ConnectFragment;
import com.stringee.app.dialog_fragment.CustomMessageFragment;
import com.stringee.app.dialog_fragment.PackageNamesFragment;
import com.stringee.app.dialog_fragment.UpdateUserInfoFragment;
import com.stringee.app.dialog_fragment.UserIdFragment;
import com.stringee.app.model.PackageName;
import com.stringee.app.model.UserId;
import com.stringee.exception.StringeeError;
import com.stringee.listener.StatusListener;
import com.stringee.messaging.User;
import com.stringee.messaging.UserInfo;
import com.stringee.messaging.listeners.CallbackListener;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class TabGeneralFragment extends BaseFragment {
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layout.fragment_tab_general, container, false);

        view.findViewById(id.btn_connect).setOnClickListener(this);
        view.findViewById(id.btn_connect_customer).setOnClickListener(this);
        view.findViewById(id.btn_disconnect).setOnClickListener(this);
        view.findViewById(id.btn_register_push).setOnClickListener(this);
        view.findViewById(id.btn_register_push_and_delete).setOnClickListener(this);
        view.findViewById(id.btn_send_custom_message).setOnClickListener(this);
        view.findViewById(id.btn_un_register).setOnClickListener(this);
        view.findViewById(id.btn_update_user).setOnClickListener(this);
        view.findViewById(id.btn_get_user_info).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        int vId = view.getId();
        if (vId == id.btn_connect) {
            ConnectFragment connectFragment = (ConnectFragment) getParentFragmentManager().findFragmentByTag("ConnectFragment");
            if (connectFragment != null) {
                connectFragment.dismiss();
            }

            connectFragment = new ConnectFragment();
            connectFragment.show(getParentFragmentManager().beginTransaction(), "ConnectFragment");
        } else if (vId == id.btn_connect_customer) {
            ConnectAsCustomerFragment connectAsCustomerFragment = (ConnectAsCustomerFragment) getParentFragmentManager().findFragmentByTag("ConnectAsCustomerFragment");
            if (connectAsCustomerFragment != null) {
                connectAsCustomerFragment.dismiss();
            }

            connectAsCustomerFragment = new ConnectAsCustomerFragment();
            connectAsCustomerFragment.show(getParentFragmentManager().beginTransaction(), "ConnectAsCustomerFragment");
        } else if (vId == id.btn_disconnect) {
            disconnect();
        } else if (vId == id.btn_register_push) {
            registerPush();
        } else if (vId == id.btn_register_push_and_delete) {
            PackageNamesFragment packageNamesFragment = (PackageNamesFragment) getParentFragmentManager().findFragmentByTag("PackageNamesFragment");
            if (packageNamesFragment != null) {
                packageNamesFragment.dismiss();
            }

            packageNamesFragment = new PackageNamesFragment();
            packageNamesFragment.setCallBack(new CallbackListener<List<PackageName>>() {
                @Override
                public void onSuccess(List<PackageName> packageNames) {
                    registerPushAndDelete(packageNames);
                }
            });
            packageNamesFragment.show(getParentFragmentManager().beginTransaction(), "PackageNamesFragment");
        } else if (vId == id.btn_send_custom_message) {
            CustomMessageFragment customMessageFragment = (CustomMessageFragment) getParentFragmentManager().findFragmentByTag("CustomMessageFragment");
            if (customMessageFragment != null) {
                customMessageFragment.dismiss();
            }

            customMessageFragment = new CustomMessageFragment();
            customMessageFragment.setCallBack(new CallbackListener<Map>() {
                @Override
                public void onSuccess(Map map) {
                    String to = (String) map.get("to");
                    JSONObject msg = (JSONObject) map.get("msg");
                    sendCustomMessage(to, msg);
                }
            });
            customMessageFragment.show(getParentFragmentManager().beginTransaction(), "CustomMessageFragment");
        } else if (vId == id.btn_un_register) {
            unregisterPush();
        } else if (vId == id.btn_update_user) {
            UpdateUserInfoFragment customMessageFragment = (UpdateUserInfoFragment) getParentFragmentManager().findFragmentByTag("UpdateUserInfoFragment");
            if (customMessageFragment != null) {
                customMessageFragment.dismiss();
            }

            customMessageFragment = new UpdateUserInfoFragment();
            customMessageFragment.setCallBack(new CallbackListener<UserInfo>() {
                @Override
                public void onSuccess(UserInfo userInfo) {
                    updateUser(userInfo);
                }
            });
            customMessageFragment.show(getParentFragmentManager().beginTransaction(), "UpdateUserInfoFragment");
        } else if (vId == id.btn_get_user_info) {
            UserIdFragment userIdFragment = (UserIdFragment) getParentFragmentManager().findFragmentByTag("UserIdFragment");
            if (userIdFragment != null) {
                userIdFragment.dismiss();
            }

            userIdFragment = new UserIdFragment();
            userIdFragment.setCallBack(new CallbackListener<List<UserId>>() {
                @Override
                public void onSuccess(List<UserId> packageNames) {
                    getUserInfo(packageNames);
                }
            });
            userIdFragment.show(getParentFragmentManager().beginTransaction(), "UserIdFragment");
        }
    }

    public void disconnect() {
        Common.client.disconnect();
        PrefUtils.getInstance(requireContext()).clearData();
        Utils.updateLog(requireContext(), "disconnect success");
    }

    public void registerPush() {
        if (!Common.client.isConnected()) {
            return;
        }
        String token = "";
        Common.client.registerPushToken(token, new StatusListener() {
            @Override
            public void onSuccess() {
                PrefUtils.getInstance(requireContext()).putString(Constant.PREF_PUSH_TOKEN, token);
                Utils.updateLog(requireContext(), "registerPushToken success");
            }

            @Override
            public void onError(StringeeError stringeeError) {
                super.onError(stringeeError);
                Utils.updateLog(requireContext(), "registerPushToken error:" + stringeeError.getMessage());
            }
        });
    }

    public void registerPushAndDelete(List<PackageName> packageNames) {
        if (!Common.client.isConnected()) {
            return;
        }
        List<String> packageNameList = new java.util.ArrayList<>();
        if (!Utils.isListEmpty(packageNames)) {
            for (PackageName packageName : packageNames) {
                if (packageName.isSelected()) {
                    packageNameList.add(packageName.getName());
                }
            }
        }
        Common.client.registerPushTokenAndDeleteOthers("", packageNameList, new StatusListener() {
            @Override
            public void onSuccess() {
                Utils.updateLog(requireContext(), "registerPushTokenAndDeleteOthers success");
            }

            @Override
            public void onError(StringeeError stringeeError) {
                super.onError(stringeeError);
                Utils.updateLog(requireContext(), "registerPushTokenAndDeleteOthers error:" + stringeeError.getMessage());
            }
        });
    }

    public void unregisterPush() {
        if (!Common.client.isConnected()) {
            return;
        }
        String token = PrefUtils.getInstance(requireContext()).getString(Constant.PREF_PUSH_TOKEN, "");
        Common.client.unregisterPushToken(token, new StatusListener() {
            @Override
            public void onSuccess() {
                PrefUtils.getInstance(requireContext()).putString(Constant.PREF_PUSH_TOKEN, token);
                Utils.updateLog(requireContext(), "unregisterPushToken success");
            }

            @Override
            public void onError(StringeeError stringeeError) {
                super.onError(stringeeError);
                Utils.updateLog(requireContext(), "unregisterPushToken error:" + stringeeError.getMessage());
            }
        });
    }

    public void sendCustomMessage(String to, JSONObject msg) {
        if (!Common.client.isConnected()) {
            return;
        }
        Common.client.sendCustomMessage(to, msg, new StatusListener() {
            @Override
            public void onSuccess() {
                Utils.updateLog(requireContext(), "sendCustomMessage success");
            }

            @Override
            public void onError(StringeeError stringeeError) {
                super.onError(stringeeError);
                Utils.updateLog(requireContext(), "sendCustomMessage error:" + stringeeError.getMessage());
            }
        });
    }

    public void updateUser(UserInfo userInfo) {
        if (!Common.client.isConnected()) {
            return;
        }
        Common.client.updateUser(userInfo, new StatusListener() {
            @Override
            public void onSuccess() {
                Utils.updateLog(requireContext(), "updateUser success");
            }

            @Override
            public void onError(StringeeError stringeeError) {
                super.onError(stringeeError);
                Utils.updateLog(requireContext(), "updateUser error:" + stringeeError.getMessage());
            }
        });
    }

    public void getUserInfo(List<UserId> userIds) {
        if (!Common.client.isConnected()) {
            return;
        }
        List<String> userIdList = new java.util.ArrayList<>();
        if (!Utils.isListEmpty(userIds)) {
            for (UserId userId : userIds) {
                if (userId.isSelected()) {
                    userIdList.add(userId.getId());
                }
            }
        }
        Common.client.getUserInfo(userIdList, new CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                Utils.updateLog(requireContext(), "getUserInfo success: " + Utils.convertObjectToStringJSON(users));
            }

            @Override
            public void onError(StringeeError stringeeError) {
                super.onError(stringeeError);
                Utils.updateLog(requireContext(), "getUserInfo error:" + stringeeError.getMessage());
            }
        });
    }
}
