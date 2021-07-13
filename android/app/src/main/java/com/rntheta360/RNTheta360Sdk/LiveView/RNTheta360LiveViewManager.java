package com.rntheta360.RNTheta360Sdk.LiveView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.Map;

public class RNTheta360LiveViewManager extends SimpleViewManager<RNTheta360LiveView> {
    private final String PackageName = "RNTheta360LiveView";
    private RNTheta360LiveView viewInstance;

    //parameters
    private final String SESSION_ID = "sessionId";

    //eventListeners
    private final String ON_CONNECTION_CHANGE = "onConnectionChange";

    //commands
    private final int TOGGLE_LIVE_VIEW = 1;

    @NonNull
    @Override
    public String getName() {
        return PackageName;
    }

    @NonNull
    @Override
    protected RNTheta360LiveView createViewInstance(@NonNull ThemedReactContext reactContext) {
        viewInstance = new RNTheta360LiveView(reactContext);
        return viewInstance;
    }

    @ReactProp(name = SESSION_ID)
    public void setSessionId(RNTheta360LiveView view, String sessionId) {
        view.setParamSessionId(sessionId);
    }

    @Override
    public void receiveCommand(RNTheta360LiveView view, String commandId, @Nullable ReadableArray args) {
        super.receiveCommand(view, commandId, args);
        switch (commandId) {
            case "toggleLivePreview":
                view.toggleLiveView();
                break;
        }
    }

    public Map getExportedCustomBubblingEventTypeConstants() {
        MapBuilder.Builder events = MapBuilder.builder();

        Map onConnectionChangeHandler = MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", ON_CONNECTION_CHANGE));
        events.put(ON_CONNECTION_CHANGE, onConnectionChangeHandler);

        return events.build();
    }
}
