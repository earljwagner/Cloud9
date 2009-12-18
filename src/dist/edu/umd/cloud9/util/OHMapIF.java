/*
 * Cloud9: A MapReduce Library for Hadoop
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package edu.umd.cloud9.util;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Subclass of <code>HMapIF</code> that provides access to entries sorted by
 * value and other convenience methods.
 */
public class OHMapIF extends HMapIF {

	private static final long serialVersionUID = 823615346L;

	/**
	 * Adds values of keys from another map to this map.
	 * 
	 * @param m
	 *            the other map
	 */
	public void plus(HMapIF m) {
		for (MapIF.Entry e : m.entrySet()) {
			int key = e.getKey();

			if (this.containsKey(key)) {
				this.put(key, this.get(key) + e.getValue());
			} else {
				this.put(key, e.getValue());
			}
		}
	}

	/**
	 * Computes the dot product of this map with another map.
	 * 
	 * @param m
	 *            the other map
	 */
	public float dot(HMapIF m) {
		float s = 0.0f;

		for (MapIF.Entry e : m.entrySet()) {
			int key = e.getKey();

			if (this.containsKey(key)) {
				s += this.get(key) * e.getValue();
			}
		}

		return s;
	}

	/**
	 * Returns the length of the vector represented by this map.
	 * 
	 * @return length of the vector represented by this map
	 */
	public float length() {
		float s = 0.0f;

		for (MapIF.Entry e : this.entrySet()) {
			s += e.getValue() * e.getValue();
		}

		return (float) Math.sqrt(s);
	}

	/**
	 * Normalizes values such that the vector represented by this map has
	 * unit length.
	 */
	public void normalize() {
		float l = this.length();

		for (int f : this.keySet()) {
			this.put(f, this.get(f) / l);
		}

	}

	/**
	 * Returns entries sorted by descending value. Ties broken by the key.
	 * 
	 * @return entries sorted by descending value
	 */
	public Entry[] getEntriesSortedByValue() {
		if (this.size() == 0)
			return null;

		// for storing the entries
		Entry[] entries = new Entry[this.size()];
		int i = 0;
		Entry next = null;

		int index = 0;
		// advance to first entry
		while (index < table.length && (next = table[index++]) == null)
			;

		while (next != null) {
			// current entry
			Entry e = next;

			// advance to next entry
			next = e.next;
			if ((next = e.next) == null) {
				while (index < table.length && (next = table[index++]) == null)
					;
			}

			// add entry to array
			entries[i++] = e;
		}

		// sort the entries
		Arrays.sort(entries, new Comparator<Entry>() {
			@SuppressWarnings("unchecked")
			public int compare(Entry e1, Entry e2) {
				if (e1.getValue() > e2.getValue()) {
					return -1;
				} else if (e1.getValue() < e2.getValue()) {
					return 1;
				}

				if (e1.getKey() == e2.getKey())
					return 0;

				return e1.getKey() > e2.getKey() ? 1 : -1;
			}
		});

		return entries;
	}

	/**
	 * Returns top <i>n</i> entries sorted by descending value. Ties broken by
	 * the key.
	 * 
	 * @param n
	 *            number of entries to return
	 * @return top <i>n</i> entries sorted by descending value
	 */
	public Entry[] getEntriesSortedByValue(int n) {
		Entry[] entries = getEntriesSortedByValue();

		if (entries == null)
			return null;

		if (entries.length < n)
			return entries;

		return Arrays.copyOfRange(entries, 0, n);
	}

}
