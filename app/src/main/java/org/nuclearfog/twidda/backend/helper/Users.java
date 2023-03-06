package org.nuclearfog.twidda.backend.helper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.nuclearfog.twidda.model.User;

import java.util.LinkedList;

/**
 * custom twitter user list containing cursor information
 *
 * @author nuclearfog
 */
public class Users extends LinkedList<User> {

	public static final long serialVersionUID = -1108521824070076679L;

	private long prevCursor;
	private long nextCursor;

	/**
	 * creates an empty list with defined cursors
	 *
	 * @param prevCursor previous cursor of the list
	 * @param nextCursor next cursor of the list
	 */
	public Users(long prevCursor, long nextCursor) {
		super();
		this.prevCursor = prevCursor;
		this.nextCursor = nextCursor;
	}

	@Nullable
	@Override
	public User get(int index) {
		return super.get(index);
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
	 * set previous cursor
	 *
	 * @param prevCursor cursor value
	 */
	public void setPrevCursor(long prevCursor) {
		this.prevCursor = prevCursor;
	}

	/**
	 * set next cursor
	 *
	 * @param nextCursor cursor value
	 */
	public void setNextCursor(long nextCursor) {
		this.nextCursor = nextCursor;
	}

	/**
	 * replace whole list including cursors
	 *
	 * @param list new list
	 */
	public void replaceAll(Users list) {
		super.clear();
		super.addAll(list);
		prevCursor = list.prevCursor;
		nextCursor = list.nextCursor;
	}

	/**
	 * add a sublist at the bottom of this list including next cursor
	 *
	 * @param list  new sublist
	 * @param index index of the sub list
	 */
	public boolean addAll(int index, Users list) {
		if (isEmpty()) {
			prevCursor = list.prevCursor;
			nextCursor = list.nextCursor;
		} else if (index == 0) {
			prevCursor = list.prevCursor;
		} else if (index == size() - 1) {
			nextCursor = list.nextCursor;
		}
		return super.addAll(index, list);
	}


	@Override
	@NonNull
	public String toString() {
		return "size=" + size() + " previous=" + prevCursor + " next=" + nextCursor;
	}
}