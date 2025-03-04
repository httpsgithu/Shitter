package org.nuclearfog.twidda.database;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import java.io.File;

/**
 * This class creates and manages SQLite table versions
 *
 * @author nuclearfog
 */
public class DatabaseAdapter {

	/**
	 * database version number
	 */
	private static final int DB_VERSION = 5;

	/**
	 * database file name
	 */
	private static final String DB_NAME = "database.db";

	/**
	 * SQL query to create a table for user information
	 */
	private static final String TABLE_USER = "CREATE TABLE IF NOT EXISTS "
			+ UserTable.NAME + "("
			+ UserTable.ID + " INTEGER PRIMARY KEY,"
			+ UserTable.USERNAME + " TEXT,"
			+ UserTable.SCREENNAME + " TEXT,"
			+ UserTable.IMAGE + " TEXT,"
			+ UserTable.BANNER + " TEXT,"
			+ UserTable.DESCRIPTION + " TEXT,"
			+ UserTable.LOCATION + " TEXT,"
			+ UserTable.LINK + " TEXT,"
			+ UserTable.SINCE + " INTEGER,"
			+ UserTable.FRIENDS + " INTEGER,"
			+ UserTable.FOLLOWER + " INTEGER,"
			+ UserTable.TWEETS + " INTEGER,"
			+ UserTable.FAVORS + " INTEGER);";

	/**
	 * SQL query to create a table for tweet information
	 */
	private static final String TABLE_TWEET = "CREATE TABLE IF NOT EXISTS "
			+ TweetTable.NAME + "("
			+ TweetTable.ID + " INTEGER PRIMARY KEY,"
			+ TweetTable.USER + " INTEGER,"
			+ TweetTable.EMBEDDED + " INTEGER,"
			+ TweetTable.REPLYTWEET + " INTEGER,"
			+ TweetTable.REPLYNAME + " TEXT,"
			+ TweetTable.REPLYUSER + " INTEGER,"
			+ TweetTable.SINCE + " INTEGER,"
			+ TweetTable.TWEET + " TEXT,"
			+ TweetTable.MEDIA + " TEXT,"
			+ TweetTable.RETWEET + " INTEGER,"
			+ TweetTable.FAVORITE + " INTEGER,"
			+ TweetTable.SOURCE + " TEXT,"
			+ TweetTable.PLACE + " TEXT,"
			+ TweetTable.COORDINATE + " TEXT,"
			+ "FOREIGN KEY(" + TweetTable.USER + ")"
			+ "REFERENCES " + UserTable.NAME + "(" + UserTable.ID + "));";

	/**
	 * SQL query to create a table for favorite tweets
	 */
	private static final String TABLE_FAVORS = "CREATE TABLE IF NOT EXISTS "
			+ FavoriteTable.NAME + "("
			+ FavoriteTable.FAVORITEDBY + " INTEGER,"
			+ FavoriteTable.TWEETID + " INTEGER,"
			+ "FOREIGN KEY(" + FavoriteTable.FAVORITEDBY + ")"
			+ "REFERENCES " + UserTable.NAME + "(" + UserTable.ID + "),"
			+ "FOREIGN KEY(" + FavoriteTable.TWEETID + ")"
			+ "REFERENCES " + TweetTable.NAME + "(" + TweetTable.ID + "));";

	/**
	 * SQL query to create a table for trend information
	 */
	private static final String TABLE_TRENDS = "CREATE TABLE IF NOT EXISTS "
			+ TrendTable.NAME + "("
			+ TrendTable.ID + " INTEGER,"
			+ TrendTable.INDEX + " INTEGER,"
			+ TrendTable.VOL + " INTEGER,"
			+ TrendTable.TREND + " TEXT);";

	/**
	 * SQL query to create a table for message information
	 */
	private static final String TABLE_MESSAGES = "CREATE TABLE IF NOT EXISTS "
			+ MessageTable.NAME + "("
			+ MessageTable.ID + " INTEGER PRIMARY KEY,"
			+ MessageTable.SINCE + " INTEGER,"
			+ MessageTable.FROM + " INTEGER,"
			+ MessageTable.TO + " INTEGER,"
			+ MessageTable.MESSAGE + " TEXT,"
			+ MessageTable.MEDIA + " TEXT);";

	/**
	 * table for tweet register
	 */
	private static final String TABLE_TWEET_REGISTER = "CREATE TABLE IF NOT EXISTS "
			+ TweetRegisterTable.NAME + "("
			+ TweetRegisterTable.OWNER + " INTEGER,"
			+ TweetRegisterTable.ID + " INTEGER,"
			+ TweetRegisterTable.REGISTER + " INTEGER,"
			+ TweetRegisterTable.RETWEETUSER + " INTEGER,"
			+ "FOREIGN KEY(" + TweetRegisterTable.ID + ")"
			+ "REFERENCES " + TweetTable.NAME + "(" + TweetTable.ID + "));";

