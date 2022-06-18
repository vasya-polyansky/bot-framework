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
    noinline filter: Filter<C, CommonMessage<E>>? = null,
    noinline trigger: SimpleTrigger<C, CommonMessage<E>>,
) {
    onContent(filter, trigger)
}

fun <C> TgUpdateRegistrar<C>.onDocument(
    filter: Filter<C, CommonMessage<DocumentContent>>? = null,
    trigger: SimpleTrigger<C, CommonMessage<DocumentContent>>,
) {
    onMedia(filter, trigger)
}

fun <C> TgUpdateRegistrar<C>.onPhoto(
    filter: Filter<C, CommonMessage<PhotoContent>>? = null,
    trigger: SimpleTrigger<C, CommonMessage<PhotoContent>>,
) {
    onMedia(filter, trigger)
}

fun <C> TgUpdateRegistrar<C>.onVideo(
    filter: Filter<C, CommonMessage<VideoContent>>? = null,
    trigger: SimpleTrigger<C, CommonMessage<VideoContent>>,
) {
    onMedia(filter, trigger)
}

fun <C> TgUpdateRegistrar<C>.onAudio(
    filter: Filter<C, CommonMessage<AudioContent>>? = null,
    trigger: SimpleTrigger<C, CommonMessage<AudioContent>>,
) {
    onMedia(filter, trigger)
}
