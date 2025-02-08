package org.nuclearfog.twidda.backend.api;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.nuclearfog.twidda.backend.api.mastodon.Mastodon;
import org.nuclearfog.twidda.config.Configuration;
import org.nuclearfog.twidda.config.GlobalSettings;
import org.nuclearfog.twidda.config.GlobalSettings.SettingsChangeObserver;

/**
 * this class manages multiple API implementations depending on settings
 *
 * @author nuclearfog
 */
public class ConnectionManager implements SettingsChangeObserver {

	private static final int IDX_MASTODON = 0;

	private static ConnectionManager instance;

	private Connection[] connections;
	private GlobalSettings settings;
	private boolean notifyChanged = false;

	/**
	 *
	 */
	private ConnectionManager(Context context) {
		connections = new Connection[1];
		connections[IDX_MASTODON] = new Mastodon(context);

		settings = GlobalSettings.get(context);
		settings.registerObserver(this);
	}


	@Override
	public void onSettingsChange() {
		notifyChanged = true;
	}

	/**
	 * creates a connection to an online service
	 *
	 * @return connection
	 */
	@NonNull
	public static Connection getDefaultConnection(Context context) {
		ConnectionManager manager = ConnectionManager.getInstance(context);
		return manager.getConnection(null);
	}

	/**
	 * @return singleton instance of this class
	 */
	@NonNull
	public static ConnectionManager getInstance(Context context) {
		if (instance == null || instance.notifyChanged) {
			instance = new ConnectionManager(context);
		}
		return instance;
	}

	/**
	 * get singleton class of a connection
	 *
	 * @param config Network selection or null to choose automatically
	 * @return singleton instance
	 */
	@NonNull
	public Connection getConnection(@Nullable Configuration config) {
		// create new singleton instance if there is none or if settings change
		if (config == null) {
			config = instance.settings.getLogin().getConfiguration();
		}
		switch (config) {
			default:
			case MASTODON:
				return instance.connections[IDX_MASTODON];
		}
	}
}