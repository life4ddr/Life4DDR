package com.perrigogames.life4.data

import kotlin.test.Test
import kotlin.test.assertEquals

class SocialNetworkTest {

    @Test
    fun `test serializing built-in social networks`() {
        assertEquals("Discord", SocialNetwork.Discord.toString())
        assertEquals("Twitter", SocialNetwork.Twitter.toString())
        assertEquals("YouTube", SocialNetwork.YouTube.toString())
        assertEquals("Facebook", SocialNetwork.Facebook.toString())
    }

    @Test
    fun `test parsing built-in social networks`() {
        assertEquals(SocialNetwork.Discord, SocialNetwork.parse("Discord"))
        assertEquals(SocialNetwork.Twitter, SocialNetwork.parse("Twitter"))
        assertEquals(SocialNetwork.YouTube, SocialNetwork.parse("YouTube"))
        assertEquals(SocialNetwork.Facebook, SocialNetwork.parse("Facebook"))
    }

    @Test
    fun `test serializing custom social networks`() {
        assertEquals("foobar", SocialNetwork.Other("foobar").toString())
        assertEquals("bazqux", SocialNetwork.Other("bazqux").toString())
    }

    @Test
    fun `test parsing custom social networks`() {
        assertEquals(SocialNetwork.Other("foobar"), SocialNetwork.parse("foobar"))
        assertEquals(SocialNetwork.Other("bazqux"), SocialNetwork.parse("bazqux"))
    }
}