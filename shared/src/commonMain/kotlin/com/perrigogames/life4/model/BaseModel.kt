package com.perrigogames.life4.model

import co.touchlab.kermit.Logger
import com.perrigogames.life4.injectLogger
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.component.KoinComponent
import kotlin.coroutines.CoroutineContext

open class BaseModel : KoinComponent {
    val mainScope = MainScope(Dispatchers.Main)
    val ktorScope = MainScope(Dispatchers.Main)

    open fun onDestroy() {
        mainScope.job.cancel()
        ktorScope.job.cancel()
    }

    open fun onApplicationException() {}
}

// FIXME internal
class MainScope(private val mainContext: CoroutineContext) : CoroutineScope, KoinComponent {
    private val logger: Logger by injectLogger("MainScope")

    override val coroutineContext: CoroutineContext
        get() = mainContext + job + exceptionHandler

    internal val job = SupervisorJob()
    private val exceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            logger.e(throwable) { "Coroutine exception encountered" }
        }
}
