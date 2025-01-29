package com.perrigogames.life4.feature.banners

import com.perrigogames.life4.MR
import com.perrigogames.life4.model.BaseModel
import dev.icerock.moko.resources.desc.Raw
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.color.ColorDesc
import dev.icerock.moko.resources.desc.color.asColorDesc
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

/**
 * Interface for managing and retrieving banners based on their display location.
 */
interface IBannerManager {

    /**
     * Retrieves a StateFlow containing the banner currently displayed at a specified location.
     *
     * @param location The location where the banner is being displayed, represented by a BannerLocation.
     * @return A StateFlow representing the banner to be displayed at that location.
     */
    fun getBannerFlow(location: BannerLocation): StateFlow<UIBanner?>

    /**
     * Displays the specified banner at one or more given locations.
     * If the banner is null, it removes the banner instead.
     *
     * @param banner The `UIBanner` instance to be displayed, or null to remove the banner.
     * @param locations The `BannerLocation`s where the banner should be displayed.
     * @param durationSeconds The number of seconds the banner should show.  A null value means the
     *  banner will not automatically clear itself.
     */
    fun setBanner(
        banner: UIBanner?,
        vararg locations: BannerLocation,
        durationSeconds: Long? = null,
    )
}

class BannerManager : IBannerManager, BaseModel() {

    private val _banners = mutableMapOf<BannerLocation, MutableStateFlow<UIBanner?>>()

    init {
        BannerLocation.entries.forEach { _banners[it] = MutableStateFlow(null) }
    }

    override fun getBannerFlow(location: BannerLocation): StateFlow<UIBanner?> {
        return _banners[location]!!.asStateFlow()
    }

    override fun setBanner(
        banner: UIBanner?,
        vararg locations: BannerLocation,
        durationSeconds: Long?,
    ) {
        locations.forEach { _banners[it]!!.value = banner }
        if (durationSeconds != null) {
            mainScope.launch {
                delay(durationSeconds.seconds)
                locations.forEach { _banners[it]!!.value = null }
            }
        }
    }

    companion object {
        val DUMMY_BANNER = UIBanner(
            text = StringDesc.Raw("Testing..."),
            backgroundColor = MR.colors.colorAccent.asColorDesc(),
            textColor = MR.colors.white.asColorDesc()
        )
    }
}

/**
 * Represents a banner UI element that can display a text with an optional color.
 *
 * @property text The textual content of the banner.
 * @property backgroundColor The background color of the banner. If null, use an appropriate
 *  native adaptable color.
 * @property textColor The color of the banner text. If null, use an appropriate native
 *  adaptable color.
 */
data class UIBanner(
    val text: StringDesc,
    val backgroundColor: ColorDesc? = null,
    val textColor: ColorDesc? = null,
)

/**
 * Represents the location where a banner can be displayed.
 */
enum class BannerLocation {
    PROFILE, SCORES
}
