package io.github.vp.core

import arrow.core.None
import arrow.core.Option
import arrow.core.Some

fun <T> T?.toListOption(): Option<List<T>> = this?.let { Some(listOf(it)) } ?: None
