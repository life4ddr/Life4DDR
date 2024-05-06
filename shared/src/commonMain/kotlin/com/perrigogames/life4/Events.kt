package com.perrigogames.life4

import com.perrigogames.life4.data.MessageOfTheDay
import com.perrigogames.life4.data.Trial

/** Event published when a remote data source's major version is higher than the internal parser
 * can support, indicating the application should be updated */
class DataRequiresAppUpdateEvent

/** Event published when a saved rank is updated.
 * @param trial the [Trial] that was updated. If null, indicate all Trials' ranks may have been updated */
class SavedRankUpdatedEvent(val trial: Trial? = null)

/** Event published when something changes with the Trial List, requiring it to be refreshed. */
class TrialListUpdatedEvent

/** Event published when something substantial changes with the Trial List, requiring the list to recreate the entire list. */
class TrialListReplacedEvent

/** Event published when the list of saved song results changes. */
class SongResultsUpdatedEvent

/** Event published when the local user's rank is updated. */
class LadderRankUpdatedEvent

/** Event published when the rank list is updated, requiring it to be completely recreated. */
class LadderRanksReplacedEvent

/** Event published when a new message of the day should be shown. */
class MotdEvent(val motd: MessageOfTheDay)
