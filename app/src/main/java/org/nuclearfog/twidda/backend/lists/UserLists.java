package org.nuclearfog.twidda.backend.lists;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.nuclearfog.twidda.model.UserList;

import java.util.LinkedList;

/**
 * Container class for user lists containing cursor information
 *
 * @author nuclearfog
 */
public class UserLists extends LinkedList<UserList> {

	public static final long serialVersionUID = -5947008315897774115L;

	private long prevCursor, nextCursor;

	/**
	 * @param prevCursor previous list cursor or 0 if list starts
	 * @param nextCursor next cursor or 0 if list ends
	 */
	public UserLists(long prevCursor, long nextCursor) {
		super();
		this.prevCursor = prevCursor;
		this.nextCursor = nextCursor;
	}


	@Nullable
	@Override
	public UserList get(int index) {
		return super.get(index);
	}

	/**
	 * check if list is linked to a previous list
	 *
	 * @return true if list is linked
	 */
	public boolean hasPrevious() {
		return prevCursor != 0;
	}

	/**
	 * check if list has a successor
	 *
	 * @return true if list has a successor
	 */
	public boolean hasNext() {
		return nextCursor != 0;
	}

	/**
	 * get next link to a list
	 *
	 * @return cursor
	 */
	public long getNext() {
		return nextCursor;
	}

	/**
	 * replace whole list including cursors
	 *
	 * @param list new list
	 */
	public void replace(UserLists list) {
		super.clear();
		super.addAll(list);
		prevCursor = list.prevCursor;
		nextCursor = list.nextCursor;
	}

	/**
	 * remove an item from list
	 *
	 * @param id ID of the item
	 * @return index of the removed item
	 */
	public int removeItem(long id) {
		for (int index = 0; index < size(); index++) {
			UserList item = get(index);
			if (item != null && item.getId() == id) {
				remove(index);
				return index;
			}
		}
		return -1;
	}

	/**
	 * add a sublist at the bottom of this list including next cursor
	 *
	 * @param list  new sublist
	 * @param index Index of the sub list
	 */
	public void addAt(UserLists list, int index) {
		super.addAll(index, list);
		nextCursor = list.nextCursor;
	}

	@Override
	@NonNull
	public String toString() {
		return "size=" + size() + " pre=" + prevCursor + " pos=" + nextCursor;
	}
}