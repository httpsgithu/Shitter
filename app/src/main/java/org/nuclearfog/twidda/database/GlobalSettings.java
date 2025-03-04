package org.nuclearfog.twidda.database;

import static android.content.Context.MODE_PRIVATE;
import static android.graphics.Typeface.DEFAULT;
import static android.graphics.Typeface.MONOSPACE;
import static android.graphics.Typeface.NORMAL;
import static android.graphics.Typeface.SANS_SERIF;
import static android.graphics.Typeface.SERIF;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;

import androidx.annotation.NonNull;

import org.nuclearfog.twidda.database.impl.LocationImpl;
import org.nuclearfog.twidda.model.Location;

import java.util.LinkedList;
import java.util.List;

/**
 * This class manages app settings
 *
 * @author nuclearfog
 */
public class GlobalSettings {

	/**
	 * link suffix for low resolution profile images
	 */
	public static final String PROFILE_IMG_LOW_RES = "_mini";

	/**
	 * link suffix for high resolution profile images
	 */
	public static final String PROFILE_IMG_HIGH_RES = "_bigger";

	/**
	 * link suffix for low resolution banner images
	 */
	public static final String BANNER_IMG_LOW_RES = "/300x100";

	/**
	 * link suffix for standard banner image resolution
	 */
	public static final String BANNER_IMG_MID_RES = "/600x200";

	/**
	 * alternative Twitter service
	 */
	private static final String TWITTER_ALT_HOST = "https://nitter.net/";

	private static final String TWITTER_HOST = "https://twitter.com/";

	/**
	 * custom android font
	 */
	private static final Typeface SANS_SERIF_THIN = Typeface.create("sans-serif-thin", NORMAL);

	/**
	 * custom font families from android system
	 */
	public static final Typeface[] FONTS = {DEFAULT, MONOSPACE, SERIF, SANS_SERIF, SANS_SERIF_THIN};

	/**
	 * names of the font types {@link #FONTS}
	 */
	public static final String[] FONT_NAMES = {"Default", "Monospace", "Serif", "Sans-Serif", "sans-serif-thin"};

	/**
	 * font scales
	 */
	public static final float[] SCALES = {0.5f, 0.8f, 1.0f, 1.5f, 2.0f};

	/**
	 * singleton instance
	 */
	private static final GlobalSettings ourInstance = new GlobalSettings();

	// App preference names
	private static final String BACKGROUND_COLOR = "background_color";
	private static final String HIGHLIGHT_COLOR = "highlight_color";
	private static final String FONT_COLOR = "font_color";
	private static final String POPUP_COLOR = "tweet_color";
	private static final String CARD_COLOR = "card_color";
	private static final String ICON_COLOR = "icon_color";
	private static final String RT_COLOR = "retweet_color";
	private static final String FV_COLOR = "favorite_color";
	private static final String FOLLOW_COLOR = "following_color";
	private static final String F_REQ_COLOR = "following_pending_color";
	private static final String INDEX_FONT = "index_font";
	private static final String INDEX_SCALE = "index_scale";
	private static final String LIST_SIZE = "preload";
	private static final String IMAGE_LOAD = "image_load";
	private static final String IMAGE_QUALITY = "image_hq";
	private static final String ANSWER_LOAD = "answer_load";
	private static final String TWEET_INDICATOR = "tweet_indicator";
	private static final String PROFILE_OVERLAP = "profile_toolbar_overlap";
	private static final String PROXY_SET = "proxy_enabled";
	private static final String AUTH_SET = "proxy_auth_set";
	private static final String PROXY_ADDR = "proxy_addr";
	private static final String PROXY_PORT = "proxy_port";
	private static final String PROXY_USER = "proxy_user";
	private static final String PROXY_PASS = "proxy_pass";
	private static final String PROXY_IGNORE = "ignore_proxy_set";
	private static final String TREND_LOC = "location";
	private static final String TREND_ID = "world_id";
	private static final String LINK_PREVIEW = "link_preview";
	private static final String ENABLE_LIKE = "like_enable";
	private static final String ENABLE_TWITTER_ALT = "twitter_alt_set";
	private static final String FILTER_RESULTS = "filter_results";
	private static final String CUSTOM_CONSUMER_KEY_SET = "custom_api_keys";
	private static final String CUSTOM_CONSUMER_KEY_1 = "api_key1";
	private static final String CUSTOM_CONSUMER_KEY_2 = "api_key2";

