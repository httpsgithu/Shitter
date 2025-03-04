package org.nuclearfog.twidda.adapter.holder;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import org.nuclearfog.twidda.R;
import org.nuclearfog.twidda.backend.utils.AppStyles;
import org.nuclearfog.twidda.database.GlobalSettings;

/**
 * Holder class for the tweet item view
 *
 * @author nuclearfog
 * @see org.nuclearfog.twidda.adapter.TweetAdapter
 */
public class TweetHolder extends ViewHolder {

	public final ImageView profile, rtUser, verifiedIcon, lockedIcon, rtIcon, favIcon, media, location, replyIcon;
	public final TextView username, screenname, tweettext, retweet, favorite, retweeter, created, replyname;

	/**
	 * @param parent   Parent view from adapter
	 * @param settings app settings to set theme
	 */
	public TweetHolder(ViewGroup parent, GlobalSettings settings) {
		super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tweet, parent, false));
		CardView cardLayout = (CardView) itemView;
		ViewGroup container = itemView.findViewById(R.id.item_tweet_container);
		profile = itemView.findViewById(R.id.item_tweet_profile_image);
		verifiedIcon = itemView.findViewById(R.id.item_tweet_verified_icon);
		lockedIcon = itemView.findViewById(R.id.item_tweet_locked_icon);
		rtUser = itemView.findViewById(R.id.item_tweet_retweeter_icon);
		rtIcon = itemView.findViewById(R.id.item_tweet_retweet_icon);
		favIcon = itemView.findViewById(R.id.item_tweet_favorite_icon);
		media = itemView.findViewById(R.id.item_tweet_media);
		location = itemView.findViewById(R.id.item_tweet_location);
		replyIcon = itemView.findViewById(R.id.item_tweet_reply);
		username = itemView.findViewById(R.id.item_tweet_author_username);
		screenname = itemView.findViewById(R.id.item_tweet_author_screenname);
		tweettext = itemView.findViewById(R.id.item_tweet_text);
		retweet = itemView.findViewById(R.id.item_tweet_retweet_count);
		favorite = itemView.findViewById(R.id.item_tweet_favorite_count);
		retweeter = itemView.findViewById(R.id.item_tweet_retweeter_name);
		created = itemView.findViewById(R.id.item_tweet_created_at);
		replyname = itemView.findViewById(R.id.item_tweet_reply_name);

		if (settings.likeEnabled()) {
			favIcon.setImageResource(R.drawable.like);
		} else {
			favIcon.setImageResource(R.drawable.favorite);
		}
		AppStyles.setTheme(container, 0);
		cardLayout.setCardBackgroundColor(settings.getCardColor());
	}
}