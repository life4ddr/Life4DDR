package com.perrigogames.life4trials.manager

import com.perrigogames.life4trials.Life4Application

/**
 * A base Repo class that provides subclasses with ObjectBox access.
 */
open class BaseRepo {
    protected val objectBox get() = Life4Application.objectBox
}

open class BaseManager: BaseRepo() {
    open fun onApplicationException() {}
}