package com.caju.payment_authorizer.domain.model

sealed class AuthorizationResult {
    data class Approved(val updatedAccount: Account) : AuthorizationResult()
    sealed class Declined : AuthorizationResult() {
        object InsufficientFunds : Declined()
    }
}