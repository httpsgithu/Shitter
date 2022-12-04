package org.nuclearfog.twidda.database.impl;

import static org.nuclearfog.twidda.database.AppDatabase.DEFAULT_IMAGE_MASK;
import static org.nuclearfog.twidda.database.AppDatabase.FOLLOW_REQUEST_MASK;
import static org.nuclearfog.twidda.database.AppDatabase.LOCKED_MASK;
import static org.nuclearfog.twidda.database.AppDatabase.VERIFIED_MASK;

import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.nuclearfog.twidda.database.DatabaseAdapter;
import org.nuclearfog.twidda.model.User;

/**
 * database implementation of an user
 *
 * @author nuclearfog
 */
public class UserImpl implements User {

	private static final long serialVersionUID = 2367055336838212570L;

	private long id;
	private long created;
	private int following;
	private int follower;
	private int statusCount;
	private int favorCount;
	private String username;
	private String screenName;
	private String bio;
	private String location;
	private String link;
	private String profileImg;
	private String bannerImg;
	private boolean isCurrentUser;
	private boolean isVerified;
	private boolean isLocked;
	private boolean followReqSent;
	private boolean defaultImage;


	public UserImpl(Cursor cursor, long currentUserId) {
		id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseAdapter.UserTable.ID));
		username = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseAdapter.UserTable.USERNAME));
		screenName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseAdapter.UserTable.SCREENNAME));
		profileImg = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseAdapter.UserTable.IMAGE));
		bio = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseAdapter.UserTable.DESCRIPTION));
		link = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseAdapter.UserTable.LINK));
		location = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseAdapter.UserTable.LOCATION));
		bannerImg = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseAdapter.UserTable.BANNER));
		created = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseAdapter.UserTable.SINCE));
		following = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseAdapter.UserTable.FRIENDS));
		follower = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseAdapter.UserTable.FOLLOWER));
		statusCount = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseAdapter.UserTable.STATUSES));
		favorCount = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseAdapter.UserTable.FAVORITS));
		int register = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseAdapter.UserRegisterTable.REGISTER));
		isVerified = (register & VERIFIED_MASK) != 0;
		isLocked = (register & LOCKED_MASK) != 0;
		followReqSent = (register & FOLLOW_REQUEST_MASK) != 0;
		defaultImage = (register & DEFAULT_IMAGE_MASK) != 0;
		isCurrentUser = currentUserId == id;
	}


	@Override
	public long getId() {
		return id;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getScreenname() {
		return screenName;
	}

	@Override
	public long getCreatedAt() {
		return created;
	}

	@Override
	public String getImageUrl() {
		return profileImg;
	}

	@Override
	public String getBannerUrl() {
		return bannerImg;
	}

	@Override
	public String getDescription() {
		return bio;
	}

	@Override
	public String getLocation() {
		return location;
	}

	@Override
	public String getProfileUrl() {
		return link;
	}

	@Override
	public boolean isVerified() {
		return isVerified;
	}

	@Override
	public boolean isProtected() {
		return isLocked;
	}

	@Override
	public boolean followRequested() {
		return followReqSent;
	}

	@Override
	public int getFollowing() {
		return following;
	}

	@Override
	public int getFollower() {
		return follower;
	}

	@Override
	public int getStatusCount() {
		return statusCount;
	}

	@Override
	public int getFavoriteCount() {
		return favorCount;
	}

	@Override
	public boolean hasDefaultProfileImage() {
		return defaultImage;
	}

	@Override
	public boolean isCurrentUser() {
		return isCurrentUser;
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		if (!(obj instanceof User))
			return false;
		return ((User) obj).getId() == id;
	}

	@NonNull
	@Override
	public String toString() {
		return "name=\"" + screenName + "\"";
	}
}
