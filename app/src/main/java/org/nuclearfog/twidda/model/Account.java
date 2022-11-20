package org.nuclearfog.twidda.model;

import androidx.annotation.Nullable;

/**
 * interface of account implementations
 * An account class collects information about a saved login.
 *
 * @author nuclearfog
 */
public interface Account {

	/**
	 * API ID used for Twitter accounts
	 */
	int API_TWITTER = 1;

	/**
	 * API ID used for Mastodon accounts
	 */
	int API_MASTODON = 2;

	/**
	 * @return ID of the account (user ID)
	 */
	long getId();

	/**
	 * @return date of the first login
	 */
	long getLoginDate();

	/**
	 * @return user information of the account
	 */
	@Nullable
	User getUser();

	/**
	 * @return API key assosiated with an account
	 */
	String getConsumerToken();

	/**
	 * @return API secret key associated with an account
	 */
	String getConsumerSecret();

	/**
	 * @return oauth token
	 */
	String getOauthToken();

	/**
	 * @return oauth secret
	 */
	String getOauthSecret();

	/**
	 * @return bearer token
	 */
	String getBearerToken();

	/**
	 * @return hostname of the social network
	 */
	String getHostname();

	/**
	 * @return type of the ID {@link #API_TWITTER}
	 */
	int getApiType();
}