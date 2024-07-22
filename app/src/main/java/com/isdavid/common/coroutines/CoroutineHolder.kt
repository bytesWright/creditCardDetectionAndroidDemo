package com.isdavid.common.coroutines

import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

interface CoroutineHolderC {
    val localScope: CoroutineScope
    fun clear()
}

class CoroutineHolder : CoroutineHolderC {
    private val completableJob: CompletableJob = SupervisorJob()
    override val localScope = CoroutineScope(
        completableJob + Dispatchers.Main.immediate
    )

    override fun clear() = completableJob.cancel()
}