package org.nuclearfog.twidda.backend.async;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.nuclearfog.twidda.database.AppDatabase;
import org.nuclearfog.twidda.model.lists.Accounts;

/**
 * backend loader to get login information of local accounts
 *
 * @author nuclearfog
 */
public class AccountLoader extends AsyncExecutor<AccountLoader.AccountParameter, AccountLoader.AccountResult> {

	private AppDatabase db;

	/**
	 *
	 */
	public AccountLoader(Context context) {
		db = new AppDatabase(context);
	}


	@Override
	protected AccountResult doInBackground(@NonNull AccountParameter request) {
		switch (request.mode) {
			case AccountParameter.LOAD:
				Accounts accounts = db.getLogins();
				return new AccountResult(AccountResult.LOAD, 0L, accounts);

			case AccountParameter.DELETE:
				db.removeLogin(request.id);
				return new AccountResult(AccountResult.DELETE, request.id, null);

			default:
				return null;
		}
	}

	/**
	 *
	 */
	public static class AccountParameter {

		public static final int LOAD = 1;
		public static final int DELETE = 2;

		final int mode;
		final long id;

		public AccountParameter(int mode, long id) {
			this.mode = mode;
			this.id = id;
		}
	}

	/**
	 *
	 */
	public static class AccountResult {

		public static final int ERROR = -1;
		public static final int LOAD = 3;
		public static final int DELETE = 4;

		@Nullable
		public final Accounts accounts;
		public final int mode;
		public final long id;

		AccountResult(int mode, long id, @Nullable Accounts accounts) {
			this.accounts = accounts;
			this.mode = mode;
			this.id = id;
		}
	}
}