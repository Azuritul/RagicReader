package com.ragic.android.reader;

import android.util.Log;
import com.ragic.android.reader.exception.AuthenticationException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Author: Azuritul
 * Date:   2010/10/5
 * Time:   下午 01:40:00
 */
public class Validator {

    private static final String DOMAIN = "https://www.ragic.com/";

    public static void authenticate(GlobalObject global, String username, String password, String account) throws AuthenticationException {
        HttpClient client = global.getHttpClient();
        StringBuilder urlBuilder = new StringBuilder(DOMAIN);
        urlBuilder.append(account).append("/feed?login&silent");
        urlBuilder.append("&u=").append(username);
        urlBuilder.append("&p=").append(password);
        BufferedReader in = null;
        try {
            Log.i("###### url ######", urlBuilder.toString());
            HttpGet request = new HttpGet(new URI(urlBuilder.toString()));
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = in.readLine();
            if (line.indexOf("xml") == -1) {
                throw new AuthenticationException();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try { in.close(); Log.i("###########", "Reader closed after authentication."); }
            catch (IOException e) { e.printStackTrace(); }
        }
    }
}