	// login specific preference names
	private static final String LOGGED_IN = "login";
	private static final String CURRENT_ID = "userID";
	private static final String CURRENT_AUTH_KEY1 = "key1";
	private static final String CURRENT_AUTH_KEY2 = "key2";

	// file name of the preferences
	private static final String APP_SETTINGS = "settings";

	// Default App settings
	private static final int DEFAULT_FONT_INDEX = 0;
	private static final int DEFAULT_SCALE_INDEX = 2;
	private static final int DEFAULT_LIST_SIZE = 20;
	private static final int DEFAULT_BACKGROUND_COLOR = 0xff0f114a;
	private static final int DEFAULT_HIGHLIGHT_COLOR = 0xffff00ff;
	private static final int DEFAULT_FONT_COLOR = Color.WHITE;
	private static final int DEFAULT_POPUP_COLOR = 0xff19aae8;
	private static final int DEFAULT_CARD_COLOR = 0x40000000;
	private static final int DEFAULT_ICON_COLOR = Color.WHITE;
	private static final int DEFAULT_RT_ICON_COLOR = Color.GREEN;
	private static final int DEFAULT_FV_ICON_COLOR = Color.YELLOW;
	private static final int DEFAULT_FR_ICON_COLOR = Color.YELLOW;
	private static final int DEFAULT_FW_ICON_COLOR = Color.CYAN;
	private static final int DEFAULT_LOCATION_ID = 1;
	private static final String DEFAULT_LOCATION_NAME = "Worldwide";


	private SharedPreferences settings;
	private Location location;
	private String api_key1, api_key2;
	private String auth_key1, auth_key2;
	private String proxyHost, proxyPort;
	private String proxyUser, proxyPass;
	private boolean loadImage;
	private boolean hqImages;
	private boolean loadAnswer;
	private boolean loggedIn;
	private boolean isProxyEnabled;
	private boolean isProxyAuthSet;
	private boolean ignoreProxyWarning;
	private boolean customAPIKey;
	private boolean toolbarOverlap;
	private boolean linkPreview;
	private boolean tweetIndicators;
	private boolean filterResults;
	private boolean enableLike;
	private boolean twitterAlt;
	private int background_color;
	private int font_color;
	private int highlight_color;
	private int card_color;
	private int icon_color;
	private int popup_color;
	private int retweet_color;
	private int favorite_color;
	private int request_color;
	private int follow_color;
	private int indexFont;
	private int indexScale;
	private int listSize;
	private long userId;

	private List<SettingsListener> settingsListeners = new LinkedList<>();

	private GlobalSettings() {
	}

	/**
	 * Get Singleton instance
	 *
	 * @param context Application Context needed for Shared preferences
	 * @return instance of this class
	 */
	public static GlobalSettings getInstance(@NonNull Context context) {
		if (ourInstance.settings == null) {
			ourInstance.settings = context.getApplicationContext().getSharedPreferences(APP_SETTINGS, MODE_PRIVATE);
			ourInstance.initialize();
		}
		return ourInstance;
	}

	/**
	 * Get get background color
	 *
	 * @return color value
	 */
	public int getBackgroundColor() {
		return background_color;
	}

	/**
	 * Set background color
	 *
	 * @param color background color value
	 */
	public void setBackgroundColor(int color) {
		background_color = color;

		Editor edit = settings.edit();
		edit.putInt(BACKGROUND_COLOR, color);
		edit.apply();
	}

	/**
	 * get font color
	 *
	 * @return font color value
	 */
	public int getFontColor() {
		return font_color;
	}

	/**
	 * set font color
	 *
	 * @param color font color value
	 */
	public void setFontColor(int color) {
		font_color = color;

		Editor edit = settings.edit();
		edit.putInt(FONT_COLOR, color);
		edit.apply();
	}

	/**
	 * get highlight color
	 *
	 * @return highlight color value
	 */
	public int getHighlightColor() {
		return highlight_color;
	}

	/**
	 * set highlight color
	 *
	 * @param color highlight color value
	 */
	public void setHighlightColor(int color) {
		highlight_color = color;

		Editor edit = settings.edit();
		edit.putInt(HIGHLIGHT_COLOR, color);
		edit.apply();
	}

	/**
	 * get message window color
	 *
	 * @return color value
	 */
	public int getPopupColor() {
		return popup_color;
	}

