package com.perrigogames.life4trials.event

import com.perrigogames.life4trials.data.Trial

/**
 * Event published when a saved rank is updated.
 * @param trial the [Trial] that was updated. If null, indicate all Trials' ranks may have been updated
 */
class SavedRankUpdatedEvent(val trial: Trial? = null)