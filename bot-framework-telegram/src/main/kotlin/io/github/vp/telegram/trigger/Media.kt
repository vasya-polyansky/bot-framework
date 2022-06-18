@file:OptIn(PreviewFeature::class)

package io.github.vp.telegram.trigger

import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.AudioContent
import dev.inmo.tgbotapi.types.message.content.DocumentContent
import dev.inmo.tgbotapi.types.message.content.MediaContent
import dev.inmo.tgbotapi.types.message.content.PhotoContent
import dev.inmo.tgbotapi.types.message.content.VideoContent
import dev.inmo.tgbotapi.utils.PreviewFeature
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
