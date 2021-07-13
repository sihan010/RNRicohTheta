package com.rntheta360.RNTheta360Sdk.LiveView;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.AsyncTask;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.rntheta360.R;
import com.rntheta360.RNTheta360Sdk.network.HttpConnector;
import com.rntheta360.RNTheta360Sdk.network.Session;
import com.rntheta360.RNTheta360Sdk.view.MJpegInputStream;
import com.rntheta360.RNTheta360Sdk.view.MJpegView;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;

public class RNTheta360LiveView extends LinearLayout {
    private String mSessionId = "";
    private String cameraIpAddress = getResources().getString(R.string.theta_ip_address);
    private Context mContext = null;
    private View mView = null;

    private ShowLiveViewTask livePreviewTask = null;

    private boolean isConnected = false;
    private boolean isLivePreview = false;

    //UI Components
    private MJpegView mMv;

    public RNTheta360LiveView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public RNTheta360LiveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public RNTheta360LiveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    //TO:DO: OnPause and OnResume Here
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mMv.stopPlay();
        if (livePreviewTask != null) {
            livePreviewTask.cancel(true);
        }
    }

    //INITIALIZE ALL UI COMPONENTS HERE
    private void init() {
        View v = inflate(mContext, R.layout.rn_theta360_live_view, this);
        this.mView = v;

        mMv = (MJpegView) v.findViewById(R.id.live_preview);

        forceConnectToWifi();
    }

    // REACT NATIVE COMMUNICATIONS
    public void setParamSessionId(String sessionId) {
        mSessionId = sessionId;
    }

    public void toggleLiveView() {
        Log.v("------>", "toggleLiveView");
        if (isConnected) {
            if (isLivePreview) {
                mMv.stopPlay();
                if (livePreviewTask != null) {
                    livePreviewTask.cancel(true);
                }
                isLivePreview = false;
            } else {
                mMv.play();
                livePreviewTask = new ShowLiveViewTask();
                livePreviewTask.execute(cameraIpAddress);
                isLivePreview = true;
            }

        } else {
            Log.v("Connection", "disconnected");
            onConnectionChange(false);
        }
    }

    // SET EVENT HANDLERS TO REACT NATIVE END HERE
    private void onConnectionChange(boolean status) {
        WritableMap event = Arguments.createMap();

        event.putBoolean("connected", status);
        event.putBoolean("status", true);

        ReactContext reactContext = (ReactContext) getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "onConnectionChange", event);
    }

    //All the Custom Codes

    private class ShowLiveViewTask extends AsyncTask<String, String, MJpegInputStream> {
        @Override
        protected MJpegInputStream doInBackground(String... ipAddress) {
            MJpegInputStream mjis = null;
            final int MAX_RETRY_COUNT = 20;

            for (int retryCount = 0; retryCount < MAX_RETRY_COUNT; retryCount++) {
                try {
                    Log.v("live view", "start Live view");
                    HttpConnector camera = new HttpConnector(ipAddress[0]);
                    InputStream is = camera.getLivePreview();
                    mjis = new MJpegInputStream(is);
                    // Play view
                    retryCount = MAX_RETRY_COUNT;
                } catch (IOException e) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                } catch (JSONException e) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            return mjis;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            for (String log : values) {
                Log.v("live view", "failed to start live view");
            }
        }

        @Override
        protected void onPostExecute(MJpegInputStream mJpegInputStream) {
            if (mJpegInputStream != null) {
                mMv.setSource(mJpegInputStream);
            } else {
                Log.v("live view", "failed to start live view");
            }
        }
    }


    private void connectToCameraAndSetSession(boolean status) {
        Log.v("------>", "connectToCameraAndSetSession "+status);
        if (status) {
            Log.v("connection", "connecting to " + cameraIpAddress);
            HttpConnector camera = new HttpConnector(cameraIpAddress);
            String sesionId = camera.getSessionId();
            Session.setSessionId(sesionId);
        } else {
            mMv.stopPlay();
            if (livePreviewTask != null) {
                livePreviewTask.cancel(true);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void forceConnectToWifi() {
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

                        connectToCameraAndSetSession(true);
                        isConnected = true;
                        onConnectionChange(true);
                    }

                    @Override
                    public void onLost(Network network) {
                        super.onLost(network);
                        Log.v("Connection", "disconnect from Wi-Fi AP");
                        connectToCameraAndSetSession(false);
                        isConnected = false;
                        onConnectionChange(false);
                    }
                };

                cm.registerNetworkCallback(requestedNetwork, callback);
            }
        } else {
            Log.v("Connection", "Not Supported");
            onConnectionChange(false);
        }
    }
}
