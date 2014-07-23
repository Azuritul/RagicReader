package com.ragic.android.reader;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.ragic.android.reader.pojo.Message;

import java.util.ArrayList;

/**
 * Author: Azuritul
 * Date:   2010/10/3
 * Time:   下午 10:27:55
 */
public class SheetListActivity extends BaseListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_sheet);
        feedUrl = this.getIntent().getStringExtra(getString(R.string.param_feed_url));
        new LoadFeedTask().execute();
        setCurrentAccountPosition();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, EntryListActivity.class);
        i.putExtra(getString(R.string.param_feed_url), messages.get(position).getLink().toExternalForm() + "/feed");
        startActivity(i);
    }

    protected class LoadFeedTask extends AsyncTask<Void, Integer, String> {
        private final ProgressDialog dialog = new ProgressDialog(SheetListActivity.this);
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
            adapter = new ArrayAdapter<String>(SheetListActivity.this, R.layout.row_sheet, R.id.sheet_title, titles);
            setListAdapter(adapter);
            adapter.notifyDataSetChanged();
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
        }
    }

}
