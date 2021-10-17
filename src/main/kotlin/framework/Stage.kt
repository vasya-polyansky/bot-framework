package framework

typealias LifecycleTrigger<TC> = suspend TC.() -> Unit

// TODO: Maybe we don't need this class
data class Stage<TC>(
    val init: LifecycleTrigger<TC>,
    val dispose: LifecycleTrigger<TC>,
)
