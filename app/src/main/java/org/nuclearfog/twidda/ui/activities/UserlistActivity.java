package org.nuclearfog.twidda.ui.activities;

import static android.os.AsyncTask.Status.RUNNING;
import static org.nuclearfog.twidda.ui.activities.UserlistEditor.KEY_LIST_EDITOR_DATA;

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
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener;
import com.google.android.material.tabs.TabLayout.Tab;

import org.nuclearfog.twidda.R;
import org.nuclearfog.twidda.adapter.FragmentAdapter;
import org.nuclearfog.twidda.backend.api.ConnectionException;
import org.nuclearfog.twidda.backend.async.ListAction;
import org.nuclearfog.twidda.backend.async.ListManager;
import org.nuclearfog.twidda.backend.utils.AppStyles;
import org.nuclearfog.twidda.backend.utils.ErrorHandler;
import org.nuclearfog.twidda.database.GlobalSettings;
import org.nuclearfog.twidda.model.User;
import org.nuclearfog.twidda.model.UserList;
import org.nuclearfog.twidda.ui.dialogs.ConfirmDialog;
import org.nuclearfog.twidda.ui.dialogs.ConfirmDialog.OnConfirmListener;
import org.nuclearfog.twidda.ui.fragments.UserFragment;

import java.util.regex.Pattern;

/**
 * This activity shows content of an user list
 * like timeline, list member and subscriber
 *
 * @author nuclearfog
 */
public class UserlistActivity extends AppCompatActivity implements OnTabSelectedListener, OnQueryTextListener, OnConfirmListener {

	/**
	 * key to add list information
	 * value type is {@link UserList}
	 */
	public static final String KEY_LIST_DATA = "list_data";

	/**
	 * key to disable list update
	 * value type is boolean
	 */
	public static final String KEY_LIST_NO_UPDATE = "list_no_update";

	/**
	 * result key to return the ID of a removed list
	 * value type is {@link UserList}
	 */
	public static final String RESULT_REMOVED_LIST_ID = "removed-list-id";

	/**
	 * result key to update an user list
	 * value type is {@link UserList}
	 */
	public static final String RESULT_UPDATE_LIST = "update-user-list";

	/**
	 * Request code for {@link UserlistEditor}
	 */
	private static final int REQUEST_LIST_UPDATED = 0x7518;

	/**
	 * return code when an user list was deleted
	 */
	public static final int RETURN_LIST_REMOVED = 0xDAD518B4;

	/**
	 * return code when an user list was deleted
	 */
	public static final int RETURN_LIST_UPDATED = 0x5D0F5E8D;

	/**
	 * regex pattern to validate username
	 */
	private static final Pattern USERNAME_PATTERN = Pattern.compile("@?\\w{1,15}");

	private FragmentAdapter adapter;
	private ListAction listLoaderAsync;
	private ListManager listManagerAsync;

	private GlobalSettings settings;

	private ConfirmDialog confirmDialog;

	private TabLayout tablayout;
	private ViewPager pager;
	private Toolbar toolbar;

