package org.nuclearfog.twidda.backend.async;

import static org.nuclearfog.twidda.ui.fragments.TweetFragment.CLEAR_LIST;

import android.os.AsyncTask;

import androidx.annotation.Nullable;

import org.nuclearfog.twidda.backend.api.Twitter;
import org.nuclearfog.twidda.backend.api.TwitterException;
import org.nuclearfog.twidda.backend.utils.ErrorHandler.TwitterError;
import org.nuclearfog.twidda.database.AppDatabase;
import org.nuclearfog.twidda.model.Tweet;
import org.nuclearfog.twidda.ui.fragments.TweetFragment;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Background task to download a list of tweets from different sources
 *
 * @author nuclearfog
 * @see TweetFragment
 */
public class TweetLoader extends AsyncTask<Long, Void, List<Tweet>> {

	/**
	 * tweets from home timeline
	 */
	public static final int TL_HOME = 1;

	/**
	 * tweets from the mention timeline
	 */
	public static final int TL_MENT = 2;

	/**
	 * tweets of an user
	 */
	public static final int USR_TWEETS = 3;

	/**
	 * favorite tweets of an user
	 */
	public static final int USR_FAVORS = 4;

	/**
	 * tweet replies to a tweet
	 */
	public static final int REPLIES = 5;

	/**
	 * tweet replies from database
	 */
	public static final int REPLIES_OFFLINE = 6;

	/**
	 * tweets from twitter search
	 */
	public static final int TWEET_SEARCH = 7;

	/**
	 * tweets from an userlist
	 */
	public static final int USERLIST = 8;


	@Nullable
	private TwitterError twException;
	private WeakReference<TweetFragment> weakRef;
	private Twitter twitter;
	private AppDatabase db;

	private int listType;
	private String search;
	private long id;
	private int pos;

	/**
	 * @param fragment callback to update tweet data
	 * @param listType type of tweet list to load
	 * @param id       ID, depending on what list type should be loaded
	 * @param search   search string if any
	 * @param pos      index of the list where tweets should be inserted
	 */
	public TweetLoader(TweetFragment fragment, int listType, long id, String search, int pos) {
		super();
		db = new AppDatabase(fragment.getContext());
		twitter = Twitter.get(fragment.getContext());
		weakRef = new WeakReference<>(fragment);

		this.listType = listType;
		this.search = search;
		this.id = id;
		this.pos = pos;
	}


	@Override
	protected List<Tweet> doInBackground(Long[] param) {
		List<Tweet> tweets = null;
		long sinceId = param[0];
		long maxId = param[1];
		try {
			switch (listType) {
				case TL_HOME:
					if (sinceId == 0 && maxId == 0) {
						tweets = db.getHomeTimeline();
						if (tweets.isEmpty()) {
							tweets = twitter.getHomeTimeline(sinceId, maxId);
							db.storeHomeTimeline(tweets);
						}
					} else if (sinceId > 0) {
						tweets = twitter.getHomeTimeline(sinceId, maxId);
						db.storeHomeTimeline(tweets);
					} else if (maxId > 1) {
						tweets = twitter.getHomeTimeline(sinceId, maxId);
					}
					break;

				case TL_MENT:
					if (sinceId == 0 && maxId == 0) {
						tweets = db.getMentions();
						if (tweets.isEmpty()) {
							tweets = twitter.getMentionTimeline(sinceId, maxId);
							db.storeMentions(tweets);
						}
					} else if (sinceId > 0) {
						tweets = twitter.getMentionTimeline(sinceId, maxId);
						db.storeMentions(tweets);
					} else if (maxId > 1) {
						tweets = twitter.getMentionTimeline(sinceId, maxId);
					}
					break;

				case USR_TWEETS:
					if (id > 0) {
						if (sinceId == 0 && maxId == 0) {
							tweets = db.getUserTweets(id);
							if (tweets.isEmpty()) {
								tweets = twitter.getUserTimeline(id, 0, maxId);
								db.storeUserTweets(tweets);
							}
						} else if (sinceId > 0) {
							tweets = twitter.getUserTimeline(id, sinceId, maxId);
							db.storeUserTweets(tweets);
						} else if (maxId > 1) {
							tweets = twitter.getUserTimeline(id, sinceId, maxId);
						}
					} else if (search != null) {
						tweets = twitter.getUserTimeline(search, sinceId, maxId);
					}
					break;

				case USR_FAVORS:
					if (id > 0) {
						if (sinceId == 0 && maxId == 0) {
							tweets = db.getUserFavorites(id);
							if (tweets.isEmpty()) {
								tweets = twitter.getUserFavorits(id, 0, maxId);
								db.storeUserFavs(tweets, id);
							}
						} else if (sinceId > 0) {
							tweets = twitter.getUserFavorits(id, 0, maxId);
							db.storeUserFavs(tweets, id);
							pos = CLEAR_LIST; // set flag to clear previous data
						} else if (maxId > 1) {
							tweets = twitter.getUserFavorits(id, sinceId, maxId);
						}
					} else if (search != null) {
						tweets = twitter.getUserFavorits(search, sinceId, maxId);
					}
					break;

				case REPLIES_OFFLINE:
					tweets = db.getTweetReplies(id);
					break;

				case REPLIES:
					if (sinceId == 0 && maxId == 0) {
						tweets = db.getTweetReplies(id);
						if (tweets.isEmpty()) {
							tweets = twitter.getTweetReplies(search, id, sinceId, maxId);
							if (!tweets.isEmpty() && db.containsTweet(id)) {
								db.storeReplies(tweets);
							}
						}
					} else if (sinceId > 0) {
						tweets = twitter.getTweetReplies(search, id, sinceId, maxId);
						if (!tweets.isEmpty() && db.containsTweet(id)) {
							db.storeReplies(tweets);
						}
					} else if (maxId > 1) {
						tweets = twitter.getTweetReplies(search, id, sinceId, maxId);
					}
					break;

				case TWEET_SEARCH:
					tweets = twitter.searchTweets(search, sinceId, maxId);
					break;

				case USERLIST:
					tweets = twitter.getUserlistTweets(id, sinceId, maxId);
					break;
			}
		} catch (TwitterException e) {
			this.twException = e;
		}
		return tweets;
	}


	@Override
	protected void onPostExecute(List<Tweet> tweets) {
		TweetFragment fragment = weakRef.get();
		if (fragment != null) {
			if (tweets != null) {
				fragment.setData(tweets, pos);
			} else {
				fragment.onError(twException);
			}
		}
	}
}