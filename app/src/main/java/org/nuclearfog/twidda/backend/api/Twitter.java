package org.nuclearfog.twidda.backend.api;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nuclearfog.twidda.backend.lists.Users;
import org.nuclearfog.twidda.backend.utils.StringTools;
import org.nuclearfog.twidda.backend.utils.TLSSocketFactory;
import org.nuclearfog.twidda.backend.utils.Tokens;
import org.nuclearfog.twidda.database.ExcludeDatabase;
import org.nuclearfog.twidda.model.Tweet;
import org.nuclearfog.twidda.model.User;
import org.nuclearfog.twidda.database.GlobalSettings;

import java.io.IOException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * new API implementation to replace twitter4j and add version 2.0 support
 *
 * @author nuclearfog
 */
public class Twitter {

    private static final String OAUTH = "1.0";
    public static final String SIGNATURE_ALG = "HMAC-SHA256";

    private static final String API = "https://api.twitter.com/";
    private static final String AUTHENTICATE = API + "oauth/authenticate";
    private static final String REQUEST_TOKEN = API + "oauth/request_token";
    private static final String OAUTH_VERIFIER = API + "oauth/access_token";
    private static final String CREDENTIALS = API + "1.1/account/verify_credentials.json";
    private static final String USER_LOOKUP = API + "1.1/users/show.json";
    private static final String USER_FOLLOWING = API + "1.1/friends/list.json";
    private static final String USER_FOLLOWER = API + "1.1/followers/list.json";
    private static final String USER_SEARCH = API + "1.1/users/search.json";
    private static final String USER_LIST_MEMBER = API + "1.1/lists/members.json";
    private static final String USER_LIST_SUBSCRIBER = API + "1.1/lists/subscribers.json";
    private static final String BLOCK_LIST = API + "1.1/blocks/list.json";
    private static final String MUTES_LIST = API + "1.1/mutes/users/list.json";
    private static final String SHOW_TWEET = API + "1.1/statuses/show.json";
    private static final String SHOW_HOME = API + "1.1/statuses/home_timeline.json";
    private static final String SHOW_MENTIONS = API + "1.1/statuses/mentions_timeline.json";
    private static final String SHOW_USER_TL = API + "1.1/statuses/user_timeline.json";
    private static final String SHOW_USER_FAV = API + "1.1/favorites/list.json";
    public static final String REQUEST_URL = AUTHENTICATE + "?oauth_token=";

    private static Twitter instance;

    private OkHttpClient client;
    private GlobalSettings settings;
    private ExcludeDatabase filterList;
    private Tokens tokens;