	@Nullable
	private UserList userList;
	@Nullable
	private User user;


	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(AppStyles.setFontScale(newBase));
	}


	@Override
	protected void onCreate(@Nullable Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.page_listdetail);
		ViewGroup root = findViewById(R.id.listdetail_root);
		toolbar = findViewById(R.id.listdetail_toolbar);
		tablayout = findViewById(R.id.listdetail_tab);
		pager = findViewById(R.id.listdetail_pager);

		confirmDialog = new ConfirmDialog(this);
		adapter = new FragmentAdapter(this, getSupportFragmentManager());
		pager.setOffscreenPageLimit(3);
		pager.setAdapter(adapter);
		tablayout.setupWithViewPager(pager);
		tablayout.addOnTabSelectedListener(this);

		Object data = getIntent().getSerializableExtra(KEY_LIST_DATA);
		if (data instanceof UserList) {
			userList = (UserList) data;
			toolbar.setTitle(userList.getTitle());
			toolbar.setSubtitle(userList.getDescription());
			boolean isOwner = userList.getListOwner() != null && userList.getListOwner().isCurrentUser();
			adapter.setupListContentPage(userList.getId(), isOwner);
		}

		setSupportActionBar(toolbar);
		settings = GlobalSettings.getInstance(this);
		AppStyles.setTheme(root);
		AppStyles.setTabIcons(tablayout, settings, R.array.list_tab_icons);

		confirmDialog.setConfirmListener(this);
	}


	@Override
	protected void onStart() {
		super.onStart();
		if (listLoaderAsync == null && userList != null) {
			boolean blockUpdate = getIntent().getBooleanExtra(KEY_LIST_NO_UPDATE, false);
			if (!blockUpdate) {
				// update list information
				listLoaderAsync = new ListAction(this, userList.getId(), ListAction.LOAD);
				listLoaderAsync.execute();
			}
		}
	}


	@Override
	protected void onDestroy() {
		if (listLoaderAsync != null && listLoaderAsync.getStatus() == RUNNING) {
			listLoaderAsync.cancel(true);
		}
		super.onDestroy();
	}


	@Override
	public boolean onCreateOptionsMenu(@NonNull Menu m) {
		getMenuInflater().inflate(R.menu.userlist, m);
		AppStyles.setMenuIconColor(m, settings.getIconColor());
		AppStyles.setOverflowIcon(toolbar, settings.getIconColor());
		return super.onCreateOptionsMenu(m);
	}


	@Override
	public boolean onPrepareOptionsMenu(Menu m) {
		MenuItem editList = m.findItem(R.id.menu_list_edit);
		MenuItem deleteList = m.findItem(R.id.menu_delete_list);
		MenuItem followList = m.findItem(R.id.menu_follow_list);
		MenuItem search = m.findItem(R.id.menu_list_add_user);
		SearchView searchUser = (SearchView) search.getActionView();
		AppStyles.setTheme(searchUser, Color.TRANSPARENT);
		if (userList != null) {
			if (userList.getListOwner() != null && userList.getListOwner().isCurrentUser()) {
				searchUser.setQueryHint(getString(R.string.menu_add_user));
				searchUser.setOnQueryTextListener(this);
				editList.setVisible(true);
				deleteList.setVisible(true);
				search.setVisible(true);
			} else {
				followList.setVisible(true);
				if (userList.isFollowing()) {
					followList.setTitle(R.string.menu_unfollow_list);
				} else {
					followList.setTitle(R.string.menu_list_follow);
				}
			}
		}
		return super.onPrepareOptionsMenu(m);
	}


	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (userList != null && (listLoaderAsync == null || listLoaderAsync.getStatus() != RUNNING)) {
			// open user list editor
			if (item.getItemId() == R.id.menu_list_edit) {
				Intent editList = new Intent(this, UserlistEditor.class);
				editList.putExtra(KEY_LIST_EDITOR_DATA, userList);
				startActivityForResult(editList, REQUEST_LIST_UPDATED);
			}
			// delete user list
			else if (item.getItemId() == R.id.menu_delete_list) {
				confirmDialog.show(ConfirmDialog.LIST_DELETE);
			}
			// follow user list
			else if (item.getItemId() == R.id.menu_follow_list) {
				if (userList.isFollowing()) {
					confirmDialog.show(ConfirmDialog.LIST_UNFOLLOW);
				} else {
					listLoaderAsync = new ListAction(this, userList.getId(), ListAction.FOLLOW);
					listLoaderAsync.execute();
				}
			}
			// theme expanded search view
			else if (item.getItemId() == R.id.menu_list_add_user) {
				SearchView searchView = (SearchView) item.getActionView();
				AppStyles.setTheme(searchView, Color.TRANSPARENT);
			}
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onBackPressed() {
		if (tablayout.getSelectedTabPosition() > 0) {
			pager.setCurrentItem(0);
		} else {
			Intent result = new Intent();
			result.putExtra(RESULT_UPDATE_LIST, userList);
			setResult(RETURN_LIST_UPDATED, result);
			super.onBackPressed();
		}
	}


	@Override
	public void onActivityResult(int reqCode, int returnCode, @Nullable Intent result) {
		super.onActivityResult(reqCode, returnCode, result);
		if (reqCode == REQUEST_LIST_UPDATED && result != null) {
			if (returnCode == UserlistEditor.RETURN_LIST_CHANGED) {
				Object data = result.getSerializableExtra(UserlistEditor.KEY_UPDATED_USERLIST);
				if (data instanceof UserList) {
					userList = (UserList) data;
					toolbar.setTitle(userList.getTitle());
					toolbar.setSubtitle(userList.getDescription());
					invalidateOptionsMenu();
				}
			}
		}
	}


	@Override
	public void onConfirm(int type, boolean rememberChoice) {
		if (userList != null) {
			// delete user list
			if (type == ConfirmDialog.LIST_DELETE) {
				if (listLoaderAsync == null || listLoaderAsync.getStatus() != RUNNING) {
					listLoaderAsync = new ListAction(this, userList.getId(), ListAction.DELETE);
					listLoaderAsync.execute();
				}
			}
			// unfollow user list
			else if (type == ConfirmDialog.LIST_UNFOLLOW) {
				if (listLoaderAsync == null || listLoaderAsync.getStatus() != RUNNING) {
					listLoaderAsync = new ListAction(this, userList.getId(), ListAction.UNFOLLOW);
					listLoaderAsync.execute();
				}
			}
			// remove user from list
			else if (type == ConfirmDialog.LIST_REMOVE_USER) {
				if ((listManagerAsync == null || listManagerAsync.getStatus() != RUNNING) && user != null) {
					listManagerAsync = new ListManager(this, userList.getId(), ListManager.DEL_USER, user.getScreenname(), this);
					listManagerAsync.execute();
				}
			}
		}
	}


	@Override
	public void onTabSelected(Tab tab) {
	}


	@Override
	public void onTabUnselected(Tab tab) {
		adapter.scrollToTop(tab.getPosition());
	}


	@Override
	public void onTabReselected(Tab tab) {
		adapter.scrollToTop(tab.getPosition());
	}


	@Override
	public boolean onQueryTextChange(String newText) {
		return false;
	}


	@Override
	public boolean onQueryTextSubmit(String query) {
		if (userList == null)
			return false;
		if (USERNAME_PATTERN.matcher(query).matches()) {
			if (listManagerAsync == null || listManagerAsync.getStatus() != RUNNING) {
				Toast.makeText(this, R.string.info_adding_user_to_list, Toast.LENGTH_SHORT).show();
				listManagerAsync = new ListManager(this, userList.getId(), ListManager.ADD_USER, query, this);
				listManagerAsync.execute();
				return true;
			}
		} else {
			Toast.makeText(this, R.string.error_username_format, Toast.LENGTH_SHORT).show();
		}
		return false;
	}

	/**
	 * called from {@link ListManager}
	 *
	 * @param action what action was taken
	 * @param name   screen name of the list member
	 */
	public void onSuccess(int action, String name) {
		switch (action) {
			case ListManager.ADD_USER:
				if (!name.startsWith("@"))
					name = '@' + name;
				String info = getString(R.string.info_user_added_to_list, name);
				Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
				invalidateOptionsMenu();
				break;

			case ListManager.DEL_USER:
				if (user != null) {
					info = getString(R.string.info_user_removed, user.getScreenname());
					Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
					// remove user from list member page
					Fragment fragment = adapter.getItem(1);
					if (fragment instanceof UserFragment) {
						UserFragment callback = (UserFragment) fragment;
						callback.removeUser(user);
					}
				}
				break;
		}
	}

	/**
	 * called from {@link ListManager} when an error occurs
	 */
	public void onFailure(@Nullable ConnectionException exception) {
		ErrorHandler.handleFailure(this, exception);
	}

	/**
	 * called from {@link org.nuclearfog.twidda.ui.fragments.UserFragment} when an user should be removed from a list
	 *
	 * @param user user to remove from the lsit
	 */
	public void onDelete(User user) {
		if (!confirmDialog.isShowing()) {
			confirmDialog.show(ConfirmDialog.LIST_REMOVE_USER);
			this.user = user;
		}
	}

	/**
	 * called from {@link ListAction} to update userlist information
	 *
	 * @param userList userlist update
	 */
	public void onSuccess(UserList userList, int action) {
		this.userList = userList;
		switch (action) {
			case ListAction.LOAD:
				toolbar.setTitle(userList.getTitle());
				toolbar.setSubtitle(userList.getDescription());
				invalidateOptionsMenu();
				break;

			case ListAction.FOLLOW:
				Toast.makeText(this, R.string.info_list_followed, Toast.LENGTH_SHORT).show();
				invalidateOptionsMenu();
				break;

			case ListAction.UNFOLLOW:
				Toast.makeText(this, R.string.info_list_unfollowed, Toast.LENGTH_SHORT).show();
				invalidateOptionsMenu();
				break;

			case ListAction.DELETE:
				Intent result = new Intent();
				result.putExtra(RESULT_REMOVED_LIST_ID, userList.getId());
				setResult(RETURN_LIST_REMOVED, result);
				Toast.makeText(this, R.string.info_list_removed, Toast.LENGTH_SHORT).show();
				finish();
				break;
		}
	}

	/**
	 * called from {@link ListAction} if an error occurs
	 *
	 * @param err    error information
	 * @param listId ID of the list where an error occurred
	 */
	public void onFailure(@Nullable ConnectionException err, long listId) {
		ErrorHandler.handleFailure(this, err);
		if (err != null && err.getErrorCode() == ConnectionException.RESOURCE_NOT_FOUND) {
			// List does not exist
			Intent result = new Intent();
			result.putExtra(RESULT_REMOVED_LIST_ID, listId);
			setResult(RETURN_LIST_REMOVED, result);
			finish();
		}
	}
}