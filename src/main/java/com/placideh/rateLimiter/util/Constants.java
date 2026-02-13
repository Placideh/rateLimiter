package com.placideh.rateLimiter.util;

public class Constants {

    // API Key Constants
    public static final String API_KEY_PREFIX = "sk_live_";

    // Rate Limiting Algorithms
    public static final String TOKEN_BUCKET = "TOKEN_BUCKET";
    public static final String FIXED_WINDOW = "FIXED_WINDOW";




    // User Roles
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_COMPANY = "COMPANY";


    // Notification Types
    public static final String NOTIFICATION_SMS = "SMS";
    public static final String NOTIFICATION_EMAIL = "EMAIL";

    // Notification Status
    public static final String NOTIF_STATUS_SENT = "SENT";
    public static final String NOTIF_STATUS_FAILED = "FAILED";

    private Constants() {}
}
