package org.nuclearfog.twidda.model.lists;

import androidx.annotation.NonNull;

import org.nuclearfog.twidda.model.Field;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * @author nuclearfog
 */
public class Fields extends LinkedList<Field> {

	private static final long serialVersionUID = -1117517407497703438L;


	public Fields() {
		super();
	}


	public Fields(Fields fields) {
		super(fields);
	}


	public Fields(Field[] fields) {
		super(Arrays.asList(fields));
	}


	@NonNull
	@Override
	public String toString() {
		return "item_count=" + size();
	}
}