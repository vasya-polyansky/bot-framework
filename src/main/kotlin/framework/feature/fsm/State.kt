package framework.feature.fsm

import framework.Registrar

// Mocking class
class State

fun <TEvent, TEventContext> state(
    handlers: Registrar<TEvent, TEventContext>.() -> Unit,
): State {
    return State()
}