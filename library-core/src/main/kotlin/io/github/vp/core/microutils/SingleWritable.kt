package io.github.vp.core.microutils

import kotlin.reflect.KProperty

fun <T> singleWritable() = SingleWritable<T>()
fun <T> singleWritable(init: (() -> T)) = SingleWritable(init)
fun <T> singleWritable(default: T) = SingleWritable { default }


class SingleWritable<T>(private val init: (() -> T)? = null) {
    private object Key

    private val holder = mutableMapOf<Key, T>()

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T =
        synchronized(holder) {
            holder.getOrPut(Key) {
                if (init == null) {
                    throw IllegalStateException("Value has not been written")
                }

                init.invoke()
            }
        }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) =
        synchronized(holder) {
            if (holder.containsKey(Key)) {
                throw IllegalStateException("Value has already been written")
            }

            holder[Key] = value
        }
}