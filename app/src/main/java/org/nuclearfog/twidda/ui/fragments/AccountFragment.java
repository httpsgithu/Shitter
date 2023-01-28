package org.nuclearfog.twidda.ui.fragments;

import static android.os.AsyncTask.Status.RUNNING;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.nuclearfog.twidda.R;
import org.nuclearfog.twidda.adapter.AccountAdapter;
import org.nuclearfog.twidda.adapter.AccountAdapter.OnAccountClickListener;
import org.nuclearfog.twidda.backend.async.AccountLoader;
import org.nuclearfog.twidda.config.GlobalSettings;
import org.nuclearfog.twidda.model.Account;
import org.nuclearfog.twidda.ui.activities.AccountActivity;
import org.nuclearfog.twidda.ui.dialogs.ConfirmDialog;
import org.nuclearfog.twidda.ui.dialogs.ConfirmDialog.OnConfirmListener;

import java.util.List;

/**
 * fragment class to show registered accounts
 *
 * @author nuclearfog
 */
public class AccountFragment extends ListFragment implements OnAccountClickListener, OnConfirmListener {

	@Nullable
	private AccountLoader loginTask;
	private GlobalSettings settings;
	private AccountAdapter adapter;
	private ConfirmDialog dialog;

	private Account selection;


	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		dialog = new ConfirmDialog(requireContext());
		settings = GlobalSettings.getInstance(requireContext());
		adapter = new AccountAdapter(requireContext(), this);

		setAdapter(adapter);
		dialog.setConfirmListener(this);
	}


	@Override
	public void onStart() {
		super.onStart();
		if (loginTask == null) {
			setRefresh(true);
			loginTask = new AccountLoader(this, AccountLoader.MODE_LOAD);
			loginTask.execute();
		}
	}


	@Override
	public void onDestroy() {
		if (loginTask != null && loginTask.getStatus() == RUNNING)
			loginTask.cancel(true);
		super.onDestroy();
	}


	@Override
	protected void onReload() {
		loginTask = new AccountLoader(this, AccountLoader.MODE_LOAD);
		loginTask.execute();
	}


	@Override
	protected void onReset() {
		loginTask = new AccountLoader(this, AccountLoader.MODE_LOAD);
		loginTask.execute();
		setRefresh(true);
	}


	@Override
	public void onAccountClick(Account account) {
		settings.setLogin(account, true);
		if (account.getUser() != null) {
			String message = getString(R.string.info_account_selected, account.getUser().getScreenname());
			Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
		}
		// finish activity and return to parent activity
		requireActivity().setResult(AccountActivity.RETURN_ACCOUNT_CHANGED);
		requireActivity().finish();
	}


	@Override
	public void onAccountRemove(Account account) {
		if (!dialog.isShowing()) {
			selection = account;
			dialog.show(ConfirmDialog.REMOVE_ACCOUNT);
		}
	}


	@Override
	public void onConfirm(int type, boolean rememberChoice) {
		if (type == ConfirmDialog.REMOVE_ACCOUNT) {
			loginTask = new AccountLoader(this, AccountLoader.MODE_DELETE);
			loginTask.execute(selection.getId());
		}
	}

	/**
	 * called from {@link AccountLoader} to add accounts to list
	 *
	 * @param result login information
	 */
	public void onSuccess(@NonNull List<Account> result) {
		adapter.replaceItems(result);
		setRefresh(false);
	}

	/**
	 * called from {@link AccountLoader} to remove account from list
	 *
	 * @param id ID of the account to delete
	 */
	public void onDelete(long id) {
		adapter.removeItem(id);
	}

	/**
	 * called from {@link AccountLoader} when an error occurs
	 */
	public void onError() {
		Toast.makeText(requireContext(), R.string.error_acc_loading, Toast.LENGTH_SHORT).show();
	}
}