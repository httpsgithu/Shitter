package org.nuclearfog.twidda.backend.api.update;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class is used to upload a directmessage
 *
 * @author nuclearfog
 */
public class DirectmessageUpdate {

	private Uri uri;
	private MediaUpdate mediaUpdate;
	private String name = "";
	private String text = "";

	/**
	 * @param name screen name of the user
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param text message text
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * get name of the receiver
	 *
	 * @return screen name
	 */
	public String getName() {
		return name;
	}

	/**
	 * get message text
	 *
	 * @return message text
	 */
	public String getText() {
		return text;
	}


	/**
	 * get inputstream of the media file
	 *
	 * @return input stream
	 */
	@Nullable
	public MediaUpdate getMediaUpdate() {
		return mediaUpdate;
	}

	/**
	 *
	 */
	@Nullable
	public Uri getMediaUri() {
		return uri;
	}

	/**
	 * add media uri and create input stream
	 *
	 * @param context context used to create inputstream and mime type
	 * @param uri     uri of a local media file
	 * @return true if file is valid
	 */
	public boolean addMedia(Context context, @NonNull Uri uri) {
		// check if file is valid
		DocumentFile file = DocumentFile.fromSingleUri(context, uri);
		if (file != null && file.length() > 0) {
			this.uri = uri;
			return true;
		}
		return false;
	}

	/**
	 * initialize inputstream of the file to upload
	 *
	 * @return true if initialization succeded
	 */
	public boolean prepare(ContentResolver resolver) {
		if (uri == null)
			return true;
		try {
			String mimeType = resolver.getType(uri);
			InputStream fileStream = resolver.openInputStream(uri);
			if (fileStream != null && mimeType != null && fileStream.available() > 0) {
				mediaUpdate = new MediaUpdate(fileStream, mimeType);
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * close inputstream of media file
	 */
	public void close() {
		if (mediaUpdate != null) {
			mediaUpdate.close();
		}
	}

	@NonNull
	@Override
	public String toString() {
		return "to:\"" + name + "\" text:\"" + text + "\" media:" + (mediaUpdate != null);
	}
}