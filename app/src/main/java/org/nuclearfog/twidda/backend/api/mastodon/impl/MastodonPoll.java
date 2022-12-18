package org.nuclearfog.twidda.backend.api.mastodon.impl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nuclearfog.twidda.backend.utils.StringTools;
import org.nuclearfog.twidda.model.Poll;

/**
 * Poll implementation for Mastodon
 *
 * @author nuclearfog
 */
public class MastodonPoll implements Poll {

	private static final long serialVersionUID = 1387541658586903903L;

	private long id;
	private long exTime;
	private boolean expired;
	private boolean voted;
	private int voteCount;
	private MastodonOption[] options;


	public MastodonPoll(JSONObject json) throws JSONException {
		String idStr = json.getString("id");
		String exTimeStr = json.getString("expires_at");
		JSONArray optionsJson = json.getJSONArray("options");
		JSONArray votesJson = json.optJSONArray("own_votes");
		exTime = StringTools.getTime(exTimeStr, StringTools.TIME_MASTODON);
		expired = json.getBoolean("expired");
		voted = json.optBoolean("voted", false);
		voteCount = json.getInt("voters_count");

		options = new MastodonOption[optionsJson.length()];
		for (int i = 0 ; i < optionsJson.length() ; i++) {
			JSONObject option = optionsJson.getJSONObject(i);
			options[i] = new MastodonOption(option);
		}
		if (votesJson != null) {
			for (int i = 0; i < optionsJson.length() ; i++) {
				int index = votesJson.getInt(i);
				if (index >= 0 && index < options.length) {
					options[index].setSelected();
				}
			}
		}
		try {
			id = Long.parseLong(idStr);
		} catch (NumberFormatException e) {
			throw new JSONException("Bad ID: " + idStr);
		}
	}


	@Override
	public long getId() {
		return id;
	}


	@Override
	public boolean voted() {
		return voted;
	}


	@Override
	public boolean closed() {
		return expired;
	}


	@Override
	public long expirationTime() {
		return exTime;
	}


	@Override
	public int voteCount() {
		return voteCount;
	}


	@Override
	public Option[] getOptions() {
		return options;
	}


	private static class MastodonOption implements Option {

		private String title;
		private int voteCount;
		private boolean selected = false;

		MastodonOption(JSONObject json) throws JSONException {
			voteCount = json.getInt("votes_count");
			title = json.getString("title");
		}


		@Override
		public String getTitle() {
			return title;
		}


		@Override
		public int getVotes() {
			return voteCount;
		}


		@Override
		public boolean selected() {
			return selected;
		}

		/**
		 * mark this option as selected
		 */
		void setSelected() {
			selected = true;
		}
	}
}