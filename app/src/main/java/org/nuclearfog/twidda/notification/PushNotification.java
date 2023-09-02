package org.nuclearfog.twidda.notification;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.nuclearfog.twidda.BuildConfig;
import org.nuclearfog.twidda.R;
import org.nuclearfog.twidda.config.GlobalSettings;
import org.nuclearfog.twidda.model.Notification;
import org.nuclearfog.twidda.model.lists.Notifications;
import org.nuclearfog.twidda.ui.activities.MainActivity;

/**
 * This class creates app push notification
 *
 * @author nuclearfog
 */
public class PushNotification {

	public static final String NOTIFICATION_NAME = BuildConfig.APPLICATION_ID + " UnifiedPush";
	public static final String NOTIFICATION_ID_STR = BuildConfig.APPLICATION_ID + ".notification";

	/**
	 * maximum notifications to show
	 */
	private static final int NOTIFICATION_LIMIT = 3;

	private static final int NOTIFICATION_ID = 0x25281;

	private NotificationManagerCompat notificationManager;
	private NotificationCompat.Builder notificationBuilder;
	private GlobalSettings settings;
	private Context context;

	/**
	 *
	 */
	public PushNotification(Context context) {
		this.context = context;
		notificationManager = NotificationManagerCompat.from(context);
		notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_ID_STR);
		settings = GlobalSettings.get(context);
		// setup notification channel
		NotificationChannelCompat notificationChannel = new NotificationChannelCompat.Builder(NOTIFICATION_ID_STR, NotificationManagerCompat.IMPORTANCE_HIGH).setName(NOTIFICATION_NAME).build();
		notificationManager.createNotificationChannel(notificationChannel);

		// Open MainActivity and select notification tab, if notification view is clicked
		Intent notificationIntent = new Intent(context.getApplicationContext(), MainActivity.class);
		notificationIntent.putExtra(MainActivity.KEY_SELECT_PAGE, 3); // select notification tab
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent resultIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_MUTABLE);
		notificationBuilder.setContentIntent(resultIntent).setPriority(NotificationCompat.PRIORITY_HIGH)
				.setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE).setOnlyAlertOnce(true)
				.setAutoCancel(true).setDefaults(NotificationCompat.DEFAULT_ALL).setStyle(new NotificationCompat.InboxStyle());
	}

	/**
	 * create push-notification from notifications
	 *
	 * @param notifications new notifications
	 */
	public void createNotification(Notifications notifications) {
		if (!notifications.isEmpty()) {
			String title = settings.getLogin().getConfiguration().getName();
			String content;
			int icon;
			if (notifications.size() == 1) {
				Notification notification = notifications.getFirst();
				switch (notification.getType()) {
					case Notification.TYPE_FAVORITE:
						icon = R.drawable.favorite;
						content = context.getString(R.string.notification_favorite, notification.getUser().getScreenname());
						break;

					case Notification.TYPE_REPOST:
						icon = R.drawable.repost;
						content = context.getString(R.string.notification_repost, notification.getUser().getScreenname());
						break;

					case Notification.TYPE_FOLLOW:
						icon = R.drawable.follower;
						content = context.getString(R.string.notification_follow, notification.getUser().getScreenname());
						break;

					case Notification.TYPE_REQUEST:
						icon = R.drawable.follower_request;
						content = context.getString(R.string.notification_request, notification.getUser().getScreenname());
						break;

					case Notification.TYPE_MENTION:
						icon = R.drawable.mention;
						content = context.getString(R.string.notification_mention, notification.getUser().getScreenname());
						break;

					case Notification.TYPE_STATUS:
						icon = R.drawable.post;
						content = context.getString(R.string.notification_status, notification.getUser().getScreenname());
						break;

					case Notification.TYPE_UPDATE:
						icon = R.drawable.post;
						content = context.getString(R.string.notification_edit);
						break;

					case Notification.TYPE_POLL:
						icon = R.drawable.poll;
						content = context.getString(R.string.notification_poll);
						break;

					default:
						icon = R.drawable.bell;
						content = context.getString(R.string.notification_new);
						break;
				}
			} else if (notifications.size() <= NOTIFICATION_LIMIT) {
				content = context.getString(R.string.notification_new);
				icon = R.drawable.bell;
			} else {
				// todo check if notification exists
				return;
			}
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || context.checkSelfPermission(POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
				notificationBuilder.setContentTitle(title).setContentText(content).setSmallIcon(icon);
				notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
			}
		}
	}
}