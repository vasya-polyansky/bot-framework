package framework.feature.fsm

import arrow.core.None
import framework.Handler
import framework.HandlersBuilder
import framework.Registrar
import io.ktor.util.*


class State<TEvent : Any, TEventContext>(
    private val registrationBlock: Registrar<TEvent, TEventContext>.() -> Unit,
) {
    fun <TToken : Any> register(
        token: TToken,
        stateKey: AttributeKey<TToken>,
        registrar: Registrar<TEvent, TEventContext>,
    ) {
        HandlersBuilder<TEvent, TEventContext>()
            .apply(registrationBlock)
            .build()
            .map { it.mapWithState(token, stateKey) }
            .forEach { registrar.register(it) }
    }
}

private fun <TEvent : Any, TEventContext, TToken : Any, R> Handler<TEvent, TEventContext, R>.mapWithState(
    token: TToken,
    stateKey: AttributeKey<TToken>,
): Handler<TEvent, TEventContext, R> =
    copy(
        selector = {
            if (pipeline.attributes[stateKey] != token) {
                None
            } else {
                selector(it)
            }
        }
    )


// TODO: Maybe add interface for this class
/**
 * This class is required to automatically bind types for state
 */
open class BaseFsm<TEvent : Any, TEventContext> {
    fun state(
        block: Registrar<TEvent, TEventContext>.() -> Unit,
    ): State<TEvent, TEventContext> = State(block)
}
