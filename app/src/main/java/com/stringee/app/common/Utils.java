package com.stringee.app.common;

import android.R.id;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class Utils {
    public static boolean isTextEmpty(@Nullable String text) {
        if (text != null) {
            if (text.equalsIgnoreCase("null")) {
                return true;
            } else {
                return text.trim().length() <= 0;
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

    public static <T> List<T> getListFromStringJSON(String jsonArray, Class<T> clazz) {
        Type typeOfT = TypeToken.getParameterized(List.class, clazz).getType();
        return new Gson().fromJson(jsonArray, typeOfT);
    }

    public static <T> String convertObjectToStringJSON(T object) {
        return new Gson().toJson(object);
    }
}
