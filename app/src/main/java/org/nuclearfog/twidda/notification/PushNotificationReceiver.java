package org.nuclearfog.twidda.notification;

import android.content.Context;

import androidx.annotation.NonNull;

import org.nuclearfog.twidda.backend.async.AsyncExecutor.AsyncCallback;
import org.nuclearfog.twidda.backend.async.NotificationLoader;
import org.nuclearfog.twidda.backend.async.PushUpdater;
import org.nuclearfog.twidda.backend.helper.update.PushUpdate;
import org.nuclearfog.twidda.config.GlobalSettings;
import org.unifiedpush.android.connector.MessagingReceiver;

/**
 * Push notification receiver used to trigger synchronization.
 *
 * @author nuclearfog
 */
public class PushNotificationReceiver extends MessagingReceiver implements AsyncCallback<NotificationLoader.Result> {

	private PushNotification notificationManager;


	@Override
	public void onMessage(@NonNull Context context, @NonNull byte[] message, @NonNull String instance) {
		GlobalSettings settings = GlobalSettings.get(context);
		if (settings.isLoggedIn() && settings.getLogin().getConfiguration().isWebpushSupported() && settings.pushEnabled()) {
			NotificationLoader loader = new NotificationLoader(context);
			NotificationLoader.Param param = new NotificationLoader.Param(NotificationLoader.Param.LOAD_UNREAD, 0, 0L, 0L);
			notificationManager = new PushNotification(context);
			loader.execute(param, this);
		}
	}


	@Override
	public void onNewEndpoint(@NonNull Context context, @NonNull String endpoint, @NonNull String instance) {
		GlobalSettings settings = GlobalSettings.get(context);
		settings.setPushInstance(instance);
		PushUpdate update = new PushUpdate(settings.getWebPush(), endpoint);
		PushUpdater pushUpdater = new PushUpdater(context);
		pushUpdater.execute(update, null);
	}


	@Override
	public void onResult(@NonNull NotificationLoader.Result result) {
		if (result.notifications != null && !result.notifications.isEmpty()) {
			if (notificationManager != null) {
				notificationManager.createNotification(result.notifications);
			}
		}
	}
}