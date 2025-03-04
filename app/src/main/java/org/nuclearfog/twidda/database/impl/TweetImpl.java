package org.nuclearfog.twidda.database.impl;

import static org.nuclearfog.twidda.database.AppDatabase.FAV_MASK;
import static org.nuclearfog.twidda.database.AppDatabase.HIDDEN_MASK;
import static org.nuclearfog.twidda.database.AppDatabase.MEDIA_ANGIF_MASK;
import static org.nuclearfog.twidda.database.AppDatabase.MEDIA_IMAGE_MASK;
import static org.nuclearfog.twidda.database.AppDatabase.MEDIA_SENS_MASK;
import static org.nuclearfog.twidda.database.AppDatabase.MEDIA_VIDEO_MASK;
import static org.nuclearfog.twidda.database.AppDatabase.RTW_MASK;

import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.nuclearfog.twidda.backend.utils.StringTools;
import org.nuclearfog.twidda.database.DatabaseAdapter;
import org.nuclearfog.twidda.model.Tweet;
import org.nuclearfog.twidda.model.User;

import java.util.regex.Pattern;

/**
 * Implementation of a database tweet
 *
 * @author nuclearfog
 */
public class TweetImpl implements Tweet {

	private static final long serialVersionUID = -5957556706939766801L;

	private static final Pattern SEPARATOR = Pattern.compile(";");

	private long id;
	private long time;
	private long embeddedId;
	private long replyID;
	private long replyUserId;
	private long myRetweetId;
	@Nullable
	private Tweet embedded;
	private User author;
	private int retweetCount;
	private int favoriteCount;
	private int mediaType;
	private String locationName;
	private String locationCoordinates;
	private String replyName;
	private String text;
	private String source;
	private String userMentions;
	private String[] mediaLinks = {};
	private boolean retweeted;
	private boolean favorited;
	private boolean sensitive;
	private boolean isHidden;


	public TweetImpl(Cursor cursor, long currentUserId) {
		author = new UserImpl(cursor, currentUserId);
		time = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseAdapter.TweetTable.SINCE));
		text = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseAdapter.TweetTable.TWEET));
		retweetCount = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseAdapter.TweetTable.RETWEET));
		favoriteCount = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseAdapter.TweetTable.FAVORITE));
		id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseAdapter.TweetTable.ID));
		replyName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseAdapter.TweetTable.REPLYNAME));
		replyID = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseAdapter.TweetTable.REPLYTWEET));
		source = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseAdapter.TweetTable.SOURCE));
		String linkStr = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseAdapter.TweetTable.MEDIA));
		locationName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseAdapter.TweetTable.PLACE));
		locationCoordinates = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseAdapter.TweetTable.COORDINATE));
		replyUserId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseAdapter.TweetTable.REPLYUSER));
		embeddedId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseAdapter.TweetTable.EMBEDDED));
		myRetweetId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseAdapter.TweetRegisterTable.RETWEETUSER));
		int tweetRegister = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseAdapter.TweetRegisterTable.REGISTER));
		favorited = (tweetRegister & FAV_MASK) != 0;
		retweeted = (tweetRegister & RTW_MASK) != 0;
		sensitive = (tweetRegister & MEDIA_SENS_MASK) != 0;
		isHidden = (tweetRegister & HIDDEN_MASK) != 0;
		if (!linkStr.isEmpty())
			mediaLinks = SEPARATOR.split(linkStr);
		userMentions = StringTools.getUserMentions(text, author.getScreenname());
		// get media type
		if ((tweetRegister & MEDIA_ANGIF_MASK) == MEDIA_ANGIF_MASK) {
			mediaType = MEDIA_GIF;
		} else if ((tweetRegister & MEDIA_IMAGE_MASK) == MEDIA_IMAGE_MASK) {
			mediaType = MEDIA_PHOTO;
		} else if ((tweetRegister & MEDIA_VIDEO_MASK) == MEDIA_VIDEO_MASK) {
			mediaType = MEDIA_VIDEO;
		} else {
			mediaType = MEDIA_NONE;
		}
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public User getAuthor() {
		return author;
	}

	@Override
	public long getTimestamp() {
		return time;
	}

	@Override
	public String getSource() {
		return source;
	}

	@Nullable
	@Override
	public Tweet getEmbeddedTweet() {
		return embedded;
	}

	@Override
	public String getReplyName() {
		return replyName;
	}

	@Override
	public long getRepliedUserId() {
		return replyUserId;
	}

	@Override
	public long getRepliedTweetId() {
		return replyID;
	}

	@Override
	public long getRetweetId() {
		return myRetweetId;
	}

	@Override
	public int getRetweetCount() {
		return retweetCount;
	}

	@Override
	public int getFavoriteCount() {
		return favoriteCount;
	}

	@NonNull
	@Override
	public Uri[] getMediaUris() {
		Uri[] result = new Uri[mediaLinks.length];
		for (int i = 0; i < result.length; i++)
			result[i] = Uri.parse(mediaLinks[i]);
		return result;
	}

	@Override
	public String getUserMentions() {
		return userMentions;
	}

	@Override
	public int getMediaType() {
		return mediaType;
	}

	@Override
	public boolean isSensitive() {
		return sensitive;
	}

	@Override
	public boolean isRetweeted() {
		return retweeted;
	}

	@Override
	public boolean isFavorited() {
		return favorited;
	}

	@Override
	public String getLocationName() {
		return locationName;
	}

	@Override
	public String getLocationCoordinates() {
		return locationCoordinates;
	}

	@Override
	public boolean isHidden() {
		return isHidden;
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		if (!(obj instanceof Tweet))
			return false;
		return ((Tweet) obj).getId() == id;
	}

	@NonNull
	@Override
	public String toString() {
		return "from=\"" + author.getScreenname() + "\" text=\"" + text + "\"";
	}

	/**
	 * get ID of the embedded tweet
	 *
	 * @return ID of the
	 */
	public long getEmbeddedTweetId() {
		return embeddedId;
	}

	/**
	 * attach tweet referenced by {@link #embeddedId}
	 *
	 * @param embedded embedded tweet
	 */
	public void addEmbeddedTweet(Tweet embedded) {
		this.embedded = embedded;
	}
}