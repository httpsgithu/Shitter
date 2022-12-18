package org.nuclearfog.twidda.backend.api.twitter.impl;

import org.json.JSONException;
import org.json.JSONObject;
import org.nuclearfog.twidda.model.Metrics;

/**
 * Implementation of {@link Metrics} using API V2
 *
 * @author nuclearfog
 */
public class MetricsV2 implements Metrics {

	private static final long serialVersionUID = -305086994844228862L;

	private long statusId;
	private int impressions;
	private int retweets;
	private int likes;
	private int replies;
	private int quotes;
	private int linkClicks;
	private int profileClicks;
	private int videoViews;

	/**
	 * @param json tweet json object containing metrics information
	 */
	public MetricsV2(JSONObject json, long statusId) throws JSONException {
		JSONObject metricsOrganic = json.getJSONObject("organic_metrics");
		JSONObject metricsPublic = json.getJSONObject("public_metrics");
		impressions = metricsOrganic.optInt("impression_count", 0);
		retweets = metricsOrganic.optInt("retweet_count", 0);
		likes = metricsOrganic.optInt("like_count", 0);
		replies = metricsOrganic.optInt("reply_count", 0);
		quotes = metricsPublic.optInt("quote_count", 0);
		linkClicks = metricsOrganic.optInt("url_link_clicks", 0);
		profileClicks = metricsOrganic.optInt("user_profile_clicks", 0);
		videoViews = metricsOrganic.optInt("view_count", 0);
		this.statusId = statusId;
	}


	@Override
	public long getStatusId() {
		return statusId;
	}


	@Override
	public int getViews() {
		return impressions;
	}


	@Override
	public int getReposts() {
		return retweets;
	}


	@Override
	public int getFavorits() {
		return likes;
	}


	@Override
	public int getReplies() {
		return replies;
	}


	@Override
	public int getQuoteCount() {
		return quotes;
	}


	@Override
	public int getLinkClicks() {
		return linkClicks;
	}


	@Override
	public int getProfileClicks() {
		return profileClicks;
	}


	@Override
	public int getVideoViews() {
		return videoViews;
	}
}