package io.sketchware

import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

internal inline fun <reified T> String.serialize(): T {
    return Json.decodeFromString<T>(this)
}

internal inline fun <reified T> ByteArray.serialize() = String(this).serialize<T>()

internal inline fun <reified T> T.toJson() = Json.encodeToString(this)