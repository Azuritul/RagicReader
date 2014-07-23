package com.ragic.android.reader;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
public class EntryListActivity extends BaseListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_entry);
        feedUrl = this.getIntent().getStringExtra(getString(R.string.param_feed_url));
        new LoadFeedTask().execute();
        setCurrentAccountPosition();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        AlertDialog dialog = new AlertDialog.Builder(EntryListActivity.this)
            .setTitle(messages.get((int) id).getTitle())
            .setMessage(messages.get((int) id).getDescription())
            .setPositiveButton(getString(R.string.button_close), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialoginterface, int i) {
                }
            })
            .create();
        dialog.show();
    }


    protected class LoadFeedTask extends AsyncTask<Void, Integer, String> {
        private final ProgressDialog dialog = new ProgressDialog(EntryListActivity.this);
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
            adapter = new ArrayAdapter<String>(EntryListActivity.this, R.layout.row_entry, R.id.entry_title, titles);
            setListAdapter(adapter);
            adapter.notifyDataSetChanged();
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
        }
    }

}
