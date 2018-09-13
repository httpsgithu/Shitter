package org.nuclearfog.twidda.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.nuclearfog.twidda.R;
import org.nuclearfog.twidda.backend.MessageLoader;
import org.nuclearfog.twidda.backend.listitems.TwitterUser;
import org.nuclearfog.twidda.viewadapter.MessageAdapter;
import org.nuclearfog.twidda.viewadapter.MessageAdapter.OnItemSelected;

public class DirectMessage extends AppCompatActivity implements OnItemSelected, OnRefreshListener {

    private MessageLoader mLoader;
    private RecyclerView dmList;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.messagepage);
        Toolbar tool = findViewById(R.id.dm_toolbar);
        setSupportActionBar(tool);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.directmessage);
        SwipeRefreshLayout refresh = findViewById(R.id.dm_reload);

        dmList = findViewById(R.id.messagelist);
        dmList.setLayoutManager(new LinearLayoutManager(this));
        dmList.setHasFixedSize(true);

        refresh.setRefreshing(true);
        refresh.setOnRefreshListener(this);
        loadContent();
    }


    @Override
    protected void onPause() {
        if (mLoader != null && !mLoader.isCancelled())
            mLoader.cancel(true);
        super.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        getMenuInflater().inflate(R.menu.message, m);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.message:
                Intent sendDm = new Intent(this, MessagePopup.class);
                startActivity(sendDm);
                return true;

            default:
                return false;
        }
    }


    private void loadContent() {
        mLoader = new MessageLoader(this);
        mLoader.execute();
    }


    @Override
    public void onSelected(int index) {
        MessageAdapter mAdapter = (MessageAdapter) dmList.getAdapter();
        if (mAdapter != null) {
            TwitterUser sender = mAdapter.getData().get(index).sender;
            Intent sendDm = new Intent(this, MessagePopup.class);
            sendDm.putExtra("username", sender.screenname);
            startActivity(sendDm);
        }
    }


    @Override
    public void onRefresh() {
        loadContent();
    }
}