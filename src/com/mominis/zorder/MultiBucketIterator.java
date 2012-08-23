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

/**
 * Iterates over a list-of-lists either start-to-end or end-to-start.
 * 
 * The given {@link ReverseIterable} should yield objects of type: {@link ExposedLinkedList}.
 * 
 * @author Itay Duvdevani (MoMinis Ltd.)
 *
 * @param <E> Element type of inner lists
 */
/* package */ class MultiBucketIterator<E> implements Iterator<E> {
	
	/* --- Fields --- */

	/** whether to scan the list in reverse */
	private final boolean reverse;
	
	/** iterator to current bucket being iterated */
	private Iterator<ExposedLinkedList<E>> bucketsIterator;
	
	/** iterator to current bucket (inner iterator) */
	private Iterator<E> bucketIterator;
	
	/** next element to yield */
	private E next;
	
	/* --- Constructor --- */
	
	/**
	 * Constructor.
	 * 
	 * @param buckets List of internal lists to iterate.
	 * @param reverse Whether to scan the list in reverse
	 */
	public MultiBucketIterator(
			ReverseIterable<ExposedLinkedList<E>> buckets,
			boolean reverse) {
		
		this.reverse = reverse;
		
		if (!reverse) {
			bucketsIterator = buckets.iterator();
		} else {
			bucketsIterator = buckets.reverseIterator();
		}
		
		if (bucketsIterator.hasNext()) {
			if (!reverse) {
				bucketIterator = bucketsIterator.next().iterator();
			} else {
				bucketIterator = bucketsIterator.next().reverseIterator();
			}	
		}
	}
	
	/* --- Iterator<E> methods --- */
	
	/**
	 * @see {@link Iterator<E>#hasNext()}
	 */
	@Override
	public boolean hasNext() {
		
		if (bucketIterator == null) {
			return false;
		}
		
		// keep looking till we find the next valid sprite,
		// or there are no more items to examine
		while (true) {
			
			// find next item to examine
			while (!bucketIterator.hasNext()) {
				if (bucketsIterator.hasNext()) {
					if (!reverse) {
						bucketIterator = bucketsIterator.next().iterator();
					} else {
						bucketIterator = bucketsIterator.next().reverseIterator();
					}
				} else {
					// nothing valid found and no more buckets
					return false;
				}
			}
			
			next = bucketIterator.next();
			return true;
		}
	}

	/**
	 * @see {@link Iterator<E>#next()}
	 */
	@Override
	public E next() {
		return next;
	}

	/**
	 * Not implemented.
	 */
	@Override
	public void remove() {
		// intentionally left blank (not implemented)
	}
}
