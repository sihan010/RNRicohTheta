package com.rntheta360.RNTheta360Sdk;


import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.rntheta360.RNTheta360Sdk.LiveView.RNTheta360Module;
import com.rntheta360.RNTheta360Sdk.LiveView.RNTheta360LiveViewManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RNTheta360SdkPackage implements ReactPackage {
    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Arrays.<ViewManager>asList(new RNTheta360LiveViewManager());

    }

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        List<NativeModule> modules = new ArrayList<>();
        modules.add(new RNTheta360Module(reactContext));
        return modules;

    }
}
