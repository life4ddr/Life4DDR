package com.perrigogames.life4.feature.trials

//@OptIn(ExperimentalSerializationApi::class)
//class TrialDetailsViewModel(
//    val trial: Trial,
//    initialRankOverride: TrialRank? = null,
//) : ViewModel(), KoinComponent {
//
//    /// region Dependencies
//
//    private val trialManager: TrialManager by inject()
//    private val trialRecordsManager: TrialRecordsManager by inject()
//    private val trialGoalStringProvider: TrialGoalStringProvider by inject()
//    private val userRankManager: UserRankManager by inject()
//
//    /// endregion
//
//    /// region Ranks
//
////    private val storedRank: TrialRank? = trialManager.
//    private val initialRank: TrialRank =
//        if (trial.isEvent)
//            TrialRank.fromLadderRank(userRankManager.currentRank, true) ?:
//            TrialRank.COPPER
//        else
//            storedRank?.let { trial.rankAfter(it) } ?:
//            initialRankOverride ?:
//            TrialRank.fromLadderRank(userRankManager.currentRank, false) ?:
//            TrialRank.COPPER
//
//    private val _targetRank = MutableStateFlow(trial.toTargetRankView(initialRank)).cMutableStateFlow()
//    val targetRankView: StateFlow<TargetRankView> = _targetRank
//
//    fun setTargetRank(rank: TrialRank) {
//        viewModelScope.launch {
//            _targetRank.emit(trial.toTargetRankView(rank))
//            _session.emit(_session.value.copy(
//                goalRank = rank
//            ))
//        }
//    }
//
//    /// endregion
//
//    /// Session Progress
//
//    private val _session = MutableStateFlow(InProgressTrialSession(trial)).cMutableStateFlow()
//    val session: StateFlow<InProgressTrialSession> = _session
//
//    val exProgress: StateFlow<TrialEXProgress> = session.map { it.progress }
//        .stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.Eagerly,
//            initialValue = TrialEXProgress(0, 0, 0)
//        )
//
//    val highestPossibleRank = session.map { it.highestPossibleRank }
//
//    val songViews = session.map { session ->
//        trial.songs.map { song ->
//            TrialSongView(
//                song = song,
//                result = session.results.firstOrNull { it?.song == song }
//            )
//        }
//    }.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.Eagerly,
//        initialValue = emptyList()
//    )
//
//    fun saveSession() {
//        trialRecordsManager.save(session.value)
//    }
//
//    /// endregion
//
//    private val _filesystemChecked = MutableStateFlow(false).cMutableStateFlow()
//    val filesystemChecked: StateFlow<Boolean> = _filesystemChecked
//    fun setFilesystemChecked(checked: Boolean) {
//        viewModelScope.launch {
//            _filesystemChecked.emit(checked)
//        }
//    }
//}
//
//data class TargetRankView(
//    val rank: TrialRank,
//    val availableRanks: List<TrialRank>,
//    val goalText: String,
//)
//
//data class TrialSongView(
//    val song: Song,
//    val result: SongResult?,
//) {
//    val title get() = song.name
//    val difficultyClass get() = song.difficultyClass
//    val difficultyNumber get() = song.difficultyNumber
//    val jacketUrl get() = song.url
//    val hasResult get() = result != null
//    val resultText get() = "${result?.score} (${result?.exScore} EX)"
//    val resultBold get() = false // FIXME
//    val resultTextColor: Color? get() = null // FIXME
//}
//
//fun Trial.toTargetRankView(
//    targetRank: TrialRank,
//) = TargetRankView(
//    rank = targetRank,
//    availableRanks = goals?.map { it.rank } ?: emptyList(),
//    goalText = goals!!.first {
//        it.rank == targetRank
//    }.generateSingleGoalString(
//        trial = this,
//    )
//)