package org.nuclearfog.twidda.ui.activities;

import static android.os.AsyncTask.Status.RUNNING;
import static android.view.View.GONE;
import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.nuclearfog.twidda.R;
import org.nuclearfog.twidda.backend.api.update.DirectmessageUpdate;
import org.nuclearfog.twidda.backend.async.MessageUpdater;
import org.nuclearfog.twidda.backend.utils.AppStyles;
import org.nuclearfog.twidda.backend.utils.ErrorHandler;
import org.nuclearfog.twidda.ui.dialogs.ConfirmDialog;
import org.nuclearfog.twidda.ui.dialogs.ConfirmDialog.OnConfirmListener;
import org.nuclearfog.twidda.ui.dialogs.ProgressDialog;
import org.nuclearfog.twidda.ui.dialogs.ProgressDialog.OnProgressStopListener;

/**
 * Directmessage editor activity
 *
 * @author nuclearfog
 */
public class MessageEditor extends MediaActivity implements OnClickListener, OnConfirmListener, OnProgressStopListener {

	/**
	 * key for the screenname if any
	 * value type is String
	 */
	public static final String KEY_DM_PREFIX = "dm_prefix";

	private MessageUpdater messageAsync;

	private ProgressDialog loadingCircle;
	private ConfirmDialog confirmDialog;

	private EditText receiver, message;
	private ImageButton media, preview;

	private DirectmessageUpdate holder = new DirectmessageUpdate();

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(AppStyles.setFontScale(newBase));
	}


	@Override
	protected void onCreate(@Nullable Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.popup_dm);
		ViewGroup root = findViewById(R.id.dm_popup);
		ImageButton send = findViewById(R.id.dm_send);
		ImageView background = findViewById(R.id.dm_background);
		media = findViewById(R.id.dm_media);
		preview = findViewById(R.id.dm_preview);
		receiver = findViewById(R.id.dm_receiver);
		message = findViewById(R.id.dm_text);
		AppStyles.setEditorTheme(root, background);

		loadingCircle = new ProgressDialog(this);
		confirmDialog = new ConfirmDialog(this);

		String prefix = getIntent().getStringExtra(KEY_DM_PREFIX);
		if (prefix != null) {
			receiver.append(prefix);
		}

		send.setOnClickListener(this);
		media.setOnClickListener(this);
		preview.setOnClickListener(this);
		loadingCircle.addOnProgressStopListener(this);
		confirmDialog.setConfirmListener(this);
	}


	@Override
	public void onBackPressed() {
		if (receiver.getText().length() == 0 && message.getText().length() == 0 && holder.getMediaUri() == null) {
			super.onBackPressed();
		} else {
			confirmDialog.show(ConfirmDialog.MESSAGE_EDITOR_LEAVE);
		}
	}


	@Override
	protected void onDestroy() {
		if (messageAsync != null && messageAsync.getStatus() == RUNNING)
			messageAsync.cancel(true);
		loadingCircle.dismiss();
		if (holder != null) {
			holder.close();
		}
		super.onDestroy();
	}


	@Override
	protected void onAttachLocation(@Nullable Location location) {
	}


	@Override
	protected void onMediaFetched(int resultType, @NonNull Uri uri) {
		if (resultType == REQUEST_IMAGE) {
			if (holder.addMedia(this, uri)) {
				preview.setVisibility(VISIBLE);
				media.setVisibility(GONE);
			} else {
				Toast.makeText(this, R.string.error_adding_media, LENGTH_SHORT).show();
			}
		}
	}


	@Override
	public void onClick(View v) {
		// send direct message
		if (v.getId() == R.id.dm_send) {
			if (messageAsync == null || messageAsync.getStatus() != RUNNING) {
				sendMessage();
			}
		}
		// get media
		else if (v.getId() == R.id.dm_media) {
			getMedia(REQUEST_IMAGE);
		}
		// open media
		else if (v.getId() == R.id.dm_preview) {
			if (holder.getMediaUri() != null) {
				Intent image = new Intent(this, ImageViewer.class);
				image.putExtra(ImageViewer.IMAGE_URIS, new Uri[]{holder.getMediaUri()});
				image.putExtra(ImageViewer.IMAGE_DOWNLOAD, false);
				startActivity(image);
			}
		}
	}


	@Override
	public void stopProgress() {
		if (messageAsync != null && messageAsync.getStatus() == RUNNING) {
			messageAsync.cancel(true);
		}
	}


	@Override
	public void onConfirm(int type, boolean rememberChoice) {
		// retry sending message
		if (type == ConfirmDialog.MESSAGE_EDITOR_ERROR) {
			sendMessage();
		}
		// leave message editor
		else if (type == ConfirmDialog.MESSAGE_EDITOR_LEAVE) {
			finish();
		}
	}

	/**
	 * called when direct message is sent
	 */
	public void onSuccess() {
		Toast.makeText(this, R.string.info_dm_send, Toast.LENGTH_SHORT).show();
		finish();
	}

	/**
	 * called when an error occurs
	 *
	 * @param error Engine Exception
	 */
	public void onError(@Nullable ErrorHandler.TwitterError error) {
		String message = ErrorHandler.getErrorMessage(this, error);
		confirmDialog.show(ConfirmDialog.MESSAGE_EDITOR_ERROR, message);
		loadingCircle.dismiss();
	}

	/**
	 * check inputs and send message
	 */
	private void sendMessage() {
		String username = receiver.getText().toString();
		String message = this.message.getText().toString();
		if (!username.trim().isEmpty() && (!message.trim().isEmpty() || holder.getMediaUri() != null)) {
			if (holder.prepare(getContentResolver())) {
				holder.setName(username);
				holder.setText(message);
				messageAsync = new MessageUpdater(this, holder);
				messageAsync.execute();
				loadingCircle.show();
			} else {
				Toast.makeText(this, R.string.error_media_init, LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(this, R.string.error_dm, LENGTH_SHORT).show();
		}
	}
}