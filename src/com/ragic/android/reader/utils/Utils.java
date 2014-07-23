package com.ragic.android.reader.utils;

import android.util.Log;
import com.ragic.android.reader.pojo.User;

/**
 * Author: Azuritul
 * Date:   2010/10/5
 * Time:   下午 04:07:28
 */
public class Utils {

    private static final String RAGIC_HTTPS_DOMAIN = "https://www.ragic.com/";

    public static String createHttpsFeedUrlFromUser(User user) {
        StringBuilder urlBuilder = new StringBuilder(RAGIC_HTTPS_DOMAIN);
        urlBuilder.append(user.getAccounts().get(0).getAccountName()).append("/feed?login&silent");
        urlBuilder.append("&u=").append(user.getEmail());
        urlBuilder.append("&p=").append(user.getPassword());
        Log.i("###################################", urlBuilder.toString());
        return urlBuilder.toString();
    }

    public static String createHttpsFeedUrlFromUser(User user, String accountName) {
        StringBuilder urlBuilder = new StringBuilder(RAGIC_HTTPS_DOMAIN);
        urlBuilder.append(accountName).append("/feed");
        return urlBuilder.toString();
    }

}
