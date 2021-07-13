package com.rntheta360.RNTheta360Sdk.network;

public class Session {
    private static String sessionId = "";

    public static String getSessionId() {
        return sessionId;
    }

    public static void setSessionId(String s) {
        sessionId = s;
    }
}
