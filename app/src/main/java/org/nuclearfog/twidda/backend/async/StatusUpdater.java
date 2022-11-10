package org.nuclearfog.twidda.backend.async;

import android.os.AsyncTask;

import org.nuclearfog.twidda.backend.api.Connection;
import org.nuclearfog.twidda.backend.api.ConnectionException;
import org.nuclearfog.twidda.backend.api.ConnectionManager;
import org.nuclearfog.twidda.backend.update.MediaUpdate;
import org.nuclearfog.twidda.backend.update.StatusUpdate;
import org.nuclearfog.twidda.ui.activities.StatusEditor;

import java.lang.ref.WeakReference;

/**
 * Background task for posting a status
 *
 * @author nuclearfog
 * @see StatusEditor
 */
public class StatusUpdater extends AsyncTask<StatusUpdate, Void, Void> {

	private Connection connection;
	private ConnectionException exception;
	private WeakReference<StatusEditor> weakRef;

	/**
	 * initialize task
	 *
	 * @param activity Activity context
	 */
	public StatusUpdater(StatusEditor activity) {
		super();
		connection = ConnectionManager.get(activity);
		weakRef = new WeakReference<>(activity);
	}


	@Override
	protected Void doInBackground(StatusUpdate... statusUpdates) {
		StatusUpdate statusUpdate = statusUpdates[0];
		try {
			// upload media first
			MediaUpdate[] mediaUpdates = statusUpdate.getMediaUpdates();
			long[] mediaIds = new long[mediaUpdates.length];
			for (int pos = 0; pos < mediaUpdates.length; pos++) {
				// upload media file and save media ID
				mediaIds[pos] = connection.uploadMedia(mediaUpdates[pos]);
			}
			// upload status
			if (!isCancelled()) {
				connection.uploadStatus(statusUpdate, mediaIds);
			}
		} catch (ConnectionException exception) {
			this.exception = exception;
		} finally {
			// close inputstreams
			statusUpdate.close();
		}
		return null;
	}


	@Override
	protected void onPostExecute(Void v) {
		StatusEditor activity = weakRef.get();
		if (activity != null) {
			if (exception == null) {
				activity.onSuccess();
			} else {
				activity.onError(exception);
			}
		}
	}
}