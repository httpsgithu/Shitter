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
public class UsersLoader extends AsyncExecutor<UsersLoader.Param, UsersLoader.Result> {

	private Connection connection;

	/**
	 *
	 */
	public UsersLoader(Context context) {
		connection = ConnectionManager.getDefaultConnection(context);
	}


	@Override
	protected Result doInBackground(@NonNull Param param) {
		try {
			switch (param.type) {
				case Param.FOLLOWS:
					Users users = connection.getFollower(param.id, param.cursor);
					return new Result(users, param.index, null);

				case Param.FRIENDS:
					users = connection.getFollowing(param.id, param.cursor);
					return new Result(users, param.index, null);

				case Param.REPOST:
					users = connection.getRepostingUsers(param.id, param.cursor);
					return new Result(users, param.index, null);

				case Param.FAVORIT:
					users = connection.getFavoritingUsers(param.id, param.cursor);
					return new Result(users, param.index, null);

				case Param.SEARCH:
					users = connection.searchUsers(param.search, param.cursor);
					return new Result(users, param.index, null);

				case Param.SUBSCRIBER:
					users = connection.getListSubscriber(param.id, param.cursor);
					return new Result(users, param.index, null);

				case Param.LISTMEMBER:
					users = connection.getListMember(param.id, param.cursor);
					return new Result(users, param.index, null);

				case Param.BLOCK:
					users = connection.getBlockedUsers(param.cursor);
					return new Result(users, param.index, null);

				case Param.MUTE:
					users = connection.getMutedUsers(param.cursor);
					return new Result(users, param.index, null);

				case Param.REQUEST_IN:
					users = connection.getIncomingFollowRequests(param.cursor);
					return new Result(users, param.index, null);

				case Param.REQUEST_OUT:
					users = connection.getOutgoingFollowRequests(param.cursor);
					return new Result(users, param.index, null);

				default:
					return null;
			}
		} catch (ConnectionException exception) {
			return new Result(null, param.index, exception);
		}
	}

	/**
	 *
	 */
	public static class Param {

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

		public Param(int type, int index, long id, long cursor, String search) {
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
	public static class Result {

		@Nullable
		public final Users users;
		@Nullable
		public final ConnectionException exception;
		public final int index;

		Result(@Nullable Users users, int index, @Nullable ConnectionException exception) {
			this.users = users;
			this.index = index;
			this.exception = exception;
		}
	}
}