package org.mozilla.fenix.home.intent

import android.content.Intent
import androidx.navigation.NavController
import org.mozilla.fenix.BuildConfig
import org.mozilla.fenix.NavGraphDirections
import org.mozilla.fenix.components.metrics.Event
import org.mozilla.fenix.ext.nav
import org.mozilla.fenix.tabstray.TabsTrayFragmentDirections

class ActivateSearchIntentProcessor: HomeIntentProcessor {

    override fun process(intent: Intent, navController: NavController, out: Intent): Boolean {
        val action = intent.action
        return if (action != null) {
            when (action) {
                ACTIVATE_SEARCH -> {
                    navController.navigate(TabsTrayFragmentDirections.actionGlobalHome(focusOnAddressBar = true))
                    val directions = NavGraphDirections.actionGlobalSearchDialog(
                        sessionId = null,
                        searchAccessPoint = Event.PerformedSearch.SearchAccessPoint.SUGGESTION,
                        pastedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                    )
                    directions.let { navController.nav(null, it) }
                    true
                }
                else -> false
            }
        } else {
            false
        }
    }

    companion object {
        const val ACTIVATE_SEARCH = "${BuildConfig.APPLICATION_ID}.TextSearchActivity"
    }

}