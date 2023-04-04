package net.sunofbeach.blog.utils;

public interface Constants {

    interface User {
        String ROLE_ADMIN = "role_admin";
        String ROLE_USER = "role_user";
        String DEFAULT_AVATAR = "static/uploadFile/2023_04_01/png/1091773219959996416.png";
        String DEFAULT_STATE = "1";
        String DISABLE_STATE = "0";
        String KEY_TOKEN = "key_token";
        String KEY_EMAIL_SEND_IP = "yey_email_send_ip";
        String KEY_EMIL_SEND_ADDRESS = "key_emil_send_address";
        String JWT_SEC = "jwt_sec_tan_jia_1029";
    }

    interface Settings {
        String MANAGER_ACCOUNT_INIT_STATE = "manager_account_init_state";
        String WEB_SIZE_TITLE = "web_size_title";
        String WEB_SIZE_DESCRIPTION = "web_size_description";
        String WEB_SIZE_KEYWORDS = "web_size_keywords";
        String WEB_SIZE_VIEW_COUNT = "web_size_view_count";
    }
}
