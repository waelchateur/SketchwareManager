package io.sketchware.utils

object OpCodes {
    val logic = javaClass.getResource("/opcodes/logic").readText().split("\n")
}