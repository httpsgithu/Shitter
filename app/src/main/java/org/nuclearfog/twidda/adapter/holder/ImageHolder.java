package org.nuclearfog.twidda.adapter.holder;

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import org.nuclearfog.twidda.R;
import org.nuclearfog.twidda.backend.utils.AppStyles;
import org.nuclearfog.twidda.database.GlobalSettings;

/**
 * Holder for image item
 *
 * @author nuclearfog
 * @see org.nuclearfog.twidda.adapter.ImageAdapter
 */
public class ImageHolder extends ViewHolder implements OnClickListener {

	private ImageView preview;
	private ImageButton saveButton;

	private OnHolderClickListener listener;

	/**
	 * @param parent Parent view from adapter
	 */
	public ImageHolder(ViewGroup parent, GlobalSettings settings) {
		super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false));
		// get views
		CardView cardBackground = (CardView) itemView;
		preview = itemView.findViewById(R.id.item_image_preview);
		saveButton = itemView.findViewById(R.id.item_image_save);
		// theme views
		cardBackground.setCardBackgroundColor(settings.getCardColor());
		AppStyles.setButtonColor(saveButton, settings.getFontColor());
		AppStyles.setDrawableColor(saveButton, settings.getIconColor());

		preview.setOnClickListener(this);
		saveButton.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		int pos = getLayoutPosition();
		if (pos != NO_POSITION && listener != null) {
			if (v == preview) {
				listener.onItemClick(pos, OnHolderClickListener.IMAGE_CLICK);
			} else if (v == saveButton) {
				listener.onItemClick(pos, OnHolderClickListener.IMAGE_SAVE);
			}
		}
	}

	/**
	 * set item click listener
	 */
	public void setOnImageClickListener(OnHolderClickListener listener) {
		this.listener = listener;
	}

	/**
	 * enable save button
	 */
	public void enableSaveButton() {
		saveButton.setVisibility(View.VISIBLE);
	}

	/**
	 * set image link
	 */
	public void setImageUri(Uri uri) {
		preview.setImageURI(uri);
	}
}