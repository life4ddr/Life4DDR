package com.perrigogames.life4.data

import com.soywiz.klock.DateTime
import com.soywiz.klock.ISO8601
import com.soywiz.klock.format
import com.soywiz.klock.parseUtc
import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor

@Serializable(DateTimeIsoSerializer::class)
class DateTimeWrapper(val value: DateTime)

@Serializer(forClass = DateTimeWrapper::class)
object DateTimeIsoSerializer: KSerializer<DateTimeWrapper> {
    override val descriptor = StringDescriptor

    override fun deserialize(decoder: Decoder): DateTimeWrapper =
        DateTimeWrapper(ISO8601.DATETIME_COMPLETE.parseUtc(decoder.decodeString()))

    override fun serialize(encoder: Encoder, obj: DateTimeWrapper) {
        encoder.encodeString(ISO8601.DATETIME_COMPLETE.format(obj.value))
    }
}
