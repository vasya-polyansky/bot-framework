package framework


// TODO: Maybe make this context customizable
data class FilterContext<TEvent : Any>(
    val pipeline: EventPipeline<TEvent>,
)

interface Registrar<TEvent : Any, TEventContext> {
    fun <R> register(handler: Handler<TEvent, TEventContext, R>)
}

/**
 * The order of parameters is strict
 */
fun <TEvent : Any, TEventContext, R> Registrar<TEvent, TEventContext>.register(
    trigger: EventTrigger<TEventContext, R>,
    selector: ConvertingFilter<TEvent, R>,
) {
    register(Handler(selector, trigger))
}
