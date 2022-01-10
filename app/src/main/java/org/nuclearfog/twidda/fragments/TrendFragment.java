package org.nuclearfog.twidda.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.nuclearfog.twidda.activities.SearchPage;
import org.nuclearfog.twidda.adapter.TrendAdapter;
import org.nuclearfog.twidda.adapter.TrendAdapter.TrendClickListener;
import org.nuclearfog.twidda.backend.TrendLoader;
import org.nuclearfog.twidda.backend.apiold.EngineException;
import org.nuclearfog.twidda.backend.utils.ErrorHandler;
import org.nuclearfog.twidda.model.Trend;

import java.util.List;

import static android.os.AsyncTask.Status.RUNNING;
import static org.nuclearfog.twidda.activities.SearchPage.KEY_SEARCH_QUERY;

/**
 * Fragment class for trend lists
 *
 * @author nuclearfog
 */
public class TrendFragment extends ListFragment implements TrendClickListener {

    private TrendLoader trendTask;
    private TrendAdapter adapter;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new TrendAdapter(settings, this);
        setAdapter(adapter);
    }


    @Override
    public void onStart() {
        super.onStart();
        if (trendTask == null) {
            load();
            setRefresh(true);
        }
    }


    @Override
    protected void onReset() {
        if (adapter != null) {
            adapter.clear();
        }
        load();
        setRefresh(true);
    }


    @Override
    public void onDestroy() {
        if (trendTask != null && trendTask.getStatus() == RUNNING)
            trendTask.cancel(true);
        super.onDestroy();
    }


    @Override
    protected void onReload() {
        if (trendTask != null && trendTask.getStatus() != RUNNING) {
            load();
        }
    }


    @Override
    public void onTrendClick(Trend trend) {
        if (!isRefreshing()) {
            Intent intent = new Intent(requireContext(), SearchPage.class);
            String name = trend.getName();
            if (!name.startsWith("\"") && !name.endsWith("\""))
                name = "\"" + name + "\"";
            intent.putExtra(KEY_SEARCH_QUERY, name);
            startActivity(intent);
        }
    }

    /**
     * set trend data to list
     *
     * @param data Trend data
     */
    public void setData(List<Trend> data) {
        adapter.setData(data);
        setRefresh(false);
    }

    /**
     * check if list is empty
     *
     * @return true if list is empty
     */
    public boolean isEmpty() {
        return adapter.isEmpty();
    }

    /**
     * called from {@link TrendLoader} if an error occurs
     *
     * @param error Twitter exception
     */
    public void onError(@Nullable ErrorHandler.TwitterError error) {
        ErrorHandler.handleFailure(requireContext(), error);
        setRefresh(false);
    }

    /**
     * load content into the list
     */
    private void load() {
        trendTask = new TrendLoader(this);
        trendTask.execute(settings.getTrendLocation().getId());
    }
}