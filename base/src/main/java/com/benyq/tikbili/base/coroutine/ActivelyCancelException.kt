package com.benyq.tikbili.base.coroutine

import kotlin.coroutines.cancellation.CancellationException

class ActivelyCancelException : CancellationException()