	/**
	 * set message window color
	 *
	 * @param color color value
	 */
	public void setPopupColor(int color) {
		popup_color = color;

		Editor edit = settings.edit();
		edit.putInt(POPUP_COLOR, color);
		edit.apply();
	}

	/**
	 * get CardView color
	 *
	 * @return color
	 */
	public int getCardColor() {
		return card_color;
	}

	/**
	 * set Card View Color
	 *
	 * @param color new color
	 */
	public void setCardColor(int color) {
		card_color = color;

		Editor edit = settings.edit();
		edit.putInt(CARD_COLOR, color);
		edit.apply();
	}

	/**
	 * get icon color
	 *
	 * @return color
	 */
	public int getIconColor() {
		return icon_color;
	}

	/**
	 * set icon Color
	 *
	 * @param color new color
	 */
	public void setIconColor(int color) {
		icon_color = color;

		Editor edit = settings.edit();
		edit.putInt(ICON_COLOR, color);
		edit.apply();
	}

	/**
	 * get icon color of the favorite icon
	 *
	 * @return icon color
	 */
	public int getFavoriteIconColor() {
		return favorite_color;
	}

	/**
	 * set icon color of the favorite icon (enabled)
	 *
	 * @param color icon color
	 */
	public void setFavoriteIconColor(int color) {
		favorite_color = color;

		Editor edit = settings.edit();
		edit.putInt(FV_COLOR, color);
		edit.apply();
	}

	/**
	 * get retweet icon color
	 *
	 * @return icon color
	 */
	public int getRetweetIconColor() {
		return retweet_color;
	}

	/**
	 * set retweet icon color (enabled)
	 *
	 * @param color icon color
	 */
	public void setRetweetIconColor(int color) {
		retweet_color = color;

		Editor edit = settings.edit();
		edit.putInt(RT_COLOR, color);
		edit.apply();
	}

	/**
	 * get icon color of the follow button
	 *
	 * @return icon color
	 */
	public int getFollowPendingColor() {
		return request_color;
	}

	/**
	 * set icon color of the follow button
	 *
	 * @param color icon color
	 */
	public void setFollowPendingColor(int color) {
		request_color = color;

		Editor edit = settings.edit();
		edit.putInt(F_REQ_COLOR, color);
		edit.apply();
	}

	/**
	 * get icon color for the follow button
	 *
	 * @return icon color
	 */
	public int getFollowIconColor() {
		return follow_color;
	}

	/**
	 * set icon color for the follow button
	 *
	 * @param color color value for the follow button if enabled
	 */
	public void setFollowIconColor(int color) {
		follow_color = color;

		Editor edit = settings.edit();
		edit.putInt(FOLLOW_COLOR, color);
		edit.apply();
	}

	/**
	 * return an array of all installed colors
	 *
	 * @return array of colors
	 */
	public int[] getAllColors() {
		return new int[]{
				background_color, font_color,
				popup_color, highlight_color,
				card_color, icon_color,
				retweet_color, favorite_color,
				request_color, follow_color
		};
	}

	/**
	 * image load loading enabled
	 *
	 * @return true if enabled
	 */
	public boolean imagesEnabled() {
		return loadImage;
	}

	/**
	 * enable/disable image load load
	 *
	 * @param enable true if enabled
	 */
	public void setImageLoad(boolean enable) {
		loadImage = enable;

		Editor edit = settings.edit();
		edit.putBoolean(IMAGE_LOAD, enable);
		edit.apply();
	}

	/**
	 * is profile toolbar overlap enabled
	 *
	 * @return true if enabled
	 */
	public boolean toolbarOverlapEnabled() {
		return toolbarOverlap;
	}

	/**
	 * set profile toolbar overlap
	 *
	 * @param enable true if toolbar should overlap profile banner
	 */
	public void setToolbarOverlap(boolean enable) {
		toolbarOverlap = enable;

		Editor edit = settings.edit();
		edit.putBoolean(PROFILE_OVERLAP, enable);
		edit.apply();
	}

	/**
	 * check if link preview is enabled
	 */
	public boolean linkPreviewEnabled() {
		return linkPreview;
	}

	/**
	 * enable/disable link preview
	 *
	 * @param enable true enables link preview
	 */
	public void setLinkPreview(boolean enable) {
		linkPreview = enable;

		Editor edit = settings.edit();
		edit.putBoolean(LINK_PREVIEW, enable);
		edit.apply();
	}

