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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

enum Action {
    ALLOW, DISALLOW
}

public class Rule {

    private Action action; // If it should be allowed
    private OperatingSystemRule os; // The OS this rule applies to

    public boolean ruleApplies() {
        if (this.os == null) {
            return true;
        }
        if (this.os.getName() != null && this.os.getName() != OperatingSystem.getOS()) {
            return false;
        }
        if (this.os.getVersion() == null) {
            return true;
        }
        Pattern pattern = Pattern.compile(this.os.getVersion());
        Matcher matcher = pattern.matcher(OperatingSystem.getVersion());
        return matcher.matches();
    }

    public Action getAction() {
        return this.action;
    }
}
