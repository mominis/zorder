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
 * An always-sorted O(1) collection of {@link ZSortable}s.
 * 
 * All actions are done in constant time.
 * 
 * @author Itay Duvdevani (MoMinis Ltd.)
 *
 */
public interface ZCollection {

	/**
	 * Adds an object to the collection.
	 * 
	 * An object cannot be added more than once to the collection. Either {@link #remove(ZSortable)} it
	 * before adding, or use {@link #change(ZSortable)}.
	 * 
	 * In addition, an object cannot be added to two different collections at the same time -
	 * An object is either present in a single collection, or none at all. 
	 * 
	 * @param object Object to add to the collection
	 * @throws AlreadyInCollectionException Thrown when the object has been already added to the collection.
	 * @throws IllegalArgumentException If the object's Z-order is invalid
	 */
	public void add(ZSortable object) throws AlreadyInCollectionException;
	
	/**
	 * Removes the given object from the collection
	 * @param object Object to remove
	 */
	public void remove(ZSortable object);
	
	/**
	 * Notify the collection that the object's Z-order has been changed
	 * @param object Object to the change Z-order of
	 * @throws IllegalArgumentException If the object's Z-order is invalid
	 */
	public void change(ZSortable object);
	
	/**
	 * Iterate sortables in back-to-front order
	 * @return Back-to-front iterator
	 */
	public Iterator<ZSortable> backToFrontIterator();
	
	/**
	 * Iterate sortables in front-to-back order
	 * @return Front-to-back iterator
	 */
	public Iterator<ZSortable> frontToBackIterator();
	
}
