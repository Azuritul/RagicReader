package com.ragic.android.reader;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.ragic.android.reader.db.DataHelper;
import com.ragic.android.reader.pojo.User;
import com.ragic.android.reader.utils.Utils;

/**
 * Author: Azuritul
 * Date:   2010/10/3
 * Time:   下午 08:58:14
 */
public class AppListActivity extends BaseListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_app);

        GlobalObject global = (GlobalObject) getApplication();
        DataHelper helper = global.getDataHelper();
        User user = helper.getUser();
        if (user == null) {
            Intent intent = new Intent(AppListActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            feedUrl = Utils.createHttpsFeedUrlFromUser(helper.getUser());
            ((TextView) findViewById(R.id.positionInfo)).setText("@" + user.getAccounts().get(0).getAccountName());
            new LoadFeedTask().execute();
        }
        findViewById(R.id.button_account).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showSwitchAccountDialog();
            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, SheetListActivity.class);
        i.putExtra(getString(R.string.param_feed_url), messages.get(position).getLink().toExternalForm() + "/feed");
        startActivity(i);
    }

}
