package org.nuclearfog.twidda.backend.async;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.nuclearfog.twidda.backend.api.Connection;
import org.nuclearfog.twidda.backend.api.ConnectionException;
import org.nuclearfog.twidda.backend.api.ConnectionManager;
import org.nuclearfog.twidda.database.AppDatabase;
import org.nuclearfog.twidda.model.User;

/**
 * Async class to load user information
 *
 * @author nuclearfog
 */
public class UserLoader extends AsyncExecutor<UserLoader.UserParam, UserLoader.UserResult> {

	private Connection connection;
	private AppDatabase db;

	/**
	 *
	 */
	public UserLoader(Context context) {
		connection = ConnectionManager.getDefaultConnection(context);
		db = new AppDatabase(context);
	}


	@Override
	protected UserResult doInBackground(@NonNull UserParam param) {
		try {
			switch (param.mode) {
				case UserParam.DATABASE:
					User user = db.getUser(param.id);
					if (user != null) {
						return new UserResult(UserResult.DATABASE, user, null);
					}
					// fall through

				case UserParam.ONLINE:
					user = connection.showUser(param.id);
					db.saveUser(user);
					return new UserResult(UserResult.ONLINE, user, null);

				default:
					return null;
			}
		} catch (ConnectionException exception) {
			return new UserResult(UserResult.ERROR, null, exception);
		}
	}

	/**
	 *
	 */
	public static class UserParam {

		public static final int DATABASE = 1;
		public static final int ONLINE = 2;

		final int mode;
		final long id;

		public UserParam(int mode, long id) {
			this.mode = mode;
			this.id = id;
		}
	}

	/**
	 *
	 */
	public static class UserResult {

		public static final int ERROR = -1;
		public static final int DATABASE = 3;
		public static final int ONLINE = 4;

		@Nullable
		public final User user;
		@Nullable
		public final ConnectionException exception;
		public final int mode;

		UserResult(int mode, @Nullable User user, @Nullable ConnectionException exception) {
			this.mode = mode;
			this.user = user;
			this.exception = exception;
		}
	}
}