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
 * A minimal double-ended linked-list which exposes it's internal links to the outside world.
 * 
 * @author Itay Duvdevani (MoMinis Ltd.)
 *
 * @param <E> Element type
 */
/* package */ class ExposedLinkedList<E> implements ReverseIterable<E> {
	
	/* --- Fields --- */
	
	/** List's head link */
	private ExposedLink head;
	
	/** List's tail link */
	private ExposedLink tail;
	
	/* --- Constructor --- */
	
	/**
	 * Initializes an empty list
	 */
	public ExposedLinkedList() {
		head = null;
		tail = null;
	}
	
	/* --- Public Methods --- */
	
	/**
	 * Appends the given object to the end of the list, giving the caller an object
	 * to remove the object from the list in constant time.
	 * 
	 * The method runs in constant time
	 * 
	 * @param object Object to append to the list
	 * @return The link the given object was stored at
	 */
	public ExposedLink append(E object) {
		ExposedLink link = new ExposedLink(object);
		if (head == null) {
			// first link
			head = link;
			tail = link;
		} else {
			// other link
			tail.next = link;
			link.prev = tail;
			tail = link;
		}
		
		return link;
	}
	
	/* --- ReverseIterable<E> Methods --- */
	
	/**
	 * @see {@link ReverseIterable<E>#iterator()}
	 */
	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			
			private ExposedLink current = head;
			
			@Override
			public boolean hasNext() {
				return current != null;
			}

			@Override
			public E next() {
				ExposedLink result = current;
				current = current.next;
				return result.object;
			}

			@Override
			public void remove() {
				current.unlink();
				current = current.next;
			}
		};
	}
	
	/**
	 * @see {@link ReverseIterable<E>#reverseIterator()}
	 */
	@Override
	public Iterator<E> reverseIterator() {
		return new Iterator<E>() {
			
			private ExposedLink current = tail;
			
			@Override
			public boolean hasNext() {
				return current != null;
			}

			@Override
			public E next() {
				ExposedLink result = current;
				current = current.prev;
				return result.object;
			}

			@Override
			public void remove() {
				current.unlink();
				current = current.prev;
			}
		};
	}
	
	/* --- Inner Classes --- */
	
	/**
	 * An exposed linked-list link
	 * 
	 * @author itayd
	 *
	 */
	public class ExposedLink implements Unlinkable {
		
		/* --- Fields --- */
		
		/** next link */
		public ExposedLink next;
		
		/** previous link */
		public ExposedLink prev;
		
		/** link's object */
		public final E object;
		
		/* --- Constructor --- */
		
		/**
		 * Constructor
		 * @param object Link's object
		 */
		public ExposedLink(E object) {
			this.object = object;
		}
		
		/* --- Unlinkable Methods --- */
		
		/**
		 * Removes this link from the list
		 */
		@Override
		public void unlink() {
			if (prev == null) {
				// head
				head = next;
			} else {
				prev.next = next;
			}
			
			if (next == null) {
				// tail
				tail = prev;
			} else {
				next.prev = prev;
			}
		}
	}	
}
