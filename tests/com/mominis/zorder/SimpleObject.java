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

/**
 * 
 * @author Itay Duvdevani (MoMinis Ltd.)
 *
 */
public class SimpleObject implements ZSortable {
	
	private final String name;
	private int zOrder;
	private Unlinkable currentLink;
	
	public SimpleObject(String name) {
		this.name = name;
	}
	
	public String toString() {
		return String.format("{ [%s] z:%d }", name, zOrder);
	}

	@Override
	public int getZOrder() {
		return zOrder;
	}

	public void setZOrder(int zOrder) {
		this.zOrder = zOrder;
	}

	@Override
	public Unlinkable getCurrentLink() {
		return currentLink;
	}

	@Override
	public void setCurrentLink(Unlinkable currentLink) {
		this.currentLink = currentLink;
	}

}
