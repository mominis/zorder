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
 * A fixed-point {@link ZCollection}, imposing no limitation on legal
 * Z-orders.
 * 
 * Working only with predetermined levels will result in optimal performance
 * similar to {@link SimpleZCollection}.
 * 
 * Why fixed-point: We wrote this code to run on mobile devices, where fixed-point
 * arithmetics is sometimes faster than floating-point arithmetics.
 * 
 * @author Itay Duvdevani (MoMinis Ltd.)
 *
 */
public class FixedPointZCollection implements ZCollection {
	
	/* --- Constants --- */
	
	/** Fixed-point pivot (=1) */
	public static final int PIVOT = 1000;
	
	/* --- Fields --- */
	
	/** max. optimized Z-order */
	private final int maxZ;
	
	/** list of open buckets */
	private final ExposedLinkedList<ListWithZ> buckets;
	
	/** quick access to certain links in the buckets list */
	private final ExposedLinkedList<ListWithZ>.ExposedLink[] quickAccess;
	
	/** quick access to the last link in the list */
	private final ExposedLinkedList<ListWithZ>.ExposedLink maxLevelList;
	
	/* --- Constructor --- */
	
	/**
	 * Initializes an empty collection, supporting optimized work with
	 * natural Z-orders that are less than or equals to maxZ.
	 * 
	 * Z-orders should be given as fixed-point numbers with {@link #PIVOT}
	 * 
	 * @param maxZ Max. optimized Z-order (inclusive)
	 */
	@SuppressWarnings("unchecked")
	public FixedPointZCollection(int maxZ) {
		this.maxZ = maxZ;
		buckets = new ExposedLinkedList<ListWithZ>();
		quickAccess = new ExposedLinkedList.ExposedLink[maxZ + 1];
		
		// at the expense of one always empty list we get much easier life later
		// if we initialize both ends of allowed Z-orders
		ListWithZ bucket = new ListWithZ(Integer.MIN_VALUE);
		buckets.append(bucket);
		
		// initialize optimized buckets
		for (int i = 0 ; i <= maxZ ; ++i) {
			bucket = new ListWithZ(i * PIVOT);
			quickAccess[i] = (ExposedLinkedList<ListWithZ>.ExposedLink)buckets.append(bucket);
		}
		
		// initialize last bucket
		bucket = new ListWithZ(Integer.MAX_VALUE);
		maxLevelList = (ExposedLinkedList<ListWithZ>.ExposedLink)buckets.append(bucket);
	}
	
	/* --- ZCollection Methods --- */

	/**
	 * Appends the given object to an existing bucket representing the object's
	 * Z-order, or opens a new bucket if none exists.
	 * 
	 * Using integer values between 0 and maxZ (inclusive) will give best
	 * performance.
	 * 
	 * Object's Z-order should be fixed-point.
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
		
		int zOrder = object.getZOrder();
		Unlinkable currentLink;
		
		if (zOrder >= 0) {
			if (zOrder == Integer.MAX_VALUE) {
				// special case, default for all new sprites
				currentLink = maxLevelList.object.List.append(object);
			} else if (zOrder % PIVOT == 0 && zOrder / PIVOT <= maxZ) {
				// optimized Z-order
				currentLink = quickAccess[zOrder / PIVOT].object.List.append(object);
			} else {
				// non-optimized - look for an existing bucket, or a place for new one
				ExposedLinkedList<ListWithZ>.ExposedLink current =
					quickAccess[Math.min(maxZ, zOrder / PIVOT)];
				
				while (current.object.Z < zOrder && current.next.object.Z <= zOrder) {
					current = current.next;
				}
				
				currentLink = addOrCreateBucket(object, current, true);
			}
		} else {
			// negative - look for an existing bucket, or a place for new one
			ExposedLinkedList<ListWithZ>.ExposedLink current = quickAccess[0];
			
			while (current.object.Z > zOrder && current.prev.object.Z >= zOrder) {
				current = current.prev;
			}
			
			currentLink = addOrCreateBucket(object, current, false);
		}
		
		object.setCurrentLink(currentLink);
	}

	/**
	 * @see {@link ZCollection#remove(ZSortable)}
	 */
	@Override
	public void remove(ZSortable object) {
		object.getCurrentLink().unlink();
		object.setCurrentLink(null);
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
		return new MultiBucketIterator<ZSortable>(new MyBucketsIterable(), false);
	}

