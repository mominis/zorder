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
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 
 * @author Itay Duvdevani (MoMinis Ltd.)
 *
 */
public class SimpleZCollectionTests {
	
	protected static final int MAX_Z = 10;
	
	protected Random rand;
	protected ZCollection underTest;
	
	@Before
	public void setUp() throws Exception {
		rand = new Random(System.nanoTime());
		underTest = new SimpleZCollection(MAX_Z);
	}

	@Test
	public void addOneBackToFront() throws Exception {
		SimpleObject object = new SimpleObject("object");
		int z = Math.abs(rand.nextInt()) % (MAX_Z + 1);
		
		object.setZOrder(z);
		underTest.add(object);
		
		Iterator<ZSortable> iter = underTest.backToFrontIterator();
		assertNotNull(iter);
		assertTrue(iter.hasNext());
		assertSame(object, iter.next());
		assertFalse(iter.hasNext());
	}
	
	@Test
	public void removeOneBackToFront() throws Exception {
		SimpleObject object = new SimpleObject("object");
		int z = Math.abs(rand.nextInt()) % (MAX_Z + 1);
		
		object.setZOrder(z);
		underTest.add(object);
		
		underTest.remove(object);
		
		Iterator<ZSortable> iter = underTest.backToFrontIterator();
		assertNotNull(iter);
		assertFalse(iter.hasNext());
	}
	
	@Test
	public void changeOneBackToFront() throws Exception {
		SimpleObject object = new SimpleObject("object");
		// choose two different z levels
		int z1 = Math.abs(rand.nextInt()) % (MAX_Z + 1);
		int z2 = Math.abs(rand.nextInt()) % (MAX_Z + 1);
		while (z2 == z1) {
			z2 = Math.abs(rand.nextInt()) % (MAX_Z + 1);
		}
		
		object.setZOrder(z1);
		underTest.add(object);
		
		object.setZOrder(z2);
		underTest.change(object);
		
		Iterator<ZSortable> iter = underTest.backToFrontIterator();
		assertNotNull(iter);
		assertTrue(iter.hasNext());
		assertSame(object, iter.next());
		assertFalse(iter.hasNext());
	}
	
	@Test
	public void multipleInSameBucketBackToFront() throws Exception {
		SimpleObject object = new SimpleObject("object");
		// choose two different z levels
		int z1 = Math.abs(rand.nextInt()) % (MAX_Z + 1);
		int z2 = Math.abs(rand.nextInt()) % (MAX_Z + 1);
		while (z2 == z1) {
			z2 = Math.abs(rand.nextInt()) % (MAX_Z + 1);
		}
		
		object.setZOrder(z1);
		underTest.add(object);
		
		object.setZOrder(z2);
		underTest.change(object);
		
		// change back
		object.setZOrder(z1);
		underTest.change(object);
		
		Iterator<ZSortable> iter = underTest.backToFrontIterator();
		assertNotNull(iter);
		assertTrue(iter.hasNext());
		assertSame(object, iter.next());
		assertFalse(iter.hasNext());
	}
	
	@Test
	public void multipleInSameBucketPreserveOrderBackToFront() throws Exception {
		SimpleObject object1 = new SimpleObject("object1");
		SimpleObject object2 = new SimpleObject("object2");
		
		// choose two different z levels
		int z1 = Math.abs(rand.nextInt()) % (MAX_Z + 1);
		int z2 = Math.abs(rand.nextInt()) % (MAX_Z + 1);
		while (z2 == z1) {
			z2 = Math.abs(rand.nextInt()) % (MAX_Z + 1);
		}
		
		object1.setZOrder(z1);
		underTest.add(object1);
		
		object2.setZOrder(z1);
		underTest.add(object2);
		
		Iterator<ZSortable> iter = underTest.backToFrontIterator();
		assertNotNull(iter);
		assertTrue(iter.hasNext());
		assertSame(object1, iter.next());
		assertTrue(iter.hasNext());
		assertSame(object2, iter.next());
		assertFalse(iter.hasNext());
		
		object1.setZOrder(z2);
		underTest.change(object1);
		
		// change back
		object1.setZOrder(z1);
		underTest.change(object1);
		
		iter = underTest.backToFrontIterator();
		assertNotNull(iter);
		assertTrue(iter.hasNext());
		assertSame(object2, iter.next());
		assertTrue(iter.hasNext());
		assertSame(object1, iter.next());
		assertFalse(iter.hasNext());
	}
}
