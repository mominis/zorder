/*
 * Copyright (c) 2012, MoMinis Ltd.
 * All rights reserved.
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Please contact MoMinis at opensource@mominis.com or visit www.mominis.com if
 * you need additional information or have any questions.
 */
package com.mominis.zorder;

import java.util.Iterator;

import com.mominis.zorder.except.AlreadyInCollectionException;

/**
 * The naive implementation of a {@link ZCollection}, supporting
 * bounded, natural Z-orders.
 * 
 * @author Itay Duvdevani (MoMinis Ltd.)
 *
 */
public class SimpleZCollection implements ZCollection {
	
	/* --- Fields --- */
	
	/** Max. allowed Z-order (inclusive) */
	private final int maxZ;
	
	/** Z-order collections. Every cell holds a list of objects currently in that Z-level */
	private ExposedLinkedList<ZSortable>[] buckets;
	
	/* --- Constructor --- */
	
	/**
	 * Initializes an empty collection.
	 * 
	 * @param maxZ Maximal allowed Z (inclusive).
	 */
	@SuppressWarnings("unchecked")
	public SimpleZCollection(int maxZ) {
		this.maxZ = maxZ;
		buckets = (ExposedLinkedList<ZSortable>[])new ExposedLinkedList[maxZ + 1];
		for (int i = 0 ; i <= maxZ ; ++i) {
			buckets[i] = new ExposedLinkedList<ZSortable>();
		}
	}
	
	/* --- ZCollection Methods --- */

	/**
	 * Simply appends the given object to the end of the correct bucket
	 * 
	 * @see {@link ZCollection#add(ZSortable)}
	 */
	@Override
	public void add(ZSortable object) throws AlreadyInCollectionException {
		
		if (object == null) {
			return;
		}
		
		if (object.getCurrentLink() != null) {
			throw new AlreadyInCollectionException();
		}
		
		assertZ(object.getZOrder());
		object.setCurrentLink(buckets[object.getZOrder()].append(object));
	}

	/**
	 * Simply unlink the current object's link
	 * 
	 * @see {@link ZCollection#remove(ZSortable)}
	 */
	@Override
	public void remove(ZSortable object) {
		if (object != null && object.getCurrentLink() != null) {
			object.getCurrentLink().unlink();
			object.setCurrentLink(null);
		}
	}

	/**
	 * @see {@link ZCollection#change(ZSortable)}
	 */
	@Override
	public void change(ZSortable object) {
		remove(object);
		try {
			add(object);
		} catch (AlreadyInCollectionException e) {
			// should not happen
		}
	}

	/**
	 * @see {@link ZCollection#backToFrontIterator()}
	 */
	@Override
	public Iterator<ZSortable> backToFrontIterator() {
		return new MultiBucketIterator<ZSortable>(new MyArrayIterable(), false);
	}

	/**
	 * @see {@link ZCollection#frontToBackIterator()}
	 */
	@Override
	public Iterator<ZSortable> frontToBackIterator() {
		return new MultiBucketIterator<ZSortable>(new MyArrayIterable(), true);
	}
	
	/* --- Private Mthods --- */
	
	/**
	 * Makes sure the given Z-order is valid
	 * @param zOrder Z-order to check
	 */
	private void assertZ(int zOrder) {
		if (zOrder < 0 || zOrder > maxZ) {
			throw new IllegalArgumentException(
				String.format("Invalid Z-order: %d, should be >= 0 and <= %d", zOrder, maxZ));
		}
	}
	
	/* --- Inner Classes --- */
	
	/**
	 * Auxiliary class that wraps an array of exposed lists in an iterator
	 * 
	 * @author itayd
	 *
	 */
	private class MyArrayIterable implements ReverseIterable<ExposedLinkedList<ZSortable>> {
		
		/* --- ReverseIterable Methods --- */
		
		/**
		 * @see ReverseIterable#iterator()
		 */
		@Override
		public Iterator<ExposedLinkedList<ZSortable>> iterator() {
			return new Iterator<ExposedLinkedList<ZSortable>>() {
				
				int index = 0;
				
				@Override
				public boolean hasNext() {
					return index <= maxZ;
				}

				@Override
				public ExposedLinkedList<ZSortable> next() {
					return buckets[index++];
				}

				@Override
				public void remove() {
					// not implemented
				}
			};
		}

		/**
		 * @see ReverseIterable#reverseIterator()
		 */
		@Override
		public Iterator<ExposedLinkedList<ZSortable>> reverseIterator() {
			return new Iterator<ExposedLinkedList<ZSortable>>() {
				
				int index = maxZ;
				
				@Override
				public boolean hasNext() {
					return index >= 0;
				}

				@Override
				public ExposedLinkedList<ZSortable> next() {
					return buckets[index--];
				}

				@Override
				public void remove() {
					// not implemented
				}
			};
		}
	}
}
