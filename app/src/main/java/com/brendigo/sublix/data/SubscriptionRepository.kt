package com.brendigo.sublix.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class SubscriptionViewModel(app: Application): AndroidViewModel(app) {
    private val db = SubscriptionsDb(app)
    private val _subscriptions=MutableStateFlow<List<Subscription>>(emptyList())
    val subscriptions=_subscriptions.asStateFlow()
    private val _payments=MutableStateFlow<List<PaidRecord>>(emptyList())
    val payments=_payments.asStateFlow()
    private val _priceEvents=MutableStateFlow<List<PriceEvent>>(emptyList())
    val priceEvents=_priceEvents.asStateFlow()
    init { reload() }
    fun reload() { viewModelScope.launch { withContext(Dispatchers.IO) { _subscriptions.value=db.subscriptions(); _payments.value=db.payments(); _priceEvents.value=db.priceEvents() } } }
    fun save(item: Subscription) { viewModelScope.launch(Dispatchers.IO) { db.save(item); _subscriptions.value=db.subscriptions(); _priceEvents.value=db.priceEvents() } }
    fun delete(id: String) { viewModelScope.launch(Dispatchers.IO) { db.delete(id); _subscriptions.value=db.subscriptions() } }
    fun paid(item: Subscription) { viewModelScope.launch(Dispatchers.IO) { db.markPaid(item, LocalDate.now()); _subscriptions.value=db.subscriptions(); _payments.value=db.payments() } }
    fun importItems(items: List<Subscription>) { viewModelScope.launch(Dispatchers.IO) { items.forEach { db.save(it) }; _subscriptions.value=db.subscriptions(); _priceEvents.value=db.priceEvents() } }
}
