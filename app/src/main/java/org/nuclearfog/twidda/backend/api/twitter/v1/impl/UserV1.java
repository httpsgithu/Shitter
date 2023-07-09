package org.nuclearfog.twidda.backend.api.twitter.v1.impl;

import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nuclearfog.twidda.backend.utils.StringUtils;
import org.nuclearfog.twidda.model.Emoji;
import org.nuclearfog.twidda.model.User;

/**
 * API 1.1 implementation of User
 *
 * @author nuclearfog
 */
public class UserV1 implements User {

	private static final long serialVersionUID = 7893496988800499358L;

	/**
	 * API query to prevent disable statuses in the user object.
	 */
	public static final String PARAM_SKIP_STATUS = "skip_status=true";

	/**
	 * API query parameter to get all required json fields
	 */
	public static final String PARAM_INCLUDE_ENTITIES = "include_entities=true";

	private long id;
	private long timestamp;
	private String username;
	private String screenName;
	private String description;
	private String location;
	private String url;
	private String profileImageUrl = "";
	private String profileBannerUrl = "";
	private int following;
	private int follower;
	private int tweetCount;
	private int favoriteCount;
	private boolean isVerified;
	private boolean isLocked;
	private boolean followReqSent;
	private boolean defaultImage;
	private boolean isCurrentUser;

	/**
	 * user constructor used to create instance of the current user
	 *
	 * @param json JSON object containing user information
	 */
	public UserV1(JSONObject json) throws JSONException {
		this(json, 0L);
		isCurrentUser = true;
	}

	/**
	 * default user constructor
	 *
	 * @param json      JSON object containing user information
	 * @param twitterId ID of the current user
	 */
	public UserV1(JSONObject json, long twitterId) throws JSONException {
		String idStr = json.getString("id_str");
		String profileImageUrl = json.optString("profile_image_url_https", "");
		String profileBannerUrl = json.optString("profile_banner_url", "");
		username = json.optString("name", "");
		screenName = '@' + json.optString("screen_name", "");
		isVerified = json.optBoolean("verified");
		isLocked = json.optBoolean("protected");
		location = json.optString("location", "");
		following = json.optInt("friends_count");
		follower = json.optInt("followers_count");
		tweetCount = json.optInt("statuses_count");
		favoriteCount = json.optInt("favourites_count");
		followReqSent = json.optBoolean("follow_request_sent");
		defaultImage = json.optBoolean("default_profile_image");
		timestamp = StringUtils.getTime(json.optString("created_at", ""), StringUtils.TIME_TWITTER_V1);
		description = getDescription(json);
		url = getUrl(json);

		if (Patterns.WEB_URL.matcher(profileImageUrl).matches()) {
			if (defaultImage) {
				this.profileImageUrl = profileImageUrl;
			} else {
				this.profileImageUrl = StringUtils.createProfileImageLink(profileImageUrl);
			}
		}
		if (Patterns.WEB_URL.matcher(profileBannerUrl).matches()) {
			this.profileBannerUrl = profileBannerUrl;
		}
		try {
			id = Long.parseLong(idStr);
			isCurrentUser = twitterId == id;
		} catch (NumberFormatException e) {
			throw new JSONException("bad user ID: " + idStr);
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
	public long getTimestamp() {
		return timestamp;
	}


	@Override
	public String getOriginalProfileImageUrl() {
		return profileImageUrl;
	}


	@Override
	public String getProfileImageThumbnailUrl() {
		if (defaultImage || profileImageUrl.isEmpty())
			return profileImageUrl;
		return profileImageUrl + "_bigger";
	}


	@Override
	public String getOriginalBannerImageUrl() {
		if (profileBannerUrl.isEmpty())
			return "";
		return profileBannerUrl + "/1500x500";
	}


	@Override
	public String getBannerImageThumbnailUrl() {
		if (profileBannerUrl.isEmpty())
			return "";
		return profileBannerUrl + "/600x200";
	}


	@Override
	public boolean hasDefaultProfileImage() {
		return defaultImage;
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
	public int getStatusCount() {
		return tweetCount;
	}


	@Override
	public int getFavoriteCount() {
		return favoriteCount;
	}


	@Override
	public boolean isCurrentUser() {
		return isCurrentUser;
	}


	@Override
	public Emoji[] getEmojis() {
		return new Emoji[0];
	}


	@Override
	public Field[] getFields() {
		return new Field[0];
	}


	@Override
	public boolean equals(@Nullable Object obj) {
		if (!(obj instanceof User))
			return false;
		return ((User) obj).getId() == getId();
	}


	@NonNull
	@Override
	public String toString() {
		return "name=\"" + getScreenname() + "\"";
	}

	/**
	 * expand URLs of the user description
	 *
	 * @param json root json object of user v1
	 * @return user description
	 */
	@NonNull
	private String getDescription(JSONObject json) {
		String description = json.optString("description", "");
		JSONObject entities = json.optJSONObject("entities");
		if (entities != null) {
			JSONObject descrEntities = entities.optJSONObject("description");
			if (descrEntities != null) {
				try {
					// expand shortened urls
					JSONArray urls = descrEntities.getJSONArray("urls");
					StringBuilder builder = new StringBuilder(description);
					for (int i = urls.length() - 1; i >= 0; i--) {
						JSONObject entry = urls.getJSONObject(i);
						String link = entry.getString("expanded_url");
						JSONArray indices = entry.getJSONArray("indices");
						int start = indices.getInt(0);
						int end = indices.getInt(1);
						int offset = StringUtils.calculateIndexOffset(description, start);
						builder.replace(start + offset, end + offset, link);
					}
					return builder.toString();
				} catch (JSONException e) {
					// use default description
				}
			}
		}
		return description;
	}

	/**
	 * get expanded profile url
	 *
	 * @param json root json object of user v1
	 * @return expanded url
	 */
	@NonNull
	private String getUrl(JSONObject json) {
		JSONObject entities = json.optJSONObject("entities");
		if (entities != null) {
			try {
				JSONObject urlJson = entities.getJSONObject("url");
				JSONArray urls = urlJson.getJSONArray("urls");
				if (urls.length() > 0) {
					return urls.getJSONObject(0).getString("display_url");
				}
			} catch (JSONException e) {
				// ignore
			}
		}
		return "";
	}
}