	/**
	 * @see {@link ZCollection#frontToBackIterator()}
	 */
	@Override
	public Iterator<ZSortable> frontToBackIterator() {
		return new MultiBucketIterator<ZSortable>(new MyBucketsIterable(), true);
	}
	
	/* --- Private Methods --- */
	
	/**
	 * Adds the given object to the given bucket if there's a Z match,
	 * or creates a new bucket before/after given bucket for the object. 
	 * 
	 * @param object Object to add
	 * @param bucket Bucket to work with
	 * @param after Whether to add new bucket before or after given bucket
	 * @return Link to object-in-bucket
	 */
	private Unlinkable addOrCreateBucket(
			ZSortable object,
			ExposedLinkedList<ListWithZ>.ExposedLink bucket,
			boolean after) {
		int zOrder = object.getZOrder();
		if (bucket.object.Z == zOrder) {
			// existing bucket
			return bucket.object.List.append(object);
		} else {
			// open a new bucket
			ListWithZ listWithZ = new ListWithZ(zOrder);				
			ExposedLinkedList<ListWithZ>.ExposedLink link = buckets.new ExposedLink(listWithZ);
			
			if (after) {
				link.prev = bucket;
				link.next = bucket.next;
				
				if (link.prev != null) {
					link.prev.next = link;
				}
				
				if (link.next != null) { 
					link.next.prev = link;
				}
			} else {
				link.prev = bucket.prev;
				link.next = bucket;
				
				if (link.prev != null) {
					link.prev.next = link;
				}
				
				if (link.next != null) { 
					link.next.prev = link;
				}
			}
			
			return link.object.List.append(object);
		}
	}
	
	/* --- Inner Classes --- */

	/**
	 * A wrapper for a list that remembers which Z-order it represents
	 * 
	 * @author itayd
	 *
	 */
	private static class ListWithZ {
		
		/* --- Constructor --- */
		
		/**
		 * Initializes an empty list
		 * @param z List's Z-order
		 */
		public ListWithZ(int z) {
			this.List = new ExposedLinkedList<ZSortable>();
			this.Z = z;
		}
		
		/* --- Fields --- */
		
		/** Encapsulated list */
		public final ExposedLinkedList<ZSortable> List;
		
		/** List's Z-order */
		public final int Z;
	}
	
	/**
	 * Auxiliary class that wraps a list of {@link ListWithZ} and exposes an iterator
	 * of exposed lists over {@link ZSortable}
	 * 
	 * @author itayd
	 *
	 */
	private class MyBucketsIterable implements ReverseIterable<ExposedLinkedList<ZSortable>> {
	
		/**
		 * @see ReverseIterable#iterator()
		 */
		@Override
		public Iterator<ExposedLinkedList<ZSortable>> iterator() {
			return new Iterator<ExposedLinkedList<ZSortable>>() {
				
				Iterator<ListWithZ> iterator = buckets.iterator();
				
				@Override
				public boolean hasNext() {
					return iterator.hasNext();
				}

				@Override
				public ExposedLinkedList<ZSortable> next() {
					return iterator.next().List;
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
				
				Iterator<ListWithZ> iterator = buckets.reverseIterator();
				
				@Override
				public boolean hasNext() {
					return iterator.hasNext();
				}

				@Override
				public ExposedLinkedList<ZSortable> next() {
					return iterator.next().List;
				}

				@Override
				public void remove() {
					// not implemented
				}
			};
		}
	}
}
