package org.nuclearfog.twidda.model.lists;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.nuclearfog.twidda.model.ScheduledStatus;

import java.util.LinkedList;

/**
 * @author nuclearfog
 */
public class ScheduledStatuses extends LinkedList<ScheduledStatus> {

	private static final long serialVersionUID = 9015646013535818699L;


	public ScheduledStatuses() {
	}


	public ScheduledStatuses(ScheduledStatuses scheduledStatuses) {
		addAll(scheduledStatuses);
	}


	@Override
	@Nullable
	public ScheduledStatus get(int index) {
		return super.get(index);
	}


	@NonNull
	@Override
	public String toString() {
		int itemCount = 0;
		for (ScheduledStatus item : this) {
			if (item != null) {
				itemCount++;
			}
		}
		return "item_count=" + itemCount;
	}
}