package com.brendigo.sublix.data

import android.app.Application
import android.util.Log
import com.brendigo.sublix.BuildConfig
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.time.LocalDate

/**
 * All database changes and associated StateFlow refreshes are serialized, so rapid
 * taps cannot publish stale snapshots. Database failures are reported, not allowed
 * to escape as uncaught coroutine exceptions that terminate the application.
 */
class SubscriptionViewModel(app: Application): AndroidViewModel(app) {
    private val db = SubscriptionsDb(app)
    private val operationMutex = Mutex()

    private val _subscriptions = MutableStateFlow<List<Subscription>>(emptyList())
    val subscriptions = _subscriptions.asStateFlow()
    private val _payments = MutableStateFlow<List<PaidRecord>>(emptyList())
    val payments = _payments.asStateFlow()
    private val _priceEvents = MutableStateFlow<List<PriceEvent>>(emptyList())
    val priceEvents = _priceEvents.asStateFlow()

    private val _errors = MutableSharedFlow<String>(extraBufferCapacity = 8)
    val errors = _errors.asSharedFlow()

    init { reload() }

    private suspend fun runDatabaseOperation(operation: () -> Unit): Result<Unit> =
        withContext(Dispatchers.IO) {
            operationMutex.withLock {
                try {
                    operation()
                    // Publish after the database transaction commits, while holding
                    // the lock to prevent out-of-order UI snapshots.
                    val plans = db.subscriptions()
                    val history = db.payments()
                    val prices = db.priceEvents()
                    _subscriptions.value = plans
                    _payments.value = history
                    _priceEvents.value = prices
                    Result.success(Unit)
                } catch (error: Exception) {
                    if (error is CancellationException) throw error
                    // Debug builds can retain stack traces for troubleshooting.
                    // Never print database exception details in release logcat.
                    if (BuildConfig.DEBUG) Log.e("SubliX", "Local database operation failed", error)
                    Result.failure(error)
                }
            }
        }

    private fun launchOperation(
        operation: () -> Unit,
        onComplete: (Result<Unit>) -> Unit
    ) {
        viewModelScope.launch {
            val result = runDatabaseOperation(operation)
            onComplete(result)
        }
    }

    override fun onCleared() {
        db.close()
        super.onCleared()
    }

    fun reload() {
        launchOperation({}, { result ->
            if (result.isFailure) _errors.tryEmit("Unable to read local subscription data.")
        })
    }

    fun save(item: Subscription) {
        save(item) { result ->
            if (result.isFailure) _errors.tryEmit("Unable to save subscription. No change was confirmed.")
        }
    }

    fun save(item: Subscription, onComplete: (Result<Unit>) -> Unit) =
        launchOperation({ db.save(item) }, onComplete)

    fun delete(id: String) {
        delete(id) { result ->
            if (result.isFailure) _errors.tryEmit("Unable to delete subscription.")
        }
    }

    fun delete(id: String, onComplete: (Result<Unit>) -> Unit) =
        launchOperation({ db.delete(id) }, onComplete)

    fun paid(item: Subscription) {
        paid(item) { result ->
            if (result.isFailure) _errors.tryEmit("Payment was not recorded. Check whether this plan changed.")
        }
    }

    fun paid(item: Subscription, onComplete: (Result<Unit>) -> Unit) =
        launchOperation({ db.markPaid(item, LocalDate.now()) }, onComplete)

    fun importItems(items: List<Subscription>, onComplete: (Result<Int>) -> Unit) {
        launchOperation({ db.importItems(items) }) { result ->
            onComplete(result.map { items.size })
        }
    }
}