	/**
	 * table for user register
	 */
	private static final String TABLE_USER_REGISTER = "CREATE TABLE IF NOT EXISTS "
			+ UserRegisterTable.NAME + "("
			+ UserRegisterTable.OWNER + " INTEGER,"
			+ UserRegisterTable.ID + " INTEGER,"
			+ UserRegisterTable.REGISTER + " INTEGER,"
			+ "FOREIGN KEY(" + UserRegisterTable.ID + ")"
			+ "REFERENCES " + UserTable.NAME + "(" + UserTable.ID + "));";

	/**
	 * SQL query to create a table for user logins
	 */
	private static final String TABLE_LOGINS = "CREATE TABLE IF NOT EXISTS "
			+ AccountTable.NAME + "("
			+ AccountTable.ID + " INTEGER PRIMARY KEY,"
			+ AccountTable.DATE + " INTEGER,"
			+ AccountTable.KEY1 + " TEXT,"
			+ AccountTable.KEY2 + " TEXT);";

	/**
	 * SQL query to create user exclude table
	 */
	private static final String TABLE_USER_EXCLUDE = "CREATE TABLE IF NOT EXISTS "
			+ UserExcludeTable.NAME + "("
			+ UserExcludeTable.OWNER + " INTEGER,"
			+ UserExcludeTable.ID + " INTEGER);";

	/**
	 * table index for tweet table
	 */
	private static final String INDX_TWEET = "CREATE INDEX IF NOT EXISTS idx_tweet"
			+ " ON " + TweetTable.NAME + "(" + TweetTable.USER + ");";

	/**
	 * table index for tweet register
	 */
	private static final String INDX_TWEET_REG = "CREATE INDEX IF NOT EXISTS idx_tweet_register"
			+ " ON " + TweetRegisterTable.NAME + "(" + TweetRegisterTable.OWNER + "," + TweetRegisterTable.ID + ");";

	/**
	 * table index for user register
	 */
	private static final String INDX_USER_REG = "CREATE INDEX IF NOT EXISTS idx_user_register"
			+ " ON " + UserRegisterTable.NAME + "(" + UserRegisterTable.OWNER + "," + UserRegisterTable.ID + ");";

	/**
	 * singleton instance
	 */
	private static final DatabaseAdapter INSTANCE = new DatabaseAdapter();

	/**
	 * path to the database file
	 */
	private File databasePath;

	/**
	 * database
	 */
	private SQLiteDatabase db;

	/**
	 *
	 */
	private DatabaseAdapter() {
	}

	/**
	 * get database instance
	 *
	 * @return SQLite database
	 */
	public synchronized SQLiteDatabase getDatabase() {
		if (!db.isOpen())
			db = SQLiteDatabase.openOrCreateDatabase(databasePath, null);
		return db;
	}

	/**
	 * get database adapter instance
	 *
	 * @param context application context
	 * @return database instance
	 */
	public static DatabaseAdapter getInstance(@NonNull Context context) {
		if (INSTANCE.db == null)
			INSTANCE.init(context.getApplicationContext());
		return INSTANCE;
	}

	/**
	 * delete database and destroy instance
	 *
	 * @param c application context
	 */
	public static void deleteDatabase(Context c) {
		SQLiteDatabase.deleteDatabase(c.getDatabasePath(DB_NAME));
		INSTANCE.init(c.getApplicationContext());
	}

	/**
	 * initialize databases
	 *
	 * @param c application context
	 */
	private void init(Context c) {
		databasePath = c.getDatabasePath(DB_NAME);
		db = c.openOrCreateDatabase(databasePath.toString(), MODE_PRIVATE, null);
		initTables();
	}

	/**
	 * initialize tables if there aren't any
	 */
	private void initTables() {
		// create tables
		db.execSQL(TABLE_USER);
		db.execSQL(TABLE_TWEET);
		db.execSQL(TABLE_FAVORS);
		db.execSQL(TABLE_TRENDS);
		db.execSQL(TABLE_MESSAGES);
		db.execSQL(TABLE_LOGINS);
		db.execSQL(TABLE_USER_EXCLUDE);
		db.execSQL(TABLE_TWEET_REGISTER);
		db.execSQL(TABLE_USER_REGISTER);
		// create index
		db.execSQL(INDX_TWEET);
		db.execSQL(INDX_TWEET_REG);
		db.execSQL(INDX_USER_REG);
		if (db.getVersion() == 0) {
			db.setVersion(DB_VERSION);
		}
	}

	/**
	 * table for user information
	 */
	public interface UserTable {

		/**
		 * table name
		 */
		String NAME = "user";

		/**
		 * ID of the user
		 */
		String ID = "userID";

		/**
		 * user name
		 */
		String USERNAME = "username";

		/**
		 * screen name (starting with @)
		 */
		String SCREENNAME = "scrname";

		/**
		 * description (bio) of the user
		 */
		String DESCRIPTION = "bio";

		/**
		 * location attached to profile
		 */
		String LOCATION = "location";

		/**
		 * link attached to profile
		 */
		String LINK = "link";

		/**
		 * date of account creation
		 */
		String SINCE = "createdAt";

		/**
		 * link to the original profile image
		 */
		String IMAGE = "pbLink";

