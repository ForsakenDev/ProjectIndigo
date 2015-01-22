/*
 * This file is part of ForsakenSuite.
 *
 * Copyright © 2012-2014,
 * 									ForsakenNetwork LLC
 * 									<http://www.forsaken.co/>
 * ForsakenSuite is licensed under the Forsaken Network License Version 1
 *
 * ForsakenSuite is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Forsaken Network License Version 1.
 *
 * ForsakenSuite is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Forsaken Network License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License.
 */
package co.forsaken.projectindigo.utils;

public abstract interface Callback<T> {
  public abstract void run(T paramT);
}
