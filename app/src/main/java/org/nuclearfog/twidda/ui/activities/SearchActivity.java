package org.nuclearfog.twidda.ui.activities;

import static org.nuclearfog.twidda.ui.activities.TweetEditor.KEY_TWEETPOPUP_TEXT;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener;
import com.google.android.material.tabs.TabLayout.Tab;

import org.nuclearfog.twidda.R;
import org.nuclearfog.twidda.adapter.FragmentAdapter;
import org.nuclearfog.twidda.backend.utils.AppStyles;
import org.nuclearfog.twidda.database.GlobalSettings;

/**
 * Twitter search Activity
 *
 * @author nuclearfog
 */
public class SearchActivity extends AppCompatActivity implements OnTabSelectedListener, OnQueryTextListener {

	/**
	 * Key for the search query, required
	 * value type is String
	 */
	public static final String KEY_SEARCH_QUERY = "search_query";

	public static final int SEARCH_STR_MAX_LEN = 128;

	private FragmentAdapter adapter;
	private GlobalSettings settings;
	private TabLayout tabLayout;
	private ViewPager pager;
	private Toolbar toolbar;

	private String search = "";

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(AppStyles.setFontScale(newBase));
	}


	@Override
	protected void onCreate(@Nullable Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.page_search);
		ViewGroup root = findViewById(R.id.search_layout);
		toolbar = findViewById(R.id.search_toolbar);
		tabLayout = findViewById(R.id.search_tab);
		pager = findViewById(R.id.search_pager);

		toolbar.setTitle("");
		setSupportActionBar(toolbar);

		settings = GlobalSettings.getInstance(this);
		adapter = new FragmentAdapter(getSupportFragmentManager());
		tabLayout.setupWithViewPager(pager);
		tabLayout.addOnTabSelectedListener(this);
		pager.setAdapter(adapter);

		search = getIntent().getStringExtra(KEY_SEARCH_QUERY);
		adapter.setupSearchPage(search);
		AppStyles.setTabIcons(tabLayout, settings, R.array.search_tab_icons);
		AppStyles.setTheme(root, settings.getBackgroundColor());
	}


	@Override
	public void onBackPressed() {
		if (tabLayout.getSelectedTabPosition() > 0) {
			pager.setCurrentItem(0);
		} else {
			super.onBackPressed();
		}
	}


	@Override
	public boolean onCreateOptionsMenu(@NonNull Menu m) {
		getMenuInflater().inflate(R.menu.search, m);
		AppStyles.setMenuIconColor(m, settings.getIconColor());
		MenuItem searchItem = m.findItem(R.id.new_search);
		MenuItem searchFilter = m.findItem(R.id.search_filter);
		SearchView searchView = (SearchView) searchItem.getActionView();
		searchFilter.setChecked(settings.filterResults());
		searchView.setQueryHint(search);
		searchView.setOnQueryTextListener(this);
		AppStyles.setTheme(searchView, Color.TRANSPARENT);
		AppStyles.setOverflowIcon(toolbar, settings.getIconColor());
		return super.onCreateOptionsMenu(m);
	}


	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		// write new tweet
		if (item.getItemId() == R.id.search_tweet) {
			Intent intent = new Intent(this, TweetEditor.class);
			if (search.startsWith("#"))
				intent.putExtra(KEY_TWEETPOPUP_TEXT, search + " ");
			startActivity(intent);
		}
		// theme expanded search view
		else if (item.getItemId() == R.id.new_search) {
			SearchView searchView = (SearchView) item.getActionView();
			AppStyles.setTheme(searchView, Color.TRANSPARENT);
		}
		// enable/disable search filter
		else if (item.getItemId() == R.id.search_filter) {
			boolean enable = !item.isChecked();
			settings.setFilterResults(enable);
			item.setChecked(enable);
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public boolean onQueryTextSubmit(String s) {
		if (s.length() <= SearchActivity.SEARCH_STR_MAX_LEN && !s.contains(":") && !s.contains("$")) {
			Intent search = new Intent(this, SearchActivity.class);
			search.putExtra(KEY_SEARCH_QUERY, s);
			startActivity(search);
		} else {
			Toast.makeText(this, R.string.error_twitter_search, Toast.LENGTH_SHORT).show();
		}
		return true;
	}


	@Override
	public boolean onQueryTextChange(String s) {
		return false;
	}


	@Override
	public void onTabSelected(Tab tab) {
		invalidateOptionsMenu();
	}


	@Override
	public void onTabUnselected(Tab tab) {
		adapter.scrollToTop(tab.getPosition());
	}


	@Override
	public void onTabReselected(Tab tab) {
		adapter.scrollToTop(tab.getPosition());
	}
}