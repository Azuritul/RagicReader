package com.ragic.android.reader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;
import com.ragic.android.reader.db.DataHelper;
import com.ragic.android.reader.exception.AuthenticationException;
import com.ragic.android.reader.pojo.User;

/**
 * Author: Azuritul
 * Date:   2010/10/3
 * Time:   下午 04:14:09
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);
        findViewById(R.id.login).setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                EditText username = (EditText) findViewById(R.id.username);
                EditText password = (EditText) findViewById(R.id.password);
                EditText account = (EditText) findViewById(R.id.account);
                String u = username.getText().toString();
                String p = password.getText().toString();
                String a = account.getText().toString();
                new AuthTask().execute(u, p, a);
                break;
        }
    }

    protected class AuthTask extends AsyncTask<String, Void, Boolean> {

        private final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
        private GlobalObject global = (GlobalObject) getApplication();
        private User user;

        protected void onPreExecute() {
            this.dialog.setMessage(getString(R.string.msg_authenticating));
            this.dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                String username = strings[0];
                String password = strings[1];
                String account = strings[2];
                Validator.authenticate(global, username, password, account);
                DataHelper helper = global.getDataHelper();
                user = new User(username, password);
                user.addAccount(account);
                helper.createUser(user);
                helper.createAccount(user);
                return true;
            }
            catch (AuthenticationException e) {
                return false;
            }
            catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean validated) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
            if (!validated) {
                Toast.makeText(LoginActivity.this, getString(R.string.errmsg_invalid_login), Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(LoginActivity.this, AppListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }
    }
}
