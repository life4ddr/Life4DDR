package com.perrigogames.life4.data

/**
 * Sealed class denoting the different social networks available in the app, as well as
 * a catch-all for custom networks.
 */
sealed class SocialNetwork {

    object Discord: SocialNetwork()
    object Twitter: SocialNetwork()
    object YouTube: SocialNetwork()
    object Facebook: SocialNetwork()
    data class Other(val name: String): SocialNetwork() {
        override fun toString() = name
    }

    override fun toString(): String {
        return this::class.simpleName ?: ""
    }

    companion object {
        fun parse(s: String): SocialNetwork {
            return when (s) {
                Discord.toString() -> Discord
                Twitter.toString() -> Twitter
                YouTube.toString() -> YouTube
                Facebook.toString() -> Facebook
                else -> Other(s)
            }
        }
    }
}