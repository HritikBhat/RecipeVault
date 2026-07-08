package com.hritik.recipevault.ui.screen.premium

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hritik.recipevault.data.billing.BillingHelper
import com.hritik.recipevault.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PremiumViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val billingHelper: BillingHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow<PremiumUiState>(PremiumUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _isPremium = MutableStateFlow(false)
    val isPremium = _isPremium.asStateFlow()

    private val _showConfirmationDialog = MutableStateFlow(false)
    val showConfirmationDialog = _showConfirmationDialog.asStateFlow()

    val currentUserEmail: String? get() = auth.currentUser?.email

    init {
        checkPremiumStatus()
        observeBillingEvents()
    }

    private fun checkPremiumStatus() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid)
            .addSnapshotListener { snapshot, _ ->
                _isPremium.value = snapshot?.getBoolean("isPremium") ?: false
            }
    }

    private fun observeBillingEvents() {
        viewModelScope.launch {
            billingHelper.billingEvent.collect { event ->
                when (event) {
                    is BillingHelper.BillingEvent.Success -> {
                        _uiState.value = PremiumUiState.Success
                    }
                    is BillingHelper.BillingEvent.Error -> {
                        _uiState.value = PremiumUiState.Error(UiText.DynamicString(event.message))
                    }
                }
            }
        }
    }

    fun onPurchaseClick() {
        _showConfirmationDialog.value = true
    }

    fun dismissConfirmationDialog() {
        _showConfirmationDialog.value = false
    }

    fun confirmPurchase(activity: android.app.Activity) {
        dismissConfirmationDialog()
        _uiState.value = PremiumUiState.Loading
        billingHelper.launchBillingFlow(activity)
    }

    fun restorePurchase() {
        _uiState.value = PremiumUiState.Loading
        billingHelper.queryPurchases()
    }

    fun dismissState() {
        _uiState.value = PremiumUiState.Idle
    }
}
