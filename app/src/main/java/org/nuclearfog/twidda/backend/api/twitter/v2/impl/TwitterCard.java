package org.nuclearfog.twidda.backend.api.twitter.v2.impl;

import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;
import org.nuclearfog.twidda.model.Card;

/**
 * Twitter card implementation
 *
 * @author nuclearfog
 */
public class TwitterCard implements Card {

	private static final long serialVersionUID = 8023961098954967066L;

	private String url;
	private String title;
	private String description;
	private String imageUrl = "";

	/**
	 * @param json twitter card json
	 */
	public TwitterCard(JSONObject json) {
		JSONArray images = json.optJSONArray("images");
		url = json.optString("expanded_url", "");
		title = json.optString("title", "");
		description = json.optString("description", "");
		if (images != null && images.length() > 0) {
			// first index contains image with the highest resolutuion
			JSONObject image = images.optJSONObject(0);
			if (image != null) {
				String imageUrl = image.optString("url", "");
				if (Patterns.WEB_URL.matcher(imageUrl).matches()) {
					this.imageUrl = imageUrl;
				}
			}
		}
	}


	@Override
	public String getTitle() {
		return title;
	}


	@Override
	public String getDescription() {
		return description;
	}


	@Override
	public String getUrl() {
		return url;
	}


	@Override
	public String getImageUrl() {
		return imageUrl;
	}


	@Override
	public String getBlurHash() {
		return "";
	}


	@Override
	public boolean equals(@Nullable Object obj) {
		if (!(obj instanceof Card))
			return false;
		Card card = (Card) obj;
		return card.getTitle().equals(getTitle()) && card.getUrl().equals(getUrl()) && card.getImageUrl().equals(getImageUrl());
	}


	@NonNull
	@Override
	public String toString() {
		return "title=\"" + getTitle() + " \" description=\"" + getDescription() + "\"";
	}
}