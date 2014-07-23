package com.ragic.android.reader;


import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Log;
import android.util.Xml;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.ragic.android.reader.db.DataHelper;
import com.ragic.android.reader.pojo.Account;
import com.ragic.android.reader.pojo.Message;
import com.ragic.android.reader.pojo.User;
import com.ragic.android.reader.utils.Utils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Azuritul
 * Date:   2010/10/4
 * Time:   上午 12:11:22
 */
public class BaseListActivity extends ListActivity {

    private static final String PROTOCOL_HTTP = "http";
    private static final String PROTOCOL_HTTPS = "https";
    private static final int OPTION_ITEM_SIGNOUT = 0;
    private static final int OPTION_ITEM_ADDACCOUNT = 1;
    private static final int OPTION_ITEM_ABOUT = 2;

    ArrayList<Message> messages;
    ArrayAdapter adapter;
    String feedUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, OPTION_ITEM_SIGNOUT, 0, R.string.button_signout).setIcon(R.drawable.ic_menu_signout);
        menu.add(0, OPTION_ITEM_ADDACCOUNT, 0, R.string.button_add_account).setIcon(R.drawable.ic_menu_invite);
        menu.add(0, OPTION_ITEM_ABOUT, 0, R.string.button_about).setIcon(R.drawable.ic_menu_help);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case OPTION_ITEM_SIGNOUT:
                new AlertDialog.Builder(BaseListActivity.this)
                    .setIcon(android.R.drawable.ic_menu_info_details)
                    .setTitle(R.string.button_signout)
                    .setMessage(R.string.msg_signout_alert)
                    .setPositiveButton(getString(R.string.button_confirm), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            DataHelper helper = ((GlobalObject) getApplication()).getDataHelper();
                            helper.deleteUser();
                            helper.deleteAccount();
                            Intent intent = new Intent(BaseListActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                        }
                    })
                    .create().show();
                return true;
            case OPTION_ITEM_ADDACCOUNT:
                LayoutInflater inflator = LayoutInflater.from(BaseListActivity.this);
                final View addAccountView = inflator.inflate(R.layout.add_account, null);
                new AlertDialog.Builder(BaseListActivity.this)
                    .setTitle(R.string.msg_add_account_title)
                    .setView(addAccountView)
                    .setPositiveButton(R.string.button_confirm, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            EditText newAccountInput = (EditText) addAccountView.findViewById(R.id.add_account);
                            if (newAccountInput.getText() != null) {
                                String newAccount = newAccountInput.getText().toString();
                                if (!"".equals(newAccount.trim())) {
                                    GlobalObject global = (GlobalObject) getApplication();
                                    DataHelper helper = global.getDataHelper();
                                    User user = helper.getUser();
                                    ArrayList<Account> accountList = user.getAccounts();
                                    boolean existed = false;
                                    for (Account acc : accountList) {
                                        if (acc.getAccountName().equals(newAccount)) {
                                            existed = true;
                                            continue;
                                        }
                                    }

                                    if (!existed) {
                                        helper.addAccount(user, newAccount);
                                        Toast.makeText(BaseListActivity.this, "Account " + newAccount + " added", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(BaseListActivity.this, "Account " + newAccount + " already existed", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }
                        }
                    })
                    .setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                        }
                    })
                    .create().show();
                return true;
            case OPTION_ITEM_ABOUT:
                LayoutInflater factory = LayoutInflater.from(BaseListActivity.this);
                final View aboutView = factory.inflate(R.layout.about, null);
                new AlertDialog.Builder(BaseListActivity.this)
                    .setTitle(R.string.msg_about)
                    .setView(aboutView)
                    .setPositiveButton(R.string.button_close, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                        }
                    })
                    .create().show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected ArrayList<Message> parseFeed() {
        final Message currentMessage = new Message();
        RootElement root = new RootElement("rss");
        messages = new ArrayList<Message>();
        Element channel = root.getChild("channel");
        Element item = channel.getChild("item");
        item.setEndElementListener(new EndElementListener() {
            public void end() {
                messages.add(currentMessage.copy());
            }
        });
        item.getChild("title").setEndTextElementListener(new EndTextElementListener() {
            public void end(String body) {
                currentMessage.setTitle(body);
            }
        });
        item.getChild("link").setEndTextElementListener(new EndTextElementListener() {
            public void end(String body) {
                currentMessage.setLink(body);
            }
        });
        item.getChild("description").setEndTextElementListener(new EndTextElementListener() {
            public void end(String body) {
                currentMessage.setDescription(body);
            }
        });
        item.getChild("pub_date").setEndTextElementListener(new EndTextElementListener() {
            public void end(String body) {
                currentMessage.setDate(body);
            }
        });

        BufferedInputStream in = null;
        try {
            Log.i("********************", feedUrl);
            in = new BufferedInputStream(getInputStream());
            Xml.parse(in, Xml.Encoding.UTF_8, root.getContentHandler());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                    Log.i("###############", " InputStreamClosed at finally block");
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return messages;
    }

    protected InputStream getInputStream() {
        try {
            int RETRY_COUNT = 0;
            final int RETRY_MAX = 3;
            GlobalObject global = (GlobalObject) getApplication();
            DefaultHttpClient client = global.getHttpClient();
            HttpGet httpget = new HttpGet(feedUrl);
            HttpResponse response = client.execute(httpget);
            //Log.i("########################", "response status: " + response.getStatusLine().getStatusCode());
            //showCookies(client);
            HttpEntity entity = response.getEntity();
            while (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK ) {
                response = client.execute(httpget);
                RETRY_COUNT++;
                if (RETRY_COUNT == RETRY_MAX) {
                    Toast.makeText(BaseListActivity.this, R.string.errmsg_loading_error, Toast.LENGTH_SHORT).show();
                }
            }
            return entity.getContent();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void showCookies(DefaultHttpClient client) {
        List<Cookie> cookies = client.getCookieStore().getCookies();
        if (cookies.isEmpty()) {
            Log.i("xxxxxxxxx", "none");
        } else {
            for (Cookie cooky : cookies) {
                Log.i("@@@@@@@@cookies@@@@@@@@", "- " + cooky.toString());
            }
        }
    }

    protected void showSwitchAccountDialog() {
        final GlobalObject object = (GlobalObject) getApplication();
        DataHelper helper = object.getDataHelper();
        final User user = helper.getUser();

        new AlertDialog.Builder(BaseListActivity.this)
            .setTitle(R.string.msg_switch_account_title)
            .setItems(user.getAccountsAsArray(), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    String[] accounts = user.getAccountsAsArray();
                    feedUrl = Utils.createHttpsFeedUrlFromUser(user, accounts[which]);
                     ((TextView) findViewById(R.id.positionInfo)).setText("@" + accounts[which]);
                    new LoadFeedTask().execute();
                }
            })
            .create().show();
    }

    protected void setCurrentAccountPosition() {
        String currentAccount = getCurrentAccountByURL();
        ((TextView) findViewById(R.id.positionInfo)).setText("@" + currentAccount);
    }

    protected String getCurrentAccountByURL() {
        String account = feedUrl.replace("http://www.ragic.com/", "");
        account = account.substring(0, account.indexOf("/"));
        String currentAccount = feedUrl.substring( feedUrl.indexOf(account)).replace("/feed", "");
        return currentAccount;
    }

    protected class LoadFeedTask extends AsyncTask<Void, Integer, String> {
        private final ProgressDialog dialog = new ProgressDialog(BaseListActivity.this);
        private ArrayList<String> titles;

        protected void onPreExecute() {
            this.dialog.setMessage(getString(R.string.msg_loading));
            this.dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            this.dialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            messages = parseFeed();
            titles = new ArrayList<String>(messages.size());
            for (Message msg : messages) {
                titles.add(msg.getTitle());
            }
            return "";
        }

        protected void onPostExecute(final String result) {
            adapter = new ArrayAdapter<String>(BaseListActivity.this, R.layout.row_app, R.id.app_title, titles);
            setListAdapter(adapter);
            adapter.notifyDataSetChanged();
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
