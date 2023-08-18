package org.nuclearfog.twidda.backend.helper.update;

import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * This class is used to upload list information
 *
 * @author nuclearfog
 */
public class UserListUpdate implements Serializable {

	private static final long serialVersionUID = -366691257985800712L;

	private long listId = 0L;
	private String title = "";
	private String description = "";


	/**
	 * set ID of an existing list to update
	 *
	 * @param listId ID of an existing list
	 */
	public void setId(long listId) {
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
	 * set list title
	 *
	 * @param title title text
	 */
	public void setTitle(String title) {
		this.title = title;
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
	 * set list description
	 *
	 * @param description text description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * get short description of the list
	 *
	 * @return description
	 */
	public String getDescription() {
		return description;
	}


	@NonNull
	@Override
	public String toString() {
		if (getId() != 0L)
			return "id=" + getId() + " title=\"" + getTitle() + "\" description=\"" + getDescription() + "\"";
		return "title=\"" + getTitle() + "\" description=\"" + getDescription() + "\"";
	}
}