package io.sketchware.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Cool class, fixes Sketchware shit, and even more Sketchware Pro shit.
 */
class StringBooleanSerializer(
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("stringToBoolean")
) : KSerializer<Boolean> {
    override fun deserialize(decoder: Decoder): Boolean {
        return decoder.decodeString().toBoolean()
    }

    override fun serialize(encoder: Encoder, value: Boolean) {
        encoder.encodeString(value.toString())
    }
}