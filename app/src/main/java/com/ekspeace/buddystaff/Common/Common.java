package com.ekspeace.buddystaff.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.arch.core.internal.FastSafeIterableMap;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {
    public static String currentInfoName;
    public static final SimpleDateFormat simpleDataFormat = new SimpleDateFormat("dd MMMM");
    public static String Service;
    public static String Password;
    public static String PasswordId;

    public static Boolean isOnline(Context context)	{
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnected();
    }
    public static String convertTimeStampToStringKey(Timestamp timestamp) {
        Date date = timestamp.toDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM");
        return simpleDateFormat.format(date);
    }
}
