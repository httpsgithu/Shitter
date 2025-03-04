package org.nuclearfog.twidda.backend.api.impl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nuclearfog.twidda.backend.utils.StringTools;
import org.nuclearfog.twidda.model.User;

import java.util.regex.Pattern;

/**
 * API 1.1 implementation of User
 *
 * @author nuclearfog
 */
public class UserV1 implements User {

	public static final long serialVersionUID = 7893496988800499358L;

	private static final Pattern ID_PATTERN = Pattern.compile("\\d+");

	private long id;
	private long created;
	private String username;
	private String screenName;
	private String description;
	private String location;
	private String url;
	private String profileImageUrl;
	private String profileBannerUrl;
	private int following;
	private int follower;
	private int tweetCount;
	private int favoriteCount;
	private boolean isVerified;
	private boolean isLocked;
	private boolean followReqSent;
	private boolean defaultImage;
	private boolean isCurrentUser = true;

	/**
	 * @param json      JSON object containing user information
	 * @param twitterId ID of the current user
	 * @throws JSONException if values are missing
	 */
	public UserV1(JSONObject json, long twitterId) throws JSONException {
		this(json);
		isCurrentUser = twitterId == id;
	}

	/**
	 * @param json JSON object containing user information
	 * @throws JSONException if values are missing
	 */
	public UserV1(JSONObject json) throws JSONException {
		String idStr = json.getString("id_str");
		String profileImageUrl = json.optString("profile_image_url_https");
		username = json.optString("name");
		screenName = '@' + json.optString("screen_name");
		isVerified = json.optBoolean("verified");
		isLocked = json.optBoolean("protected");
		profileBannerUrl = json.optString("profile_banner_url");
		location = json.optString("location");
		following = json.optInt("friends_count");
		follower = json.optInt("followers_count");
		tweetCount = json.optInt("statuses_count");
		favoriteCount = json.optInt("favourites_count");
		followReqSent = json.optBoolean("follow_request_sent");
		defaultImage = json.optBoolean("default_profile_image");
		created = StringTools.getTime1(json.optString("created_at"));

		description = getDescription(json);
		url = getUrl(json);
		if (defaultImage) {
			this.profileImageUrl = profileImageUrl;
		} else {
			this.profileImageUrl = StringTools.createProfileImageLink(profileImageUrl);
		}
		if (ID_PATTERN.matcher(idStr).matches()) {
			id = Long.parseLong(idStr);
		} else {
			throw new JSONException("bad ID: " + idStr);
		}
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
		return profileImageUrl;
	}

	@Override
	public String getBannerUrl() {
		return profileBannerUrl;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getLocation() {
		return location;
	}

	@Override
	public String getProfileUrl() {
		return url;
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
	public int getTweetCount() {
		return tweetCount;
	}

	@Override
	public int getFavoriteCount() {
		return favoriteCount;
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

	/**
	 * expand URLs of the user description
	 *
	 * @param json root json object of user v1
	 * @return user description
	 */
	private String getDescription(JSONObject json) {
		try {
			JSONObject entities = json.getJSONObject("entities");
			String description = json.getString("description");
			JSONObject descrEntities = entities.getJSONObject("description");
			JSONArray urls = descrEntities.getJSONArray("urls");

			// expand shortened urls
			StringBuilder builder = new StringBuilder(description);
			for (int i = urls.length() - 1; i >= 0; i--) {
				JSONObject entry = urls.getJSONObject(i);
				String link = entry.getString("expanded_url");
				JSONArray indices = entry.getJSONArray("indices");
				int start = indices.getInt(0);
				int end = indices.getInt(1);
				int offset = StringTools.calculateIndexOffset(description, start);
				builder.replace(start + offset, end + offset, link);
			}
			return builder.toString();
		} catch (JSONException e) {
			return "";
		}
	}

	/**
	 * get expanded profile url
	 *
	 * @param json root json object of user v1
	 * @return expanded url
	 */
	private String getUrl(JSONObject json) {
		try {
			JSONObject entities = json.getJSONObject("entities");
			JSONObject urlJson = entities.getJSONObject("url");
			JSONArray urls = urlJson.getJSONArray("urls");
			if (urls.length() > 0) {
				return urls.getJSONObject(0).getString("display_url");
			}
		} catch (JSONException e) {
			// ignore
		}
		return "";
	}
}