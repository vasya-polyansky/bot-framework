package io.github.vp.core

import arrow.core.None
import arrow.core.Option
import arrow.core.Some

fun <T> T?.toListInOption(): Option<Iterable<T>> {
    return this?.let { Some(listOf(it)) } ?: None
}
