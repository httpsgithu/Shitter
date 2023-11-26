package org.nuclearfog.twidda.ui.adapter.recyclerview.holder;

import android.graphics.Color;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import org.nuclearfog.twidda.R;
import org.nuclearfog.twidda.backend.async.AsyncExecutor;
import org.nuclearfog.twidda.backend.async.TextEmojiLoader;
import org.nuclearfog.twidda.backend.utils.AppStyles;
import org.nuclearfog.twidda.backend.utils.EmojiUtils;
import org.nuclearfog.twidda.backend.utils.StringUtils;
import org.nuclearfog.twidda.backend.utils.Tagger;
import org.nuclearfog.twidda.config.GlobalSettings;
import org.nuclearfog.twidda.model.Announcement;

/**
 * Viewholder for {@link org.nuclearfog.twidda.ui.adapter.recyclerview.AnnouncementAdapter}
 *
 * @author nuclearfog
 */
public class AnnouncementHolder extends ViewHolder implements OnClickListener {

	private TextView time, content;

	private OnHolderClickListener listener;
	private GlobalSettings settings;
	private TextEmojiLoader emojiLoader;

	private AsyncExecutor.AsyncCallback<TextEmojiLoader.Result> textResult = this::setTextEmojis;
	private int iconSize;

	private long tagId = 0L;

	/**
	 *
	 */
	public AnnouncementHolder(ViewGroup parent, OnHolderClickListener listener) {
		super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_announcement, parent, false));
		settings = GlobalSettings.get(parent.getContext());
		emojiLoader = new TextEmojiLoader(parent.getContext());
		CardView card = (CardView) itemView;
		ViewGroup container = itemView.findViewById(R.id.item_announcement_container);
		time = itemView.findViewById(R.id.item_announcement_timestamp);
		content = itemView.findViewById(R.id.item_announcement_content);
		iconSize = parent.getResources().getDimensionPixelSize(R.dimen.item_announcement_icon_size);

		card.setCardBackgroundColor(settings.getCardColor());
		AppStyles.setTheme(container, Color.TRANSPARENT);

		container.setOnClickListener(this);
		this.listener = listener;
	}


	@Override
	public void onClick(View v) {
		int position = getLayoutPosition();
		if (position != RecyclerView.NO_POSITION) {
			if (v.getId() == R.id.item_announcement_container) {
				listener.onItemClick(position, OnHolderClickListener.ANNOUNCEMENT_CLICK);
			}
		}
	}

	/**
	 *
	 */
	public void setContent(Announcement announcement) {
		Spannable textSpan = Tagger.makeTextWithLinks(announcement.getMessage(), settings.getHighlightColor());
		if (announcement.getEmojis().length == 0 && settings.imagesEnabled()) {
			TextEmojiLoader.Param param = new TextEmojiLoader.Param(tagId, announcement.getEmojis(), textSpan, iconSize);
			emojiLoader.execute(param, textResult);
			textSpan = EmojiUtils.removeTags(textSpan);
		}
		content.setText(textSpan);
		time.setText(StringUtils.formatCreationTime(time.getResources(), announcement.getTimestamp()));
	}

	/**
	 *
	 */
	private void setTextEmojis(@NonNull TextEmojiLoader.Result result) {
		if (result.id == tagId && result.images != null) {
			Spannable spannable = EmojiUtils.addEmojis(content.getContext(), result.spannable, result.images);
			content.setText(spannable);
		}
	}
}