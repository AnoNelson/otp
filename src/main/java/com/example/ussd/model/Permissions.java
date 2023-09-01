package com.example.ussd.model;

import java.util.Arrays;

public enum Permissions {
    USER_MANAGEMENT, VIEW_DASHBOARD,PASSWORD_RESET,OTP_SUBMIT;

    public static final String GET_PERMISSION(String role) {
        if (Permissions.valueOf(role.toUpperCase()) != null &&
                Permissions.valueOf(role.toUpperCase()).toString().equalsIgnoreCase(role)) {
            return role.toUpperCase();
        }
        return null;
    }

    public static final String GET_PERMISSION_JSON() {
        StringBuilder data = new StringBuilder();
        data.append("{\"data\": [");
        Arrays.stream(Permissions.values()).forEach(permissions -> {
            data.append("\"" + permissions.toString() + "\",");
        });
        data.deleteCharAt(data.length() - 1);
        data.append("]}");
        return data.toString();
    }
}
