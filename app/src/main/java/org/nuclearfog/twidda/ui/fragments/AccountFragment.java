package org.nuclearfog.twidda.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.nuclearfog.twidda.R;
import org.nuclearfog.twidda.backend.async.AccountAction;
import org.nuclearfog.twidda.backend.async.AccountLoader;
import org.nuclearfog.twidda.backend.async.AsyncExecutor.AsyncCallback;
import org.nuclearfog.twidda.config.GlobalSettings;
import org.nuclearfog.twidda.model.Account;
import org.nuclearfog.twidda.model.lists.Accounts;
import org.nuclearfog.twidda.notification.PushSubscription;
import org.nuclearfog.twidda.ui.activities.AccountActivity;
import org.nuclearfog.twidda.ui.adapter.recyclerview.AccountAdapter;
import org.nuclearfog.twidda.ui.adapter.recyclerview.AccountAdapter.OnAccountClickListener;
import org.nuclearfog.twidda.ui.dialogs.ConfirmDialog;
import org.nuclearfog.twidda.ui.dialogs.ConfirmDialog.OnConfirmListener;

import java.io.Serializable;

/**
 * fragment class to show registered accounts
 *
 * @author nuclearfog
 */
public class AccountFragment extends ListFragment implements OnAccountClickListener, OnConfirmListener {

	/**
	 * internal Bundle key used to save adapter items
	 * value type is {@link Accounts}
	 */
	private static final String KEY_SAVE = "account-data";

	private AccountLoader accountLoader;
	private AccountAction accountAction;
	private GlobalSettings settings;
	private AccountAdapter adapter;
	private ConfirmDialog dialog;
	@Nullable
	private Account selection;

	private AsyncCallback<AccountLoader.Result> accountLoaderResult = this::onLoaderResult;
	private AsyncCallback<AccountAction.Result> accountActionResult = this::onActionResult;


	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		dialog = new ConfirmDialog(requireActivity(), this);
		settings = GlobalSettings.get(requireContext());
		accountLoader = new AccountLoader(requireContext());
		accountAction = new AccountAction(requireContext());
		adapter = new AccountAdapter(this);
		setAdapter(adapter);

		if (savedInstanceState != null) {
			Serializable data = savedInstanceState.getSerializable(KEY_SAVE);
			if (data instanceof Accounts) {
				adapter.replaceItems((Accounts) data);
				return;
			}
		}
		load();
		setRefresh(true);
	}


	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		outState.putSerializable(KEY_SAVE, adapter.getItems());
		super.onSaveInstanceState(outState);
	}


	@Override
	public void onDestroy() {
		accountLoader.cancel();
		super.onDestroy();
	}


	@Override
	protected void onReload() {
		load();
	}


	@Override
	protected void onReset() {
		adapter.clear();
		accountLoader = new AccountLoader(requireContext());
		load();
		setRefresh(true);
	}


	@Override
	public void onAccountClick(Account account) {
		if (accountAction.isIdle()) {
			AccountAction.Param param = new AccountAction.Param(AccountAction.Param.SELECT, account);
			accountAction.execute(param, accountActionResult);
		}
	}


	@Override
	public void onAccountRemove(Account account) {
		if (!dialog.isShowing() && accountLoader.isIdle() && accountAction.isIdle()) {
			selection = account;
			dialog.show(ConfirmDialog.REMOVE_ACCOUNT);
		}
	}


	@Override
	public void onConfirm(int type, boolean remember) {
		if (type == ConfirmDialog.REMOVE_ACCOUNT) {
			if (selection != null) {
				AccountAction.Param param = new AccountAction.Param(AccountAction.Param.REMOVE, selection);
				accountAction.execute(param, accountActionResult);
			}
		}
	}

	/**
	 *
	 */
	private void load() {
		accountLoader.execute(null, accountLoaderResult);
	}

	/**
	 *
	 */
	private void onLoaderResult(AccountLoader.Result result) {
		adapter.replaceItems(result.accounts);
		setRefresh(false);
	}

	/**
	 *
	 */
	private void onActionResult(AccountAction.Result result) {
		if (result.mode == AccountAction.Result.REMOVE) {
			adapter.removeItem(result.account);
		} else if (result.mode == AccountAction.Result.SELECT) {
			if (settings.pushEnabled()) {
				PushSubscription.subscripe(requireContext());
			}
			if (result.account.getUser() != null) {
				String message = getString(R.string.info_account_selected, result.account.getUser().getScreenname());
				Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
			}
			// set result to the parent activity
			Intent intent = new Intent();
			intent.putExtra(AccountActivity.RETURN_ACCOUNT, result.account);
			requireActivity().setResult(AccountActivity.RETURN_ACCOUNT_CHANGED, intent);
		}
	}
}