    private Twitter(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            try {
                TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                factory.init((KeyStore) null);
                X509TrustManager manager = (X509TrustManager) factory.getTrustManagers()[0];
                client = new OkHttpClient().newBuilder().sslSocketFactory(new TLSSocketFactory(), manager).build();
            } catch (Exception e) {
                client = new OkHttpClient().newBuilder().build();
            }
        } else {
            client = new OkHttpClient().newBuilder().build();
        }
        tokens = Tokens.getInstance(context);
        settings = GlobalSettings.getInstance(context);
        filterList = new ExcludeDatabase(context);
    }

    /**
     * get singleton instance
     *
     * @return instance of this class
     */
    public static Twitter get(Context context) {
        if (instance == null) {
            instance = new Twitter(context);
        }
        return instance;
    }

    /**
     * request temporary access token to pass it to the Twitter login page
     *
     * @return a temporary access token created by Twitter
     */
    public String getRequestToken() throws TwitterException {
        try {
            Response response = post(REQUEST_TOKEN, new ArrayList<>(1));
            if (response.code() == 200 && response.body() != null) {
                String res = response.body().string();
                Uri uri = Uri.parse(AUTHENTICATE + "?" + res);
                return uri.getQueryParameter("oauth_token");
            } else {
                throw new TwitterException(response);
            }
        } catch (IOException e) {
            throw new TwitterException(e);
        }
    }

    /**
     * login to twitter using pin and add store access tokens
     *
     * @param pin pin from the login website
     */
    public User login(String oauth_token, String pin) throws TwitterException {
        try {
            List<String> params = new ArrayList<>(3);
            params.add("oauth_verifier=" + pin);
            params.add("oauth_token=" + oauth_token);
            Response response = post(OAUTH_VERIFIER, params);
            if (response.code() == 200 && response.body() != null) {
                String res = response.body().string();
                // extrect tokens from link
                Uri uri = Uri.parse(OAUTH_VERIFIER + "?" + res);
                settings.setAccessToken(uri.getQueryParameter("oauth_token"));
                settings.setTokenSecret(uri.getQueryParameter("oauth_token_secret"));
                settings.setUserId(Long.parseLong(uri.getQueryParameter("user_id")));
                settings.setogin(true);
                return getCredentials();
            } else {
                throw new TwitterException(response);
            }
        } catch (IOException e) {
            throw new TwitterException(e);
        }
    }

    /**
     * get credentials of the current user
     *
     * @return current user
     */
    public User getCredentials() throws TwitterException {
        try {
            Response response = get(CREDENTIALS, new ArrayList<>(1));
            if (response.body() != null) {
                JSONObject json = new JSONObject(response.body().string());
                if (response.code() == 200) {
                    return new UserV1(json);
                } else {
                    throw new TwitterException(json);
                }
            } else {
                throw new TwitterException(response);
            }
        } catch (IOException err) {
            throw new TwitterException(err);
        } catch (JSONException err) {
            throw new TwitterException(err);
        }
    }

    /**
     * lookup user and return user information
     *
     * @param id ID of the user
     * @return user information
     */
    public User showUser(long id) throws  TwitterException {
        List<String> params = new ArrayList<>(3);
        params.add("user_id=" + id);
        return showUser(params);
    }

    /**
     * lookup user and return user information
     *
     * @param name screen name of the user
     * @return user information
     */
    public User showUser(String name) throws TwitterException {
        List<String> params = new ArrayList<>(3);
        params.add("screen_name=" + name);
        return showUser(params);
    }

    /**
     * create a list of users a specified user is following
     *
     * @param userId ID of the user
     * @param cursor cursor value used to parse the list
     * @return list of users
     */
    public Users getFollowing(long userId, long cursor) throws TwitterException {
        List<String> params = new ArrayList<>(5);
        params.add("user_id=" + userId);
        params.add("cursor=" + cursor);
        return getUsers1(USER_FOLLOWING, params);
    }

    /**
     * create a list of users following a specified user
     *
     * @param userId ID of the user
     * @param cursor cursor value used to parse the list
     * @return list of users
     */
    public Users getFollower(long userId, long cursor) throws TwitterException {
        List<String> params = new ArrayList<>(5);
        params.add("user_id=" + userId);
        params.add("cursor=" + cursor);
        return getUsers1(USER_FOLLOWER, params);
    }

    /**
     * create a list of user list members
     *
     * @param listId ID of the list
     * @param cursor cursor value used to parse the list
     * @return list of users
     */
    public Users getListMember(long listId, long cursor) throws TwitterException {
        List<String> params = new ArrayList<>(5);
        params.add("list_id=" + listId);
        params.add("cursor=" + cursor);
        return getUsers1(USER_LIST_MEMBER, params);
    }

    /**
     * create a list of user list subscriber
     *
     * @param listId ID of the list
     * @param cursor cursor value used to parse the list
     * @return list of users
     */
    public Users getListSubscriber(long listId, long cursor) throws TwitterException {
        List<String> params = new ArrayList<>(5);
        params.add("list_id=" + listId);
        params.add("cursor=" + cursor);
        return getUsers1(USER_LIST_SUBSCRIBER, params);
    }

    /**
     * get block list of the current user
     *
     * @param cursor cursor value used to parse the list
     * @return list of users
     */
    public Users getBlockedUsers(long cursor) throws TwitterException {
        List<String> params = new ArrayList<>(4);
        params.add("cursor=" + cursor);
        return getUsers1(BLOCK_LIST, params);
    }

    /**
     * get mute list of the current user
     *
     * @param cursor cursor value used to parse the list
     * @return list of users
     */
    public Users getMutedUsers(long cursor) throws TwitterException {
        List<String> params = new ArrayList<>(4);
        params.add("cursor=" + cursor);
        return getUsers1(MUTES_LIST, params);
    }

    /**
     * get users retweeting a tweet
     *
     * @param tweetId ID of the tweet
     * @return user list
     */
    public Users getRetweetingUsers(long tweetId) throws TwitterException {
        String endpoint = API + "2/tweets/" + tweetId + "/retweeted_by";
        return getUsers2(endpoint);
    }

    /**
     * get users liking a tweet
     *
     * @param tweetId ID of the tweet
     * @return user list
     */
    public Users getLikingUsers(long tweetId) throws TwitterException {
        String endpoint = API + "2/tweets/" + tweetId + "/liking_users";
        return getUsers2(endpoint);
    }

    /**
     * search for users matching a search string
     *
     * @param search search string
     * @param page page of the search results
     * @return list of users
     */
    public Users searchUsers(String search, long page) throws TwitterException {
        // search endpoint only supports pages parameter
        long currentPage = page > 0 ? page : 1;
        long nextPage = currentPage + 1;

        List<String> params = new ArrayList<>(4);
        params.add("q=" + search);
        params.add("page=" + currentPage);
        params.add("count=" + settings.getListSize());
        try {
            Response response = get(USER_SEARCH, params);
            if (response.body() != null) {
                if (response.code() == 200) {
                    JSONArray array = new JSONArray(response.body().string());
                    if (array.length() < 20)
                        nextPage = 0;
                    Users users = new Users(currentPage - 1, nextPage);
                    long homeId = settings.getCurrentUserId();
                    // filter results if enabled
                    if (settings.filterResults()) {
                        Set<Long> exclude = filterList.getExcludeSet();
                        for (int i = 0; i < array.length(); i++) {
                            User user = new UserV1(array.getJSONObject(i), homeId);
                            if (!exclude.contains(user.getId())) {
                                users.add(user);
                            }
                        }
                    } else {
                        for (int i = 0; i < array.length(); i++) {
                            User user = new UserV1(array.getJSONObject(i), homeId);
                            users.add(user);
                        }
                    }
                    return users;
                } else {
                    JSONObject result = new JSONObject(response.body().string());
                    throw new TwitterException(result);
                }
            } else {
                throw new TwitterException(response);
            }
        } catch (IOException err) {
            throw new TwitterException(err);
        } catch (JSONException err) {
            throw new TwitterException(err);
        }
    }

    /**
     * show current user's home timeline
     *
     * @param minId get tweets with ID above the min ID
     * @param maxId get tweets with ID under the max ID
     * @return list of tweets
     */
    public List<Tweet> getHomeTimeline(long minId, long maxId) throws TwitterException {
        List<String> params = new ArrayList<>(5);
        if (minId > 0)
            params.add("since_id=" + minId);
        if (maxId > 0)
            params.add("max_id=" + maxId);
        return getTweets1(SHOW_HOME, params);
    }

    /**
     * show current user's home timeline
     *
     * @param minId get tweets with ID above the min ID
     * @param maxId get tweets with ID under the max ID
     * @return list of tweets
     */
    public List<Tweet> getMentionTimeline(long minId, long maxId) throws TwitterException {
        List<String> params = new ArrayList<>(5);
        if (minId > 0)
            params.add("since_id=" + minId);
        if (maxId > 1)
            params.add("max_id=" + maxId);
        return getTweets1(SHOW_MENTIONS, params);
    }

    /**
     * show the timeline of an user
     *
     * @param userId ID of the user
     * @param minId get tweets with ID above the min ID
     * @param maxId get tweets with ID under the max ID
     * @return list of tweets
     */
    public List<Tweet> getUserTimeline(long userId, long minId, long maxId) throws TwitterException {
        List<String> params = new ArrayList<>(6);
        if (minId > 0)
            params.add("since_id=" + minId);
        if (maxId > 1)
            params.add("max_id=" + maxId);
        params.add("user_id=" + userId);
        return getTweets1(SHOW_USER_TL, params);
    }

    /**
     * show the timeline of an user
     *
     * @param screen_name screen name of the user (without '@')
     * @param minId get tweets with ID above the min ID
     * @param maxId get tweets with ID under the max ID
     * @return list of tweets
     */
    public List<Tweet> getUserTimeline(String screen_name, long minId, long maxId) throws TwitterException {
        List<String> params = new ArrayList<>(6);
        if (minId > 0)
            params.add("since_id=" + minId);
        if (maxId > 1)
            params.add("max_id=" + maxId);
        params.add("screen_name=" + screen_name);
        return getTweets1(SHOW_USER_TL, params);
    }

    /**
     * show the favorite tweets of an user
     *
     * @param userId ID of the user
     * @param minId get tweets with ID above the min ID
     * @param maxId get tweets with ID under the max ID
     * @return list of tweets
     */
    public List<Tweet> getUserFavorits(long userId, long minId, long maxId) throws TwitterException {
        List<String> params = new ArrayList<>(6);
        if (minId > 0)
            params.add("since_id=" + minId);
        if (maxId > 1)
            params.add("max_id=" + maxId);
        params.add("user_id=" + userId);
        return getTweets1(SHOW_USER_FAV, params);
    }

    /**
     * show the favorite tweets of an user
     *
     * @param screen_name screen name of the user (without '@')
     * @param minId get tweets with ID above the min ID
     * @param maxId get tweets with ID under the max ID
     * @return list of tweets
     */
    public List<Tweet> getUserFavorits(String screen_name, long minId, long maxId) throws TwitterException {
        List<String> params = new ArrayList<>(6);
        if (minId > 0)
            params.add("since_id=" + minId);
        if (maxId > 1)
            params.add("max_id=" + maxId);
        params.add("screen_name=" + screen_name);
        return getTweets1(SHOW_USER_FAV, params);
    }

    /**
     * lookup tweet by ID
     * @param id tweet ID
     * @return tweet information
     */
    public Tweet showTweet(long id) throws TwitterException {
        List<String> params = new ArrayList<>(3);
        params.add("id=" + id);
        params.add(TweetV1.EXT_MODE);
        try {
            Response response = get(SHOW_TWEET, params);
            if (response.body() != null) {
                JSONObject json = new JSONObject(response.body().string());
                if (response.code() == 200) {
                    return new TweetV1(json, settings.getCurrentUserId());
                } else {
                    throw new TwitterException(json);
                }
            } else {
                throw new TwitterException(response);
            }
        } catch (IOException err) {
            throw new TwitterException(err);
        } catch (JSONException err) {
            throw new TwitterException(err);
        }
    }

    /**
     * lookup single user
     *
     * @param params additional parameter added to request
     * @return user information
     */
    private User showUser(List<String> params) throws TwitterException {
        try {
            params.add(UserV1.INCLUDE_ENTITIES);
            Response response = get(USER_LOOKUP, params);
            if (response.body() != null) {
                JSONObject json = new JSONObject(response.body().string());
                if (response.code() == 200) {
                    return new UserV1(json, settings.getCurrentUserId());
                } else {
                    throw new TwitterException(json);
                }
            } else {
                throw new TwitterException(response);
            }
        } catch (IOException err) {
            throw new TwitterException(err);
        } catch (JSONException err) {
            throw new TwitterException(err);
        }
    }

    /**
     * get tweets using an endpoint
     *
     * @param endpoint endpoint url to fetch the tweets
     * @param params additional parameters
     * @return list of tweets
     */
    private List<Tweet> getTweets1(String endpoint, List<String> params) throws TwitterException {
        try {
            params.add(TweetV1.EXT_MODE);
            params.add("count=" + settings.getListSize());
            Response response = get(endpoint, params);
            if (response.body() != null) {
                if (response.code() == 200) {
                    JSONArray array = new JSONArray(response.body().string());
                    long homeId = settings.getCurrentUserId();
                    List<Tweet> tweets = new ArrayList<>(array.length() + 1);
                    for (int i = 0; i < array.length(); i++)
                        tweets.add(new TweetV1(array.getJSONObject(i), homeId));
                    return tweets;
                } else {
                    JSONObject json = new JSONObject(response.body().string());
                    throw new TwitterException(json);
                }
            } else {
                throw new TwitterException(response);
            }
        } catch (IOException err) {
            throw new TwitterException(err);
        } catch (JSONException err) {
            throw new TwitterException(err);
        }
    }

    /**
     * create a list of users using API v 1.1
     *
     * @param endpoint endpoint url to get the user data from
     * @param params   additional parameters
     * @return user list
     */
    private Users getUsers1(String endpoint, List<String> params) throws TwitterException {
        try {
            params.add("count=" + settings.getListSize());
            params.add(UserV1.SKIP_STAT);
            Response response = get(endpoint, params);
            if (response.body() != null) {
                JSONObject json = new JSONObject(response.body().string());
                if (response.code() == 200) {
                    if (json.has("users")) {
                        JSONArray array = json.getJSONArray("users");
                        long prevCursor = json.getLong("previous_cursor");
                        long nextCursor = json.getLong("next_cursor");
                        Users users = new Users(prevCursor, nextCursor);
                        long homeId = settings.getCurrentUserId();
                        for (int i = 0; i < array.length(); i++) {
                            users.add(new UserV1(array.getJSONObject(i), homeId));
                        }
                        return users;
                    } else {
                        // return empty list
                        return new Users();
                    }
                } else {
                    throw new TwitterException(json);
                }
            } else {
                throw new TwitterException(response);
            }
        } catch (IOException err) {
            throw new TwitterException(err);
        } catch (JSONException err) {
            throw new TwitterException(err);
        }
    }

    /**
     * create a list of users using API v 2
     *
     * @param endpoint endpoint url to get the user data from
     * @return user list
     */
    private Users getUsers2(String endpoint) throws TwitterException {
        try {
            List<String> params = new ArrayList<>(2);
            params.add(UserV2.PARAMS);
            Response response = get(endpoint, params);
            if (response.body() != null) {
                JSONObject json = new JSONObject(response.body().string());
                if (response.code() == 200) {
                    if (json.has("data")) {
                        JSONArray array = json.getJSONArray("data");
                        Users users = new Users();
                        long homeId = settings.getCurrentUserId();
                        for (int i = 0; i < array.length(); i++) {
                            users.add(new UserV2(array.getJSONObject(i), homeId));
                        }
                        return users;
                    } else {
                        // return empty list
                        return new Users();
                    }
                } else {
                    throw new TwitterException(json);
                }
            } else {
                throw new TwitterException(response);
            }
        } catch (IOException err) {
            throw new TwitterException(err);
        } catch (JSONException err) {
            throw new TwitterException(err);
        }
    }

    /**
     * create and call POST endpoint
     *
     * @param endpoint endpoint url
     * @return http resonse
     */
    private Response post(String endpoint, List<String> params) throws IOException {
        String authHeader = buildHeader("POST", endpoint, params);
        String url = appendParams(endpoint, params);
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), "");
        Request request = new Request.Builder().url(url).addHeader("Authorization", authHeader).post(body).build();
        return client.newCall(request).execute();
    }

    /**
     * create and call GET endpoint
     *
     * @param endpoint endpoint url
     * @return http response
     */
    private Response get(String endpoint, List<String> params) throws IOException {
        String authHeader = buildHeader("GET", endpoint, params);
        String url = appendParams(endpoint, params);
        Request request = new Request.Builder().url(url).addHeader("Authorization", authHeader).get().build();
        return client.newCall(request).execute();
    }

    /**
     * create http header with credentials and signature
     *
     * @param method endpoint method to call
     * @param endpoint endpoint url
     * @param params parameter to add to signature
     * @return header string
     */
    private String buildHeader(String method, String endpoint, List<String> params) {
        String timeStamp = StringTools.getTimestamp();
        String random = StringTools.getRandomString();
        String signkey = tokens.getConsumerSec() + "&";
        String oauth_token_param = "";

        // init default parameters
        TreeSet<String> sortedParams = new TreeSet<>();
        sortedParams.add("oauth_callback=oob");
        sortedParams.add("oauth_consumer_key=" + tokens.getConsumerKey());
        sortedParams.add("oauth_nonce=" + random);
        sortedParams.add("oauth_signature_method=" + SIGNATURE_ALG);
        sortedParams.add("oauth_timestamp=" + timeStamp);
        sortedParams.add("oauth_version=" + OAUTH);
        // add custom parameters
        sortedParams.addAll(params);

        // only add tokens if there is no login process
        if (!REQUEST_TOKEN.equals(endpoint) && !OAUTH_VERIFIER.equals(endpoint)) {
            sortedParams.add("oauth_token=" + settings.getAccessToken());
            oauth_token_param = ", oauth_token=\"" + settings.getAccessToken() + "\"";
            signkey += settings.getTokenSecret();
        }

        // build string with sorted parameters
        StringBuilder paramStr = new StringBuilder();
        for (String param : sortedParams)
            paramStr.append(param).append('&');
        paramStr.deleteCharAt(paramStr.length() - 1);

        // calculate oauth signature
        String signature = StringTools.sign(method, endpoint, paramStr.toString(), signkey);

        // create header string
        return "OAuth oauth_callback=\"oob\"" +
                ", oauth_consumer_key=\"" + tokens.getConsumerKey() + "\"" +
                ", oauth_nonce=\""+ random + "\"" +
                ", oauth_signature=\"" + signature + "\"" +
                ", oauth_signature_method=\""+ SIGNATURE_ALG + "\"" +
                ", oauth_timestamp=\"" + timeStamp + "\""
                + oauth_token_param +
                ", oauth_version=\"" + OAUTH + "\"";
    }

    /**
     * build url with param
     *
     * @param url url without parameters
     * @param params parameters
     * @return url with parameters
     */
    private String appendParams(String url, List<String> params) {
        if (!params.isEmpty()) {
            StringBuilder result = new StringBuilder(url);
            result.append('?');
            for (String param : params)
                result.append(param).append('&');
            result.deleteCharAt(result.length() - 1);
            return result.toString();
        }
        return url;
    }
}