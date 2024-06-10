package com.perrigogames.life4.util

/**
 * Defines a destination that can be reached through navigation.
 */
interface Destination {

    /**
     * The base route provided to the navigation framework, either a simple string or
     * a template string to facilitate carrying the necessary data.
     */
    val baseRoute: String

    /**
     * The route for this specific [Destination] instance, either the same simple string
     * as [baseRoute], or a templated string.
     */
    val route: String get() = baseRoute
}