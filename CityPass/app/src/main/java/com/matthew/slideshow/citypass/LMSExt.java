package com.matthew.slideshow.citypass;

import android.content.Context;
import android.webkit.WebView;

import org.json.JSONObject;

/**
 * Created by dave on 6/5/16.
 */
public class LMSExt {
    private WebView webView;
    private JSONObject response;
    private boolean error;
    private String login_name, login_password;

    LMSExt(Context context) {
        webView = new WebView(context);
        response = new JSONObject();
        LoginRW loginRW = new LoginRW(context);
        login_name = loginRW.getLoginName();
        login_password = loginRW.getLoginPassword();
        error = false;
        loginRW.close();
    }

    public void getAnnouncement() {
    }

    public JSONObject getResponse() {
        return response;
    }

    public boolean getError() {
        return error;
    }
}
