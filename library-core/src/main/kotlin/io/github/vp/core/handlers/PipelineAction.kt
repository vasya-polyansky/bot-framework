package io.github.vp.core.handlers

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

sealed interface PipelineAction {
    object Continue : PipelineAction
    object Finish : PipelineAction
}

@OptIn(ExperimentalContracts::class)
fun PipelineAction.isFinish() : Boolean {
    contract {
        returns(true) implies (this@isFinish is PipelineAction.Finish)
    }
    return this is PipelineAction.Finish
}
