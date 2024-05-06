package com.perrigogames.life4.data

/**
 * Describes a class that has a version number. Higher version numbers should
 * override smaller version numbers in priority.
 */
interface Versioned {
    val version: Int
}

/**
 * Describes a class that, in addition to a version number, has a major version
 * number. This major version should be incremented if the data format gets to the
 * point where older versions of the parser can't adequately handle it.
 */
interface MajorVersioned : Versioned {
    val majorVersion: Int
}
