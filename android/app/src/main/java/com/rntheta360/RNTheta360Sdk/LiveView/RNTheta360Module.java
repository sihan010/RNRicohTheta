package com.rntheta360.RNTheta360Sdk.LiveView;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.IllegalViewOperationException;

public class RNTheta360Module extends ReactContextBaseJavaModule {
    private final String Module_Name = "RNTheta360";
    private ReactApplicationContext mContext;
    private Boolean isConnected = false;
    private RNTheta360LiveView viewInstance;

    public RNTheta360Module(ReactApplicationContext context) {
        super(context);
        mContext = context;
    }

    @NonNull
    @Override
    public String getName() {
        return Module_Name;
    }

    @ReactMethod
    public void checkThetaConnection(Callback errorCallback, Callback successCallback) {
        try {
            forceConnectToWifi();
            WritableMap map = Arguments.createMap();

            map.putBoolean("success", true);
            map.putBoolean("connected", isConnected);
            successCallback.invoke(map);
        } catch (IllegalViewOperationException e) {
            WritableMap map = Arguments.createMap();

            map.putBoolean("success", false);
            map.putBoolean("connected", isConnected);
            errorCallback.invoke(e.getMessage());
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void forceConnectToWifi() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if ((info != null) && info.isAvailable()) {
                NetworkRequest.Builder builder = new NetworkRequest.Builder();
                builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
                NetworkRequest requestedNetwork = builder.build();

                ConnectivityManager.NetworkCallback callback = new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onAvailable(Network network) {
                        super.onAvailable(network);

                        ConnectivityManager.setProcessDefaultNetwork(network);
                        Log.v("Connection", "connect to Wi-Fi AP");
                        isConnected = true;
                    }

                    @Override
                    public void onLost(Network network) {
                        super.onLost(network);
                        Log.v("Connection", "disconnect from Wi-Fi AP");
                        isConnected = false;
                    }
                };

                cm.registerNetworkCallback(requestedNetwork, callback);
            }
        } else {
            Log.v("Connection", "Not Supported");
            isConnected = false;
        }
    }
}
