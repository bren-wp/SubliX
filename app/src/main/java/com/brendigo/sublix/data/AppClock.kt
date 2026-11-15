package com.brendigo.sublix.data

import com.brendigo.sublix.BuildConfig
import java.time.LocalDate

/** Deterministic screenshot date for debug captures only; never changes the system clock. */
object AppClock {
    @Volatile private var debugDate: LocalDate? = null
    fun today(): LocalDate = if (BuildConfig.DEBUG) debugDate ?: LocalDate.now() else LocalDate.now()
    fun setDebugCaptureDate(date: LocalDate) { if (BuildConfig.DEBUG) debugDate = date }
}
