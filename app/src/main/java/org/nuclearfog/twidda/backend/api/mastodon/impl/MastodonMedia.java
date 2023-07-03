package org.nuclearfog.twidda.backend.api.mastodon.impl;

import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;
import org.nuclearfog.twidda.model.Media;

/**
 * implementation for a Mastodon media item
 *
 * @author nuclearfog
 */
public class MastodonMedia implements Media {

	private static final long serialVersionUID = 8402701358586444094L;

	/**
	 * Mastodon image type
	 */
	private static final String TYPE_IMAGE = "image";

	/**
	 * Mastodon animated image type
	 */
	private static final String TYPE_GIF = "gifv";

	/**
	 * Mastodon video type
	 */
	private static final String TYPE_VIDEO = "video";

	/**
	 * Mastodon audio type
	 */
	private static final String TYPE_AUDIO = "audio";

	private String key;
	private String url;
	private String preview = "";
	private String description = "";
	private String blur;
	private int type = UNDEFINED;

	/**
	 * @param json Mastodon status JSON format
	 */
	public MastodonMedia(JSONObject json) throws JSONException {
		String typeStr = json.getString("type");
		String url = json.getString("url");
		String preview = json.optString("preview_url", "");
		key = json.getString("id");
		blur = json.optString("blurhash", "");

		switch (typeStr) {
			case TYPE_IMAGE:
				type = PHOTO;
				break;

			case TYPE_GIF:
				type = GIF;
				break;

			case TYPE_VIDEO:
				type = VIDEO;
				break;

			case TYPE_AUDIO:
				type = AUDIO;
				break;
		}
		if (Patterns.WEB_URL.matcher(url).matches()) {
			this.url = url;
		} else {
			throw new JSONException("invalid url: \"" + url + "\"");
		}
		if (Patterns.WEB_URL.matcher(preview).matches()) {
			this.preview = preview;
		}
		if (json.has("description") && !json.isNull("description")) {
			description = json.getString("description");
		}
	}


	@Override
	public String getKey() {
		return key;
	}


	@Override
	public int getMediaType() {
		return type;
	}


	@Override
	public String getUrl() {
		return url;
	}


	@Override
	public String getPreviewUrl() {
		return preview;
	}


	@Override
	public String getDescription() {
		return description;
	}


	@Override
	public String getBlurHash() {
		return blur;
	}


	@Override
	public boolean equals(@Nullable Object obj) {
		if (!(obj instanceof Media))
			return false;
		Media media = (Media) obj;
		return media.getMediaType() == getMediaType() && media.getKey().equals(getKey()) && media.getPreviewUrl().equals(getPreviewUrl()) && media.getUrl().equals(getUrl());
	}


	@NonNull
	@Override
	public String toString() {
		String tostring = "type=";
		switch (getMediaType()) {
			case PHOTO:
				tostring += "photo";
				break;

			case VIDEO:
				tostring += "video";
				break;

			case GIF:
				tostring += "gif";
				break;

			default:
				tostring += "none";
				break;
		}
		return tostring + " url=\"" + getUrl() + "\"";
	}
}