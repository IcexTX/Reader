/*
 * Copyright (C) 2009-2015 FBReader.ORG Limited <contact@fbreader.org>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.android.fbreader;

import org.geometerplus.android.fbreader.api.MenuNode;
import org.geometerplus.fbreader.fbreader.ActionCode;
import org.geometerplus.zlibrary.ui.android.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 菜单数据
 */
public abstract class MenuData {

    private static List<MenuNode> ourNodes;

    private static void addToplevelNode(MenuNode node) {
        ourNodes.add(node);
    }

    /**
     * 菜单数据
     *
     * @return
     */
    public static synchronized List<MenuNode> topLevelNodes() {
        if (ourNodes == null) {
            ourNodes = new ArrayList<>();
            addToplevelNode(new MenuNode.Item(ActionCode.SHOW_LIBRARY, R.drawable.ic_menu_library));
            addToplevelNode(new MenuNode.Item(ActionCode.SHOW_BOOKMARKS, R.drawable.ic_menu_bookmarks));
            addToplevelNode(new MenuNode.Item(ActionCode.SWITCH_TO_NIGHT_PROFILE, R.drawable.ic_menu_night));
            addToplevelNode(new MenuNode.Item(ActionCode.SWITCH_TO_DAY_PROFILE, R.drawable.ic_menu_day));
            addToplevelNode(new MenuNode.Item(ActionCode.SEARCH, R.drawable.ic_menu_search));
            addToplevelNode(new MenuNode.Item(ActionCode.SHARE_BOOK));
            addToplevelNode(new MenuNode.Item(ActionCode.SHOW_PREFERENCES));
            addToplevelNode(new MenuNode.Item(ActionCode.SHOW_NAVIGATION));
            ourNodes = Collections.unmodifiableList(ourNodes);
        }
        return ourNodes;
    }
}
