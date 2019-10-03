package com.perrigogames.life4trials.manager

import com.perrigogames.life4trials.Life4Application

/**
 * A base Manager class that provides subclasses with ObjectBox access.
 */
open class BaseManager {

    protected val objectBox get() = Life4Application.objectBox

    open fun onApplicationException() {}
}