		/**
		 * link to the original banner image
		 */
		String BANNER = "banner";

		/**
		 * following count
		 */
		String FRIENDS = "following";

		/**
		 * follower count
		 */
		String FOLLOWER = "follower";

		/**
		 * count of tweets written/retweeted by user
		 */
		String TWEETS = "tweetCount";

		/**
		 * count of the tweets favored by the user
		 */
		String FAVORS = "favorCount";
	}

	/**
	 * table for all tweets
	 */
	public interface TweetTable {
		/**
		 * table name
		 */
		String NAME = "tweet";

		/**
		 * ID of the tweet
		 */
		String ID = "tweetID";

		/**
		 * ID of the author
		 */
		String USER = "userID";

		/**
		 * tweet text
		 */
		String TWEET = "tweet";

		/**
		 * media links attached to the tweet
		 */
		String MEDIA = "media";

		/**
		 * retweet count
		 */
		String RETWEET = "retweet";

		/**
		 * favorite count
		 */
		String FAVORITE = "favorite";

		/**
		 * timestamp of the tweet
		 */
		String SINCE = "time";

		/**
		 * API source of the tweet
		 */
		String SOURCE = "source";

		/**
		 * place name of the tweet
		 */
		String PLACE = "place";

		/**
		 * GPS coordinate of the tweet
		 */
		String COORDINATE = "geo";

		/**
		 * ID of the re plied tweet
		 */
		String REPLYTWEET = "replyID";

		/**
		 * ID of the replied user
		 */
		String REPLYUSER = "replyUserID";

		/**
		 * name of the replied user
		 */
		String REPLYNAME = "replyname";

		/**
		 * ID of the embedded (retweeted) status
		 */
		String EMBEDDED = "retweetID";
	}

	/**
	 * table for favored tweets of an user
	 */
	public interface FavoriteTable {
		/**
		 * table name
		 */
		String NAME = "favorit";

		/**
		 * ID of the tweet
		 */
		String TWEETID = "tweetID";
		/**
		 * ID of the user of this favored tweet
		 */
		String FAVORITEDBY = "ownerID";
	}

	/**
	 * table for twitter trends
	 */
	public interface TrendTable {
		/**
		 * table name
		 */
		String NAME = "trend";

		/**
		 * ID of the trend location
		 */
		String ID = "woeID";

		/**
		 * rank of the trend
		 */
		String INDEX = "trendpos";

		/**
		 * popularity count
		 */
		String VOL = "vol";

		/**
		 * name of the trend
		 */
		String TREND = "trendname";
	}

	/**
	 * Table for direct messages
	 */
	public interface MessageTable {
		/**
		 * table name
		 */
		String NAME = "message";

		/**
		 * ID of the message
		 */
		String ID = "messageID";

		/**
		 * date of the message
		 */
		String SINCE = "time";

		/**
		 * User ID of the sender
		 */
		String FROM = "senderID";

		/**
		 * User ID of the receiver
		 */
		String TO = "receiverID";

		/**
		 * message text
		 */
		String MESSAGE = "message";

		/**
		 * media links
		 */
		String MEDIA = "media";
	}

	/**
	 * Table for multi user login information
	 */
	public interface AccountTable {
		/**
		 * SQL table name
		 */
		String NAME = "login";

		/**
		 * ID of the user
		 */
		String ID = "userID";

		/**
		 * date of login
		 */
		String DATE = "date";

		/**
		 * primary oauth access token
		 */
		String KEY1 = "auth_key1";

		/**
		 * second oauth access token
		 */
		String KEY2 = "auth_key2";
	}

	/**
	 * table for tweet register
	 * <p>
	 * a register contains status flags (status bits) of a tweet
	 * every flag stands for a status like retweeted or favored
	 * the idea is to save space by putting boolean rows into a single integer row
	 * <p>
	 * to avoid conflicts between multi users,
	 * every login has its own status registers
	 */
	public interface TweetRegisterTable {
		/**
		 * SQL table name
		 */
		String NAME = "tweetFlags";

		/**
		 * ID of the user this register references to
		 */
		String ID = "tweetID";

		/**
		 * ID of the current user accessing the database
		 */
		String OWNER = "ownerID";

		/**
		 * Register with status bits
		 */
		String REGISTER = "tweetRegister";

		/**
		 * ID of the retweet of the current user (if exists)
		 */
		String RETWEETUSER = "retweeterID";
	}

	/**
	 * table for user register
	 */
	public interface UserRegisterTable {
		/**
		 * SQL table name
		 */
		String NAME = "userFlags";

		/**
		 * ID of the user this register references to
		 */
		String ID = "userID";

		/**
		 * ID of the current user accessing the database
		 */
		String OWNER = "ownerID";

		/**
		 * Register with status bits
		 */
		String REGISTER = "userRegister";
	}

	/**
	 * table for user filter list
	 */
	public interface UserExcludeTable {
		/**
		 * table name
		 */
		String NAME = "userExclude";

		/**
		 * owner ID of the list
		 */
		String OWNER = "listOwner";

		/**
		 * user ID to filter
		 */
		String ID = "userID";
	}
}