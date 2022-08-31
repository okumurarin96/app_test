package com.stringee.app.common;

import android.R.id;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stringee.app.model.JSONModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Utils {
    public static boolean isStringEmpty(@Nullable CharSequence text) {
        if (text != null) {
            if (text.toString().equalsIgnoreCase("null")) {
                return true;
            } else {
                return text.toString().trim().length() <= 0;
            }
        } else {
            return true;
        }
    }

    public static boolean isListEmpty(@Nullable List list) {
        if (list != null) {
            return list.isEmpty();
        } else {
            return true;
        }
    }

    public static void reportMessage(Context context, String message) {
        runOnUiThread(() -> {
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            if (VERSION.SDK_INT < VERSION_CODES.R) {
                toast.setGravity(Gravity.CENTER, 0, 0);
                TextView v = toast.getView().findViewById(id.message);
                if (v != null)
                    v.setGravity(Gravity.CENTER);
            }
            toast.show();
        });
    }

    public static void runOnUiThread(Runnable runnable) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(runnable);
    }

    public static void postDelayed(Runnable runnable, long delayMillis) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(runnable, delayMillis);
    }

    public static <T> List<T> getListFromStringJSON(String jsonArray, Class<T> clazz) {
        Type typeOfT = TypeToken.getParameterized(List.class, clazz).getType();
        return new Gson().fromJson(jsonArray, typeOfT);
    }

    public static <T> String convertObjectToStringJSON(T object) {
        return new Gson().toJson(object);
    }

    public static List<JSONModel> jsonToJSONModel(JSONObject jsonObject) throws JSONException {
        List<JSONModel> jsonModels = new ArrayList<>();
        Iterator<String> iter = jsonObject.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            String value = jsonObject.getString(key);
            jsonModels.add(new JSONModel(key, value));
        }
        return jsonModels;
    }

    public static JSONObject jsonModelToJSON(List<JSONModel> jsonModels) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        for (JSONModel jsonModel : jsonModels) {
            jsonObject.put(jsonModel.getKey(), jsonModel.getValue());
        }
        return jsonObject;
    }

    public static String getEditableText(@Nullable CharSequence text, boolean isNotNull) {
        if (text != null) {
            return text.toString().trim();
        } else {
            return isNotNull ? "" : null;
        }
    }

    public static void updateLog(Context context, String log) {
        runOnUiThread(() -> {
            Log.d("Stringee", log);
            Common.logs.add(log);
            Intent intent = new Intent(Notify.UPDATE_LOG.getValue());
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        });
    }
}
