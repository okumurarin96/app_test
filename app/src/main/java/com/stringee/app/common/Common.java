package com.stringee.app.common;

import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;

import com.stringee.StringeeClient;
import com.stringee.call.StringeeCall;
import com.stringee.call.StringeeCall2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Common {
    public static StringeeClient client;
    public static Map<String, StringeeCall> callsMap = new HashMap<>();
    public static Map<String, StringeeCall2> calls2Map = new HashMap<>();
    public static List<String> logs = new ArrayList<>();
    public static ActivityResultLauncher<Intent> launcher;
    public static boolean isInCall = false;
    public static boolean isCustomer = false;
    public static int REQUEST_PERMISSION_CALL = 1;
}
