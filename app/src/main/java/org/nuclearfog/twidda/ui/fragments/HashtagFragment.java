package org.nuclearfog.twidda.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.nuclearfog.twidda.backend.async.AsyncExecutor.AsyncCallback;
import org.nuclearfog.twidda.backend.async.TrendLoader;
import org.nuclearfog.twidda.backend.async.TrendLoader.TrendParameter;
import org.nuclearfog.twidda.backend.async.TrendLoader.TrendResult;
import org.nuclearfog.twidda.backend.utils.ErrorUtils;
import org.nuclearfog.twidda.model.Hashtag;
import org.nuclearfog.twidda.model.lists.Trends;
import org.nuclearfog.twidda.ui.activities.SearchActivity;
import org.nuclearfog.twidda.ui.adapter.recyclerview.TrendAdapter;
import org.nuclearfog.twidda.ui.adapter.recyclerview.TrendAdapter.TrendClickListener;

import java.io.Serializable;

/**
 * Fragment class to show a list of trends
 *
 * @author nuclearfog
 */
public class HashtagFragment extends ListFragment implements TrendClickListener, AsyncCallback<TrendResult>, ActivityResultCallback<ActivityResult> {

	/**
	 * setup fragment to show popular trends of an instance/location
	 */
	public static final int MODE_POPULAR = 0x32105718;

	/**
	 * setup fragment to show hashtags relating to search
	 * requires {@link #KEY_SEARCH}
	 */
	public static final int MODE_SEARCH = 0x17210512;

	/**
	 * setup fragment to show hashtags followed by the current user
	 */
	public static final int MODE_FOLLOW = 0x50545981;

	/**
	 * setup fragment to view featured hashtags
	 */
	public static final int MODE_FEATURE = 0x16347583;

	/**
	 * key used to define what type of trends should be shown, see {@link #MODE_FOLLOW ,#MODE_POPULAR ,#KEY_FRAGMENT_TREND_SEARCH}
	 * value type is Integer
	 */
	public static final String KEY_MODE = "fragment_trend_mode";

	/**
	 * (optional) key to search for trends and hashtag matching a search string
	 * value type is String
	 */
	public static final String KEY_SEARCH = "fragment_trend_search";

	/**
	 * bundle key to add adapter items
	 * value type is {@link Trends}
	 */
	private static final String KEY_DATA = "fragment_trend_data";

	private ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this);

	private TrendLoader trendLoader;
	private TrendAdapter adapter;

	private int mode = 0;
	private String search = "";


	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		adapter = new TrendAdapter(this);
		trendLoader = new TrendLoader(requireContext());
		setAdapter(adapter);

		Bundle args = getArguments();
		if (args != null) {
			search = args.getString(KEY_SEARCH, "");
			mode = args.getInt(KEY_MODE, 0);
		}
		if (savedInstanceState != null) {
			Serializable data = savedInstanceState.getSerializable(KEY_DATA);
			if (data instanceof Trends) {
				adapter.addItems((Trends) data, TrendAdapter.CLEAR_LIST);
				return;
			}
		}
		setRefresh(true);
		load(TrendParameter.NO_CURSOR, TrendAdapter.CLEAR_LIST);
	}


	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		outState.putSerializable(KEY_DATA, adapter.getItems());
		super.onSaveInstanceState(outState);
	}


	@Override
	public void onDestroy() {
		trendLoader.cancel();
		super.onDestroy();
	}


	@Override
	protected void onReset() {
		adapter.clear();
		trendLoader = new TrendLoader(requireContext());
		load(TrendParameter.NO_CURSOR, TrendAdapter.CLEAR_LIST);
		setRefresh(true);
	}


	@Override
	protected void onReload() {
		load(TrendParameter.NO_CURSOR, TrendAdapter.CLEAR_LIST);
	}


	@Override
	public void onActivityResult(ActivityResult result) {
		if (result.getResultCode() == SearchActivity.RETURN_TREND) {
			if (result.getData() != null) {
				Serializable data = result.getData().getSerializableExtra(SearchActivity.KEY_DATA);
				if (data instanceof Hashtag) {
					Hashtag update = (Hashtag) data;
					// remove hashtag if unfollowed
					if (mode == MODE_FOLLOW && !update.following()) {
						adapter.removeItem(update);
					}
				}
			}
		}
	}


	@Override
	public void onTrendClick(Hashtag hashtag) {
		if (!isRefreshing()) {
			Intent intent = new Intent(requireContext(), SearchActivity.class);
			String name = hashtag.getName();
			if (!name.startsWith("#") && !name.startsWith("\"") && !name.endsWith("\"")) {
				name = "\"" + name + "\"";
				intent.putExtra(SearchActivity.KEY_QUERY, name);
			} else {
				intent.putExtra(SearchActivity.KEY_DATA, hashtag);
			}
			activityResultLauncher.launch(intent);
		}
	}


	@Override
	public boolean onPlaceholderClick(long cursor, int index) {
		if (trendLoader.isIdle()) {
			load(cursor, index);
			return true;
		}
		return false;
	}


	@Override
	public void onResult(@NonNull TrendResult result) {
		if (result.mode == TrendResult.ERROR) {
			if (getContext() != null) {
				ErrorUtils.showErrorMessage(getContext(), result.exception);
			}
			adapter.disableLoading();
		} else {
			adapter.addItems(result.trends, result.index);
		}
		setRefresh(false);
	}

	/**
	 * load content into the list
	 */
	private void load(long cursor, int index) {
		TrendParameter param;
		switch (mode) {
			case MODE_POPULAR:
				if (adapter.isEmpty()) {
					param = new TrendParameter(TrendParameter.POPULAR_OFFLINE, index, search, cursor);
				} else {
					param = new TrendParameter(TrendParameter.POPULAR_ONLINE, index, search, cursor);
				}
				trendLoader.execute(param, this);
				break;

			case MODE_FOLLOW:
				param = new TrendParameter(TrendParameter.FOLLOWING, index, search, cursor);
				trendLoader.execute(param, this);
				break;

			case MODE_FEATURE:
				param = new TrendParameter(TrendParameter.FEATURING, index, search, cursor);
				trendLoader.execute(param, this);
				break;

			case MODE_SEARCH:
				param = new TrendParameter(TrendParameter.SEARCH, index, search, cursor);
				trendLoader.execute(param, this);
				break;
		}
	}
}