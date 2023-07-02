package org.nuclearfog.twidda.ui.dialogs;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.nuclearfog.twidda.R;
import org.nuclearfog.twidda.backend.async.AsyncExecutor.AsyncCallback;
import org.nuclearfog.twidda.backend.async.EmojiLoader;
import org.nuclearfog.twidda.config.GlobalSettings;
import org.nuclearfog.twidda.model.Emoji;
import org.nuclearfog.twidda.ui.adapter.EmojiAdapter;
import org.nuclearfog.twidda.ui.adapter.EmojiAdapter.OnEmojiClickListener;

import java.util.List;

/**
 * Expandable emoji picker dialog
 *
 * @author nuclearfog
 */
public class EmojiPicker extends BottomSheetDialog implements AsyncCallback<List<Emoji>>, OnEmojiClickListener {

	private OnEmojiSelectListener listener;
	private EmojiAdapter adapter;
	private EmojiLoader emojiLoader;

	/**
	 * @param activity activity used to show emoji picker
	 * @param listener emoji add listener
	 */
	public EmojiPicker(Activity activity, OnEmojiSelectListener listener) {
		super(activity, R.style.EmojiPickerDialog);
		emojiLoader = new EmojiLoader(activity.getApplicationContext());
		adapter = new EmojiAdapter(this);
		this.listener = listener;
	}


	@Override
	@SuppressWarnings("ConstantConditions")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_emoji_picker);
		ViewGroup root = findViewById(R.id.dialog_emoji_root);
		RecyclerView listView = findViewById(R.id.dialog_emoji_list);
		ImageView background = findViewById(R.id.dialog_emoji_background);

		// set round corner background annd color
		getWindow().setBackgroundDrawable(new ColorDrawable(0));
		GlobalSettings settings = GlobalSettings.get(getContext());
		background.getBackground().setColorFilter(settings.getBackgroundColor(), PorterDuff.Mode.SRC_IN);
		// set height
		int height = Resources.getSystem().getDisplayMetrics().heightPixels / 4;
		BottomSheetBehavior<View> mBehavior = BottomSheetBehavior.from((View) root.getParent());
		mBehavior.setPeekHeight(height);

		listView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
		listView.setAdapter(adapter);

		emojiLoader.execute(null, this);
	}


	@Override
	public void onResult(@NonNull List<Emoji> emojis) {
		adapter.replaceItems(emojis);
	}


	@Override
	public void show() {
		if (!isShowing()) {
			super.show();
		}
	}


	@Override
	public void dismiss() {
		if (isShowing()) {
			super.dismiss();
		}
	}


	@Override
	public void onEmojiClick(Emoji emoji) {
		listener.onEmojiSelected(emoji);
	}

	/**
	 * Listener used to send selected emoji back to activity
	 */
	public interface OnEmojiSelectListener {

		/**
		 * called when an emoji was selected
		 *
		 * @param emoji selected emoji
		 */
		void onEmojiSelected(Emoji emoji);
	}
}