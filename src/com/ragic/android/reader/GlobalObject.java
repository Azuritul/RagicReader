package com.ragic.android.reader;

import android.app.Application;
import com.ragic.android.reader.db.DataHelper;
import com.ragic.android.reader.pojo.User;
import org.apache.http.HttpVersion;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

/**
 * User: Azuritul
 * Date: 2010/6/21
 * Time: 下午 04:27:36
 */
public class GlobalObject extends Application {

    private User user;
    private DataHelper dataHelper;
    private DefaultHttpClient httpClient;

    @Override
    public void onCreate() {
        super.onCreate();
        dataHelper = new DataHelper(this);
        dataHelper.open();
        httpClient = createHttpClient();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (dataHelper != null) {
            dataHelper.close();
        }
        shutdownHttpClient();
    }

    private DefaultHttpClient createHttpClient() {
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
        HttpProtocolParams.setUseExpectContinue(params, true);
        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
        return new DefaultHttpClient(conMgr, params);
    }

    private void shutdownHttpClient() {
        if (httpClient != null && httpClient.getConnectionManager() != null) {
            httpClient.getConnectionManager().shutdown();
        }
    }

    public DefaultHttpClient getHttpClient() {
        return httpClient;
    }

    public DataHelper getDataHelper() {
        return dataHelper;
    }

    private DataHelper createDataHelper() {
        dataHelper = new DataHelper(this);
        dataHelper.open();
        return dataHelper;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
