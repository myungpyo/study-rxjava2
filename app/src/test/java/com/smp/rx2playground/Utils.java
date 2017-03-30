package com.smp.rx2playground;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by myungpyo.shim on 2017. 3. 20..
 *
 */

public class Utils {

	public static List<Character> asList(char[] source) {
		if (source == null) {
			return new ArrayList<>(0);
		}

		ArrayList<Character> result = new ArrayList<>(source.length);
		for (char character : source) {
			result.add(character);
		}
		return result;

	}

	public static String collectionToString(Collection collection) {

		StringBuilder sb = new StringBuilder();
		sb.append("[");

		Iterator iterator = collection.iterator();
		for (;iterator.hasNext();) {
			sb.append(iterator.next());
			if (iterator.hasNext()) {
				sb.append(",");
			}
		}

		sb.append("]");
		return sb.toString();
	}
}
