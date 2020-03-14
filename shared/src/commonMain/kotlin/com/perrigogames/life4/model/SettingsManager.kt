package com.perrigogames.life4.model

import com.perrigogames.life4.isDebug
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import org.koin.core.inject

@Deprecated("Replaced by Settings multiplatform module")
class SettingsManager: BaseModel() {

    private val settings: Settings by inject()

    /** @return a stored flag in the user preferences */
    fun getUserFlag(flag: String, def: Boolean) = settings.getBoolean(flag, def)

    /** @return a stored int in the user preferences */
    fun getUserInt(flag: String, def: Int) = settings.getInt(flag, def)

    /** @return a stored Long in the user preferences */
    fun getUserLong(flag: String, def: Long) = settings.getLong(flag, def)

    /** @return a stored string in the user preferences */
    fun getUserString(flag: String, def: String? = null): String? = settings.getStringOrNull(flag) ?: def

    /** @return a stored debug flag in the user preferences, also checking the debug state of the app */
    fun getDebugFlag(flag: String) = isDebug && getUserFlag(flag, false)

    fun setUserFlag(flag: String, v: Boolean) = settings.putBoolean(flag, v)

    fun setUserInt(flag: String, v: Int) = settings.putInt(flag, v)

    fun setUserLong(flag: String, v: Long) = settings.putLong(flag, v)

    fun setUserString(flag: String, v: String? = null) { settings[flag] = v }

    //
    // Major Update
    //
    //FIXME major update
//    fun handleMajorUpdate(c: Context) {
//        val currentUpdate = getUserInt(KEY_MAJOR_UPDATE, -1)
//        MajorUpdate.values()
//            .filter { it.ordinal > currentUpdate }
//            .forEach {
//                Log.i(javaClass.simpleName, "Processing upgrade ${it.name}")
//                eventBus.postSticky(MajorUpdateProcessEvent(it))
//            }
//        setUserInt(KEY_MAJOR_UPDATE, MajorUpdate.values().last().ordinal)
//    }

    companion object {
//        const val KEY_MAJOR_UPDATE = "major_update"
        const val KEY_SONG_LIST_VERSION = "song_list_version"
    }
}
