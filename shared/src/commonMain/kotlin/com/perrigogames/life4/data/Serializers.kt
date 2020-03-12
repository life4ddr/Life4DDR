package com.perrigogames.life4.data

import com.soywiz.klock.DateTime
import com.soywiz.klock.ISO8601
import com.soywiz.klock.format
import com.soywiz.klock.parseUtc
import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor

@Serializer(forClass = DateTime::class)
object DateTimeIsoSerializer: KSerializer<DateTime> {
    override val descriptor = StringDescriptor

    override fun deserialize(decoder: Decoder): DateTime =
        ISO8601.DATE_CALENDAR_COMPLETE.parseUtc(decoder.decodeString())

    override fun serialize(encoder: Encoder, obj: DateTime) {
        encoder.encodeString(ISO8601.DATE_CALENDAR_COMPLETE.format(obj))
    }
}
