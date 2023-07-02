package org.nuclearfog.twidda.backend.async;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.nuclearfog.twidda.backend.api.Connection;
import org.nuclearfog.twidda.backend.api.ConnectionException;
import org.nuclearfog.twidda.backend.api.ConnectionManager;
import org.nuclearfog.twidda.model.lists.Users;
import org.nuclearfog.twidda.ui.fragments.UserFragment;

/**
 * download a list of user such as follower, following or searched users
 *
 * @author nuclearfog
 * @see UserFragment
 */
public class UsersLoader extends AsyncExecutor<UsersLoader.UserParam, UsersLoader.UserResult> {

	private Connection connection;

	/**
	 *
	 */
	public UsersLoader(Context context) {
		connection = ConnectionManager.getDefaultConnection(context);
	}


	@Override
	protected UserResult doInBackground(@NonNull UserParam param) {
		try {
			switch (param.type) {
				case UserParam.FOLLOWS:
					Users users = connection.getFollower(param.id, param.cursor);
					return new UserResult(users, param.index, null);

				case UserParam.FRIENDS:
					users = connection.getFollowing(param.id, param.cursor);
					return new UserResult(users, param.index, null);

				case UserParam.REPOST:
					users = connection.getRepostingUsers(param.id, param.cursor);
					return new UserResult(users, param.index, null);

				case UserParam.FAVORIT:
					users = connection.getFavoritingUsers(param.id, param.cursor);
					return new UserResult(users, param.index, null);

				case UserParam.SEARCH:
					users = connection.searchUsers(param.search, param.cursor);
					return new UserResult(users, param.index, null);

				case UserParam.SUBSCRIBER:
					users = connection.getListSubscriber(param.id, param.cursor);
					return new UserResult(users, param.index, null);

				case UserParam.LISTMEMBER:
					users = connection.getListMember(param.id, param.cursor);
					return new UserResult(users, param.index, null);

				case UserParam.BLOCK:
					users = connection.getBlockedUsers(param.cursor);
					return new UserResult(users, param.index, null);

				case UserParam.MUTE:
					users = connection.getMutedUsers(param.cursor);
					return new UserResult(users, param.index, null);

				case UserParam.REQUEST_IN:
					users = connection.getIncomingFollowRequests(param.cursor);
					return new UserResult(users, param.index, null);

				case UserParam.REQUEST_OUT:
					users = connection.getOutgoingFollowRequests(param.cursor);
					return new UserResult(users, param.index, null);

				default:
					return null;
			}
		} catch (ConnectionException exception) {
			return new UserResult(null, param.index, exception);
		}
	}

	/**
	 *
	 */
	public static class UserParam {

		public static final long NO_CURSOR = -1L;

		public static final int FOLLOWS = 1;
		public static final int FRIENDS = 2;
		public static final int REPOST = 3;
		public static final int FAVORIT = 4;
		public static final int SEARCH = 5;
		public static final int SUBSCRIBER = 6;
		public static final int LISTMEMBER = 7;
		public static final int BLOCK = 8;
		public static final int MUTE = 9;
		public static final int REQUEST_OUT = 10;
		public static final int REQUEST_IN = 11;

		final int type, index;
		final String search;
		final long id, cursor;

		public UserParam(int type, int index, long id, long cursor, String search) {
			this.type = type;
			this.index = index;
			this.id = id;
			this.cursor = cursor;
			this.search = search;
		}
	}

	/**
	 *
	 */
	public static class UserResult {

		@Nullable
		public final Users users;
		@Nullable
		public final ConnectionException exception;
		public final int index;

		UserResult(@Nullable Users users, int index, @Nullable ConnectionException exception) {
			this.users = users;
			this.index = index;
			this.exception = exception;
		}
	}
}