	/**
	 * check if search filter is enabled
	 *
	 * @return true if search filter is enabled
	 */
	public boolean filterResults() {
		return filterResults;
	}

	/**
	 * enable/disable search filter
	 *
	 * @param enable true to enable search filter
	 */
	public void setFilterResults(boolean enable) {
		filterResults = enable;

		Editor edit = settings.edit();
		edit.putBoolean(FILTER_RESULTS, enable);
		edit.apply();
	}

	/**
	 * sets image quality
	 *
	 * @return true if thumbnails should be in high resolution
	 */
	public boolean getImageQuality() {
		return hqImages;
	}

	/**
	 * returns the twitter image suffix depending on the resolution
	 *
	 * @return suffix string
	 */
	public String getImageSuffix() {
		if (hqImages)
			return PROFILE_IMG_HIGH_RES;
		return PROFILE_IMG_LOW_RES;
	}

	/**
	 * returns the suffix for the banner image link
	 *
	 * @return suffix string
	 */
	public String getBannerSuffix() {
		if (hqImages)
			return BANNER_IMG_MID_RES;
		return BANNER_IMG_LOW_RES;
	}

	/**
	 * sets the image quality
	 *
	 * @param enable true if small thumbnails should be in high resolution
	 */
	public void setHighQualityImage(boolean enable) {
		hqImages = enable;
		Editor edit = settings.edit();
		edit.putBoolean(IMAGE_QUALITY, enable);
		edit.apply();
	}

	/**
	 * answer loading enabled
	 *
	 * @return true if enabled
	 */
	public boolean replyLoadingEnabled() {
		return loadAnswer;
	}

	/**
	 * enable/disable answer load load
	 *
	 * @param loadAnswer true if enabled
	 */
	public void setAnswerLoad(boolean loadAnswer) {
		this.loadAnswer = loadAnswer;

		Editor edit = settings.edit();
		edit.putBoolean(ANSWER_LOAD, loadAnswer);
		edit.apply();
	}

	/**
	 * @return true if tweet indicators enabled
	 */
	public boolean tweetIndicatorsEnabled() {
		return tweetIndicators;
	}

	/**
	 * enable/disable tweet indicators
	 *
	 * @param enable true to enable tweet indicators
	 */
	public void enableTweetIndicators(boolean enable) {
		tweetIndicators = enable;

		Editor edit = settings.edit();
		edit.putBoolean(TWEET_INDICATOR, enable);
		edit.apply();
	}

	/**
	 * get selected location information
	 *
	 * @return saved location information
	 */
	public Location getTrendLocation() {
		return location;
	}

	/**
	 * set selected location information
	 *
	 * @param location location information
	 */
	public void setTrendLocation(Location location) {
		this.location = location;

		Editor edit = settings.edit();
		edit.putInt(TREND_ID, location.getId());
		edit.putString(TREND_LOC, location.getName());
		edit.apply();
	}


	/**
	 * get loading limit of tweets/users
	 *
	 * @return max numbers of tweets/users should be loaded
	 */
	public int getListSize() {
		return listSize;
	}

	/**
	 * set limit of tweets/users
	 *
	 * @param listSize max numbers of tweets/users
	 */
	public void setListSize(int listSize) {
		this.listSize = listSize;

		Editor edit = settings.edit();
		edit.putInt(LIST_SIZE, listSize);
		edit.apply();
	}

	/**
	 * get font scale
	 *
	 * @return font scale
	 */
	public float getTextScale() {
		return SCALES[indexScale];
	}

	/**
	 * get current index of the selected scale value
	 *
	 * @return current index
	 * @see #SCALES
	 */
	public int getScaleIndex() {
		return indexScale;
	}

	/**
	 * set index of the selected scale
	 *
	 * @param index index of the selected scale
	 * @see #SCALES
	 */
	public void setScaleIndex(int index) {
		this.indexScale = index;

		Editor edit = settings.edit();
		edit.putInt(INDEX_SCALE, index);
		edit.apply();
	}

	/**
	 * return font type
	 *
	 * @return font family
	 */
	public Typeface getTypeFace() {
		return FONTS[indexFont];
	}

	/**
	 * get font position
	 *
	 * @return font index
	 */
	public int getFontIndex() {
		return indexFont;
	}

