package io.github.vp.telegram.trigger

import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.abstracts.MediaContent
import dev.inmo.tgbotapi.types.message.content.media.AudioContent
import dev.inmo.tgbotapi.types.message.content.media.DocumentContent
import dev.inmo.tgbotapi.types.message.content.media.PhotoContent
import dev.inmo.tgbotapi.types.message.content.media.VideoContent
import io.github.vp.core.Filter
import io.github.vp.core.SimpleTrigger
import io.github.vp.telegram.TgUpdateRegistrar

inline fun <C, reified E: MediaContent> TgUpdateRegistrar<C>.onMedia(
    noinline filter: Filter<C, CommonMessage<E>> = { true },
    noinline trigger: SimpleTrigger<C, CommonMessage<E>>,
) {
    onContent(
        filter = filter,
        trigger = trigger
    )
}

fun <C> TgUpdateRegistrar<C>.onDocument(
    filter: Filter<C, CommonMessage<DocumentContent>> = { true },
    trigger: SimpleTrigger<C, CommonMessage<DocumentContent>>,
) {
    onMedia(filter, trigger)
}

fun <C> TgUpdateRegistrar<C>.onPhoto(
    filter: Filter<C, CommonMessage<PhotoContent>> = { true },
    trigger: SimpleTrigger<C, CommonMessage<PhotoContent>>,
) {
    onContent(
        filter = filter,
        trigger = trigger
    )
}

fun <C> TgUpdateRegistrar<C>.onVideo(
    filter: Filter<C, CommonMessage<VideoContent>> = { true },
    trigger: SimpleTrigger<C, CommonMessage<VideoContent>>,
) {
    onContent(
        filter = filter,
        trigger = trigger
    )
}

fun <C> TgUpdateRegistrar<C>.onAudio(
    filter: Filter<C, CommonMessage<AudioContent>> = { true },
    trigger: SimpleTrigger<C, CommonMessage<AudioContent>>,
) {
    onContent(
        filter = filter,
        trigger = trigger
    )
}


