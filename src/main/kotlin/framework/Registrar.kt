package framework

interface Registrar<TEvent, TEventContext> {
    fun <R> register(
        trigger: EventTrigger<TEventContext, R>,
        filter: ResultingFilter<TEvent, R>,
    )
}