	/**
	 * set font type
	 *
	 * @param index index of font type in array
	 */
	public void setFontIndex(int index) {
		this.indexFont = index;

		Editor edit = settings.edit();
		edit.putInt(INDEX_FONT, index);
		edit.apply();
	}

	/**
	 * check if 'like' is used instead of 'favorite'
	 *
	 * @return true if 'like' should be used
	 */
	public boolean likeEnabled() {
		return enableLike;
	}

	/**
	 * enable 'like' or 'favorite'
	 *
	 * @param enableLike true to enable 'like', false to enable 'favorite'
	 */
	public void enableLike(boolean enableLike) {
		this.enableLike = enableLike;

		Editor edit = settings.edit();
		edit.putBoolean(ENABLE_LIKE, enableLike);
		edit.apply();
	}

	/**
	 * check if Twitter link alternative is set (e.G. nitter.net)
	 *
	 * @return true if link alternative is set
	 */
	public boolean twitterAltSet() {
		return twitterAlt;
	}

	/**
	 * set Twitter alternative link
	 *
	 * @param enable true to enable alternative link
	 */
	public void setTwitterAlt(boolean enable) {
		twitterAlt = enable;

		Editor edit = settings.edit();
		edit.putBoolean(ENABLE_TWITTER_ALT, enable);
		edit.apply();
	}

	/**
	 * get hostname of the Twitter service to use
	 *
	 * @return custom host domain name if alternative is set, otherwise default Twitter host
	 */
	public String getTwitterHostname() {
		if (twitterAlt)
			return TWITTER_ALT_HOST;
		return TWITTER_HOST;
	}

	/**
	 * set proxy address
	 *
	 * @param proxyHost address of proxy
	 * @param proxyPort port of proxy
	 */
	public void setProxyServer(String proxyHost, String proxyPort, String proxyUser, String proxyPass) {
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
		this.proxyUser = proxyUser;
		this.proxyPass = proxyPass;

		Editor edit = settings.edit();
		edit.putString(PROXY_ADDR, proxyHost);
		edit.putString(PROXY_PORT, proxyPort);
		edit.putString(PROXY_USER, proxyUser);
		edit.putString(PROXY_PASS, proxyPass);
		edit.apply();

		notifySettingsChange();
	}

	/**
	 * Remove Proxy settings
	 */
	public void clearProxyServer() {
		isProxyEnabled = false;
		isProxyAuthSet = false;
		proxyHost = "";
		proxyPort = "";
		proxyUser = "";
		proxyPass = "";

		Editor edit = settings.edit();
		edit.remove(PROXY_SET);
		edit.remove(AUTH_SET);
		edit.remove(PROXY_ADDR);
		edit.remove(PROXY_PORT);
		edit.remove(PROXY_USER);
		edit.remove(PROXY_PASS);
		edit.apply();

		notifySettingsChange();
	}

	/**
	 * set proxy server connection enabled
	 *
	 * @param enable true if proxy connection is set
	 */
	public void setProxyEnabled(boolean enable) {
		isProxyEnabled = enable;

		Editor edit = settings.edit();
		edit.putBoolean(PROXY_SET, enable);
		edit.apply();
	}

	/**
	 * set proxy authentication enabled
	 *
	 * @param enable true if proxy auth is enabled
	 */
	public void setProxyAuthSet(boolean enable) {
		isProxyAuthSet = enable;

		Editor edit = settings.edit();
		edit.putBoolean(AUTH_SET, enable);
		edit.apply();
	}

	/**
	 * get proxy address
	 *
	 * @return proxy address
	 */
	public String getProxyHost() {
		if (isProxyEnabled)
			return proxyHost;
		return "";
	}

	/**
	 * get proxy port
	 *
	 * @return proxy port string
	 */
	public String getProxyPort() {
		if (isProxyEnabled)
			return proxyPort;
		return "";
	}

	/**
	 * get proxy port
	 *
	 * @return proxy port integer
	 */
	public int getProxyPortNumber() {
		if (isProxyEnabled && !proxyPort.isEmpty() && proxyPort.length() < 6)
			return Integer.parseInt(proxyPort);
		return 0;
	}

	/**
	 * get proxy user login
	 *
	 * @return username
	 */
	public String getProxyUser() {
		if (isProxyAuthSet) {
			return proxyUser;
		}
		return "";
	}

	/**
	 * get proxy password
	 *
	 * @return login password
	 */
	public String getProxyPass() {
		if (isProxyAuthSet) {
			return proxyPass;
		}
		return "";
	}

