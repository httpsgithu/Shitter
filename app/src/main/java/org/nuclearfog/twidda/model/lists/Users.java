package org.nuclearfog.twidda.model.lists;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.nuclearfog.twidda.model.User;

import java.util.LinkedList;

/**
 * custom list implementaion containing users and cursor information
 *
 * @author nuclearfog
 */
public class Users extends LinkedList<User> {

	private static final long serialVersionUID = -1108521824070076679L;

	private long prevCursor, nextCursor;

	/**
	 *
	 */
	public Users() {
		this(0L, 0L);
	}

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

	/**
	 *
	 */
	public Users(Users users) {
		super.addAll(users);
		prevCursor = users.prevCursor;
		nextCursor = users.nextCursor;
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
	public long getNextCursor() {
		return nextCursor;
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
	public void addAll(int index, Users list) {
		if (isEmpty()) {
			prevCursor = list.prevCursor;
			nextCursor = list.nextCursor;
		} else if (index == 0) {
			prevCursor = list.prevCursor;
		} else if (index == size() - 1) {
			nextCursor = list.nextCursor;
		}
		super.addAll(index, list);
	}


	@Override
	@NonNull
	public String toString() {
		return "size=" + size() + " previous=" + prevCursor + " next=" + nextCursor;
	}
}