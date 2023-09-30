package org.nuclearfog.twidda.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import org.nuclearfog.twidda.R;
import org.nuclearfog.twidda.backend.utils.AppStyles;
import org.nuclearfog.twidda.ui.adapter.viewpager.ScheduleAdapter;

/**
 * @author nuclearfog
 */
public class ScheduleActivity extends AppCompatActivity {


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_tab_view);

		ViewGroup root = findViewById(R.id.page_tab_view_root);
		Toolbar toolbar = findViewById(R.id.page_tab_view_toolbar);
		ViewPager2 viewPager = findViewById(R.id.page_tab_view_pager);
		View tabSelector = findViewById(R.id.page_tab_view_tabs);

		ScheduleAdapter adapter = new ScheduleAdapter(this);
		viewPager.setAdapter(adapter);

		tabSelector.setVisibility(View.GONE);
		toolbar.setTitle(R.string.toolbar_schedule_title);
		setSupportActionBar(toolbar);
		AppStyles.setTheme(root);
	}
}