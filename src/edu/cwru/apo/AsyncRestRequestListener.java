/*
 * Copyright 2011 Devin Schwab, Umang Banugaria
 *
 * This file is part of the APO Theta Upsilon App for Case Western Reserve University's Alpha Phi Omega Theta Upsilon Chapter.
 *
 * The APO Theta Upsilon program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package edu.cwru.apo;

public interface AsyncRestRequestListener<U, T> {
	public void onRestRequestComplete(U method, T result);
}
