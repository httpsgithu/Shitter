package org.nuclearfog.twidda.backend.async;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.nuclearfog.twidda.backend.api.Connection;
import org.nuclearfog.twidda.backend.api.ConnectionException;
import org.nuclearfog.twidda.backend.api.ConnectionManager;
import org.nuclearfog.twidda.model.Translation;

/**
 * Status translation loader
 *
 * @author nuclearfog
 */
public class TranslationLoader extends AsyncExecutor<Long, TranslationLoader.TranslationResult> {

	private Connection connection;

	/**
	 *
	 */
	public TranslationLoader(Context context) {
		connection = ConnectionManager.getDefaultConnection(context);
	}


	@Override
	protected TranslationResult doInBackground(@NonNull Long param) {
		try {
			return new TranslationResult(connection.getStatusTranslation(param));
		} catch (ConnectionException exception) {
			return new TranslationResult(null);
		}
	}

	/**
	 *
	 */
	public static class TranslationResult {

		@Nullable
		public Translation translation;

		TranslationResult(@Nullable Translation translation) {
			this.translation = translation;
		}
	}
}