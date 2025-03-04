package org.nuclearfog.twidda.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import org.nuclearfog.twidda.R;
import org.nuclearfog.twidda.backend.utils.AppStyles;
import org.nuclearfog.twidda.database.GlobalSettings;
import org.nuclearfog.twidda.ui.fragments.AccountFragment;
import org.nuclearfog.twidda.ui.fragments.ListFragment;

/**
 * account manager activity
 *
 * @author nuclearfog
 */
public class AccountActivity extends AppCompatActivity {

	/**
	 * request code to start {@link LoginActivity}
	 */
	private static final int REQUEST_LOGIN = 0xDF14;

	/**
	 * return code to notify that a new account was selected
	 */
	public static final int RETURN_ACCOUNT_CHANGED = 0x3660;

	/**
	 * return code to notify if settings may changed
	 */
	public static final int RETURN_SETTINGS_CHANGED = 0x336;

	/**
	 * key to disable account selector option from menu
	 * value type is Boolean
	 */
	public static final String KEY_DISABLE_SELECTOR = "disable-acc-manager";

	private GlobalSettings settings;
	private ListFragment fragment;
	private Toolbar tool;


	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(AppStyles.setFontScale(newBase));
	}


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_fragment);
		ViewGroup root = findViewById(R.id.fragment_root);
		tool = findViewById(R.id.fragment_toolbar);
		fragment = new AccountFragment();

		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.fragment_container, fragment);
		fragmentTransaction.commit();

		tool.setTitle(R.string.account_page);
		setSupportActionBar(tool);

		settings = GlobalSettings.getInstance(this);
		AppStyles.setTheme(root, settings.getBackgroundColor());
	}


	@Override
	public boolean onCreateOptionsMenu(@NonNull Menu m) {
		getMenuInflater().inflate(R.menu.accounts, m);
		// disable account selector icon if this activity started from LoginActivity
		boolean disableSelector = getIntent().getBooleanExtra(KEY_DISABLE_SELECTOR, false);
		m.findItem(R.id.action_add_account).setVisible(!disableSelector);
		// theme icons
		AppStyles.setMenuIconColor(m, settings.getIconColor());
		return super.onCreateOptionsMenu(m);
	}


	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == R.id.action_add_account) {
			// open login page to add new account
			Intent loginIntent = new Intent(this, LoginActivity.class);
			startActivityForResult(loginIntent, REQUEST_LOGIN);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_LOGIN) {
			// new account registered, reload fragment
			if (resultCode == LoginActivity.RETURN_LOGIN_SUCCESSFUL) {
				setResult(AccountActivity.RETURN_ACCOUNT_CHANGED);
				fragment.reset();
			}
			// check if setting page was opened and reload theme
			else if (resultCode == LoginActivity.RETURN_SETTINGS_CHANGED) {
				AppStyles.setTheme(tool, settings.getBackgroundColor());
				setResult(RETURN_SETTINGS_CHANGED);
				fragment.reset();
			}
		}
	}
}