	/**
	 * check if proxy connection is set
	 *
	 * @return true if proxy is set
	 */
	public boolean isProxyEnabled() {
		return isProxyEnabled;
	}

	/**
	 * check if proxy authentication is set
	 *
	 * @return true if user auth is set
	 */
	public boolean isProxyAuthSet() {
		return isProxyAuthSet;
	}

	/**
	 * check if proxy warning should be ignored
	 *
	 * @return true if proxy warning should be ignored
	 */
	public boolean ignoreProxyWarning() {
		return ignoreProxyWarning;
	}

	/**
	 * enable/ignore proxy warning
	 *
	 * @param ignore true to ignore proxy warning
	 */
	public void setIgnoreProxyWarning(boolean ignore) {
		ignoreProxyWarning = ignore;
		Editor e = settings.edit();
		e.putBoolean(PROXY_IGNORE, ignore);
		e.apply();
	}

	/**
	 * Check if current user is logged in
	 *
	 * @return true if current user is logged in
	 */
	public boolean isLoggedIn() {
		return loggedIn;
	}


	/**
	 * set app login status
	 *
	 * @param login true if current user is logged in successfully
	 */
	public void setogin(boolean login) {
		loggedIn = login;
		Editor e = settings.edit();
		e.putBoolean(LOGGED_IN, login);
		e.apply();
	}

	/**
	 * return access token of the current user
	 *
	 * @return first access token
	 */
	public String getAccessToken() {
		return auth_key1;
	}

	/**
	 * set access token of the current user
	 *
	 * @param token first access token
	 */
	public void setAccessToken(String token) {
		this.auth_key1 = token;
		Editor e = settings.edit();
		e.putString(CURRENT_AUTH_KEY1, token);
		e.apply();
	}

	/**
	 * return second access token of the current user
	 *
	 * @return first access token
	 */
	public String getTokenSecret() {
		return auth_key2;
	}

	/**
	 * set second access token of the current user
	 *
	 * @param token first access token
	 */
	public void setTokenSecret(String token) {
		this.auth_key2 = token;
		Editor e = settings.edit();
		e.putString(CURRENT_AUTH_KEY2, token);
		e.apply();
	}

	/**
	 * get Consumer keys
	 *
	 * @return key string
	 */
	public String getConsumerKey() {
		return api_key1;
	}

	/**
	 * get consumer key secret
	 *
	 * @return key string
	 */
	public String getConsumerSecret() {
		return api_key2;
	}

	/**
	 * get current users ID
	 *
	 * @return User ID
	 */
	public long getCurrentUserId() {
		return userId;
	}

	/**
	 * set current user ID
	 *
	 * @param userId current user ID
	 */
	public void setUserId(long userId) {
		this.userId = userId;
		Editor e = settings.edit();
		e.putLong(CURRENT_ID, userId);
		e.apply();
	}

	/**
	 * sets custom API consumer keys
	 *
	 * @param key1 consumer key
	 * @param key2 consumer key secret
	 */
	public void setCustomAPI(String key1, String key2) {
		customAPIKey = true;
		this.api_key1 = key1;
		this.api_key2 = key2;

		Editor e = settings.edit();
		e.putBoolean(CUSTOM_CONSUMER_KEY_SET, true);
		e.putString(CUSTOM_CONSUMER_KEY_1, key1);
		e.putString(CUSTOM_CONSUMER_KEY_2, key2);
		e.apply();
	}

	/**
	 * remove all API keys
	 */
	public void removeCustomAPI() {
		customAPIKey = false;
		this.api_key1 = "";
		this.api_key2 = "";

		Editor e = settings.edit();
		e.remove(CUSTOM_CONSUMER_KEY_SET);
		e.remove(CUSTOM_CONSUMER_KEY_1);
		e.remove(CUSTOM_CONSUMER_KEY_2);
		e.apply();
	}

	/**
	 * check if custom API consumer keys are set
	 *
	 * @return true if custom API keys are set
	 */
	public boolean isCustomApiSet() {
		return customAPIKey;
	}

	/**
	 * clear user specific settings
	 */
	public void logout() {
		loggedIn = false;
		auth_key1 = "";
		auth_key2 = "";
		userId = 0;
		Editor e = settings.edit();
		e.remove(LOGGED_IN);
		e.remove(CURRENT_AUTH_KEY1);
		e.remove(CURRENT_AUTH_KEY2);
		e.remove(CURRENT_ID);
		e.apply();
		notifySettingsChange();
	}

