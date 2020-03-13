package com.perrigogames.life4.model

import com.perrigogames.life4.printThrowable
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.KoinComponent
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

//FIXME internal
class MainScope(private val mainContext: CoroutineContext) : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = mainContext + job + exceptionHandler

    internal val job = SupervisorJob()
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        showError(throwable)
    }

    //TODO: Some way of exposing this to the caller without trapping a reference and freezing it.
    fun showError(t: Throwable) {
        printThrowable(t)
    }
}
