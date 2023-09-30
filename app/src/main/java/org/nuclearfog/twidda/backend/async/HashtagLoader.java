package org.nuclearfog.twidda.backend.async;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.nuclearfog.twidda.backend.api.Connection;
import org.nuclearfog.twidda.backend.api.ConnectionException;
import org.nuclearfog.twidda.backend.api.ConnectionManager;
import org.nuclearfog.twidda.database.AppDatabase;
import org.nuclearfog.twidda.model.lists.Trends;
import org.nuclearfog.twidda.ui.fragments.HashtagFragment;

/**
 * Background task to load a list of location specific trends
 *
 * @author nuclearfog
 * @see HashtagFragment
 */
public class HashtagLoader extends AsyncExecutor<HashtagLoader.Param, HashtagLoader.Result> {

	private Connection connection;
	private AppDatabase db;

	/**
	 *
	 */
	public HashtagLoader(Context context) {
		connection = ConnectionManager.getDefaultConnection(context);
		db = new AppDatabase(context);
	}


	@Override
	protected Result doInBackground(@NonNull Param param) {
		try {
			switch (param.mode) {
				case Param.POPULAR_OFFLINE:
					Trends trends = db.getTrends();
					if (!trends.isEmpty()) {
						return new Result(Result.POPULAR, trends, param.index, null);
					}
					// fall through

				case Param.POPULAR_ONLINE:
					trends = connection.getTrends();
					db.saveTrends(trends);
					return new Result(Result.POPULAR, trends, param.index, null);

				case Param.SEARCH:
					trends = connection.searchHashtags(param.trend);
					return new Result(Result.SEARCH, trends, param.index, null);

				case Param.FOLLOWING:
					trends = connection.showHashtagFollowing(param.cursor);
					return new Result(Result.FOLLOWING, trends, param.index, null);

				case Param.FEATURING:
					trends = connection.showHashtagFeaturing();
					return new Result(Result.FEATURING, trends, param.index, null);

				default:
					return null;
			}
		} catch (ConnectionException exception) {
			return new Result(Result.ERROR, null, param.index, exception);
		}
	}

	/**
	 *
	 */
	public static class Param {

		public static final int POPULAR_OFFLINE = 1;
		public static final int POPULAR_ONLINE = 2;
		public static final int SEARCH = 3;
		public static final int FOLLOWING = 4;
		public static final int FEATURING = 5;

		public static final long NO_CURSOR = 0L;

		final String trend;
		final int mode;
		final int index;
		final long cursor;

		public Param(int mode, int index, String trend, long cursor) {
			this.mode = mode;
			this.trend = trend;
			this.index = index;
			this.cursor = cursor;
		}
	}

	/**
	 *
	 */
	public static class Result {

		public static final int ERROR = -1;
		public static final int POPULAR = 20;
		public static final int SEARCH = 21;
		public static final int FOLLOWING = 22;
		public static final int FEATURING = 23;

		public final int mode;
		public final int index;
		@Nullable
		public final Trends trends;
		@Nullable
		public final ConnectionException exception;

		Result(int mode, @Nullable Trends trends, int index, @Nullable ConnectionException exception) {
			this.trends = trends;
			this.exception = exception;
			this.index = index;
			this.mode = mode;
		}
	}
}