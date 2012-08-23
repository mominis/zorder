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
 * Something that can be iterated over in both directions
 * 
 * @author Itay Duvdevani (MoMinis Ltd.)
 *
 * @param <E> Iterated element type
 */
public interface ReverseIterable<E> extends Iterable<E> {
	/**
	 * @return an iterator over the elements in this list in reverse sequence.
	 */
	public Iterator<E> reverseIterator();
}
