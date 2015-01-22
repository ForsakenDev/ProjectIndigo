/*
 * ATLauncher - https://github.com/ATLauncher/ATLauncher
 * Copyright (C) 2013 ATLauncher
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package co.forsaken.projectindigo.utils.mojangtokens;

import java.io.File;

import co.forsaken.projectindigo.utils.Utils;

public class AssetObject {
  private String hash;
  private long   size;

  public String getHash() {
    return hash;
  }

  public long getSize() {
    return size;
  }

  public boolean needToDownload(File file) {
    if (!file.exists() || !file.isFile()) { return true; }
    if (file.length() != this.size) { return true; }
    return !this.hash.equalsIgnoreCase(Utils.getSHA1(file));
  }
}