	/**
	 * register settings listener
	 * changes like new proxy settings requires re initialization
	 * registered instances can be notified if these setting change
	 *
	 * @param listener listener called when some settings change
	 */
	public void addSettingsChangeListener(@NonNull SettingsListener listener) {
		settingsListeners.add(listener);
	}

	/**
	 * notify listener when settings changes and clear old instances
	 */
	private void notifySettingsChange() {
		for (SettingsListener listener : settingsListeners) {
			listener.onSettingsChange();
		}
		settingsListeners.clear();
	}

	/**
	 * Init setting values
	 */
	private void initialize() {
		// app settings
		background_color = settings.getInt(BACKGROUND_COLOR, DEFAULT_BACKGROUND_COLOR);
		highlight_color = settings.getInt(HIGHLIGHT_COLOR, DEFAULT_HIGHLIGHT_COLOR);
		font_color = settings.getInt(FONT_COLOR, DEFAULT_FONT_COLOR);
		popup_color = settings.getInt(POPUP_COLOR, DEFAULT_POPUP_COLOR);
		card_color = settings.getInt(CARD_COLOR, DEFAULT_CARD_COLOR);
		icon_color = settings.getInt(ICON_COLOR, DEFAULT_ICON_COLOR);
		retweet_color = settings.getInt(RT_COLOR, DEFAULT_RT_ICON_COLOR);
		favorite_color = settings.getInt(FV_COLOR, DEFAULT_FV_ICON_COLOR);
		request_color = settings.getInt(F_REQ_COLOR, DEFAULT_FR_ICON_COLOR);
		follow_color = settings.getInt(FOLLOW_COLOR, DEFAULT_FW_ICON_COLOR);
		indexFont = settings.getInt(INDEX_FONT, DEFAULT_FONT_INDEX);
		indexScale = settings.getInt(INDEX_SCALE, DEFAULT_SCALE_INDEX);
		listSize = settings.getInt(LIST_SIZE, DEFAULT_LIST_SIZE);
		isProxyEnabled = settings.getBoolean(PROXY_SET, false);
		isProxyAuthSet = settings.getBoolean(AUTH_SET, false);
		ignoreProxyWarning = settings.getBoolean(PROXY_IGNORE, false);
		loggedIn = settings.getBoolean(LOGGED_IN, false);
		loadImage = settings.getBoolean(IMAGE_LOAD, true);
		loadAnswer = settings.getBoolean(ANSWER_LOAD, false);
		tweetIndicators = settings.getBoolean(TWEET_INDICATOR, true);
		hqImages = settings.getBoolean(IMAGE_QUALITY, false);
		toolbarOverlap = settings.getBoolean(PROFILE_OVERLAP, true);
		linkPreview = settings.getBoolean(LINK_PREVIEW, false);
		filterResults = settings.getBoolean(FILTER_RESULTS, true);
		enableLike = settings.getBoolean(ENABLE_LIKE, false);
		customAPIKey = settings.getBoolean(CUSTOM_CONSUMER_KEY_SET, false);
		twitterAlt = settings.getBoolean(ENABLE_TWITTER_ALT, false);
		proxyHost = settings.getString(PROXY_ADDR, "");
		proxyPort = settings.getString(PROXY_PORT, "");
		proxyUser = settings.getString(PROXY_USER, "");
		proxyPass = settings.getString(PROXY_PASS, "");
		api_key1 = settings.getString(CUSTOM_CONSUMER_KEY_1, "");
		api_key2 = settings.getString(CUSTOM_CONSUMER_KEY_2, "");
		String place = settings.getString(TREND_LOC, DEFAULT_LOCATION_NAME);
		int woeId = settings.getInt(TREND_ID, DEFAULT_LOCATION_ID);
		location = new LocationImpl(place, woeId);

		// user specific settings
		auth_key1 = settings.getString(CURRENT_AUTH_KEY1, "");
		auth_key2 = settings.getString(CURRENT_AUTH_KEY2, "");
		userId = settings.getLong(CURRENT_ID, 0);
	}

	/**
	 * global listener for settings
	 */
	public interface SettingsListener {

		/**
		 * called when settings change
		 */
		void onSettingsChange();
	}
}