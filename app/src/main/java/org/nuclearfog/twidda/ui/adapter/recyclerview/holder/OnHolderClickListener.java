package org.nuclearfog.twidda.ui.adapter.recyclerview.holder;

/**
 * Click listener for adapter view holder
 *
 * @author nuclearfog
 */
public interface OnHolderClickListener {

	int NO_TYPE = -1;

	int LIST_CLICK = 1;

	int USER_CLICK = 2;

	int USER_REMOVE = 3;

	int STATUS_CLICK = 4;

	int STATUS_LABEL = 5;

	int ACCOUNT_SELECT = 12;

	int ACCOUNT_REMOVE = 13;

	int PREVIEW_CLICK = 14;

	int CARD_IMAGE = 15;

	int CARD_LINK = 16;

	int POLL_OPTION = 17;

	int POLL_VOTE = 18;

	int NOTIFICATION_DISMISS = 19;

	int EMOJI_CLICK = 20;

	int DOMAIN_REMOVE = 21;

	int FILTER_CLICK = 22;

	int FILTER_REMOVE = 23;

	int HASHTAG_CLICK = 24;

	int HASHTAG_REMOVE = 25;

	int SCHEDULE_CLICK = 26;

	int SCHEDULE_REMOVE = 27;

	/**
	 * called when an item was clicked
	 *
	 * @param position adapter position of the item
	 * @param type     type of click
	 * @param extras   extra information
	 */
	void onItemClick(int position, int type, int... extras);

	/**
	 * called when a placeholder item was clicked
	 *
	 * @param index index of the item
	 * @return true to enable loading animation
	 */
	boolean onPlaceholderClick(int index);
}