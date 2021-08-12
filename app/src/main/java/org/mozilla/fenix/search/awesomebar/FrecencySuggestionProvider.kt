package org.mozilla.fenix.search.awesomebar

import mozilla.components.browser.icons.BrowserIcons
import mozilla.components.browser.icons.IconRequest
import mozilla.components.concept.awesomebar.AwesomeBar
import mozilla.components.concept.awesomebar.AwesomeBar.Suggestion
import mozilla.components.concept.storage.FrecencyThresholdOption
import mozilla.components.concept.storage.HistoryStorage
import mozilla.components.feature.session.SessionUseCases
import mozilla.components.support.utils.URLStringUtils
import java.util.UUID

internal const val FRECENCY_SUGGESTION_LIMIT = 20

/**
 * Gexsi specific History frecency provider.
 *
 * @property historyStorage used to fetch the urls by frecency
 * @property loadUrlUseCase what to do when the user click on the suggestion
 * @property browserIcons used to load the website icon
 */
class FrecencySuggestionProvider(
    private val historyStorage: HistoryStorage,
    private val loadUrlUseCase: SessionUseCases.LoadUrlUseCase,
    private val browserIcons: BrowserIcons? = null
): AwesomeBar.SuggestionProvider {

    override val id: String = UUID.randomUUID().toString()

    override suspend fun onInputChanged(text: String): List<Suggestion> {
        return historyStorage.getTopFrecentSites(FRECENCY_SUGGESTION_LIMIT, FrecencyThresholdOption.SKIP_ONE_TIME_PAGES)
            .mapIndexed { index, info ->
                val displayUrl = URLStringUtils.toDisplayUrl(info.url).toString()
                val title = if (info.title.isNullOrEmpty())
                    displayUrl
                else
                    info.title!!
                Suggestion(
                    provider = this@FrecencySuggestionProvider,
                    title = title,
                    description = displayUrl,
                    icon = browserIcons?.loadIcon(IconRequest(info.url))?.await()?.bitmap,
                    score = FRECENCY_SUGGESTION_LIMIT - index,
                    onSuggestionClicked = { loadUrlUseCase.invoke(info.url) }
                )
            }
            .distinctBy { it.description }
    }
}