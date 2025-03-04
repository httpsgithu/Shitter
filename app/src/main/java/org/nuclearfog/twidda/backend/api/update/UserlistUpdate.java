package org.nuclearfog.twidda.backend.api.update;

import androidx.annotation.NonNull;

/**
 * This class is used to upload list information
 *
 * @author nuclearfog
 */
public class UserlistUpdate {

	/**
	 * this ID indicates that the list isn't created yet
	 */
	public static final long NEW_LIST = -1;

	private long listId;
	private String title;
	private String description;
	private boolean isPublic;


	/**
	 * @param title       Title of the list
	 * @param description short description of the list
	 * @param isPublic    true if list should be public
	 * @param listId      ID of the list to update or {@link #NEW_LIST} to create a new list
	 */
	public UserlistUpdate(String title, String description, boolean isPublic, long listId) {
		this.title = title;
		this.description = description;
		this.isPublic = isPublic;
		this.listId = listId;
	}

	/**
	 * get ID of the list
	 *
	 * @return list ID
	 */
	public long getId() {
		return listId;
	}

	/**
	 * get Title of the list
	 *
	 * @return Title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * get short description of the list
	 *
	 * @return description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * check if list is public
	 *
	 * @return true if list is public
	 */
	public boolean isPublic() {
		return isPublic;
	}

	/**
	 * check if list exists, so only the information will be updated
	 *
	 * @return true if list exists
	 */
	public boolean exists() {
		return listId != NEW_LIST;
	}

	@NonNull
	@Override
	public String toString() {
		return "id:" + listId + " title:\"" + title + "\"";
	}
}