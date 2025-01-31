/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.tabstray

import android.content.Context
import com.google.android.material.tabs.TabLayout
import mozilla.components.browser.menu.BrowserMenuBuilder
import mozilla.components.browser.menu.item.SimpleBrowserMenuItem
import mozilla.components.browser.state.selector.normalTabs
import mozilla.components.browser.state.selector.privateTabs
import mozilla.components.browser.state.store.BrowserStore
import org.mozilla.fenix.R
import org.mozilla.fenix.components.metrics.Event
import org.mozilla.fenix.ext.components
import org.mozilla.fenix.tabstray.ext.isNormalModeSelected
import org.mozilla.fenix.tabstray.ext.isPrivateModeSelected
/* Gexsi begin: disable sync
import org.mozilla.fenix.tabstray.ext.isSyncedModeSelected
 */

class TabsTrayMenu(
    private val context: Context,
    browserStore: BrowserStore,
    private val tabLayout: TabLayout,
    private val onItemTapped: (Item) -> Unit = {}
) {

    private val checkOpenTabs =
        when {
            tabLayout.isNormalModeSelected() ->
                browserStore.state.normalTabs.isNotEmpty()
            tabLayout.isPrivateModeSelected() ->
                browserStore.state.privateTabs.isNotEmpty()
            else ->
                false
        }

    private val shouldShowSelectOrShare = { tabLayout.isNormalModeSelected() && checkOpenTabs }
    /* Gexsi begin: disable sync
    private val shouldShowAccountSetting = { tabLayout.isSyncedModeSelected() }
     */
    private val shouldShowTabSetting = { true }

    sealed class Item {
        object ShareAllTabs : Item()
        object OpenAccountSettings : Item()
        object OpenTabSettings : Item()
        object SelectTabs : Item()
        object CloseAllTabs : Item()
        object OpenRecentlyClosed : Item()
    }

    val menuBuilder by lazy { BrowserMenuBuilder(menuItems) }

    private val menuItems by lazy {
        listOf(
            SimpleBrowserMenuItem(
                context.getString(R.string.tabs_tray_select_tabs),
                textColorResource = R.color.primary_text_normal_theme
            ) {
                onItemTapped.invoke(Item.SelectTabs)
            }.apply { visible = shouldShowSelectOrShare },

            SimpleBrowserMenuItem(
                context.getString(R.string.tab_tray_menu_item_share),
                textColorResource = R.color.primary_text_normal_theme
            ) {
                context.components.analytics.metrics.track(Event.TabsTrayShareAllTabsPressed)
                onItemTapped.invoke(Item.ShareAllTabs)
            }.apply { visible = shouldShowSelectOrShare },

            /* Gexsi begin: disable account
            SimpleBrowserMenuItem(
                context.getString(R.string.tab_tray_menu_account_settings),
                textColorResource = R.color.primary_text_normal_theme
            ) {
                onItemTapped.invoke(Item.OpenAccountSettings)
            }.apply { visible = shouldShowAccountSetting },
             */

            SimpleBrowserMenuItem(
                context.getString(R.string.tab_tray_menu_tab_settings),
                textColorResource = R.color.primary_text_normal_theme
            ) {
                onItemTapped.invoke(Item.OpenTabSettings)
            }.apply { visible = shouldShowTabSetting },

            SimpleBrowserMenuItem(
                context.getString(R.string.tab_tray_menu_recently_closed),
                textColorResource = R.color.primary_text_normal_theme
            ) {
                onItemTapped.invoke(Item.OpenRecentlyClosed)
            },

            SimpleBrowserMenuItem(
                context.getString(R.string.tab_tray_menu_item_close),
                textColorResource = R.color.primary_text_normal_theme
            ) {
                context.components.analytics.metrics.track(Event.TabsTrayCloseAllTabsPressed)
                onItemTapped.invoke(Item.CloseAllTabs)
            }.apply { visible = { checkOpenTabs } }
        )
    }
}
