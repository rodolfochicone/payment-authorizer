package com.caju.payment_authorizer.domain.service

import com.caju.payment_authorizer.domain.model.Account
import com.caju.payment_authorizer.domain.model.AuthorizationResult
import com.caju.payment_authorizer.domain.model.Category
import com.caju.payment_authorizer.domain.model.Transaction
import org.springframework.stereotype.Service

@Service
class AuthorizationService(private val categoryResolver: CategoryResolver) {
    fun authorize(transaction: Transaction, account: Account): AuthorizationResult {
        val category = categoryResolver.resolve(transaction.merchant, transaction.mcc)
        return when {
            account.canAuthorize(transaction.amount, category) -> {
                val updatedAccount = account.debit(transaction.amount, category)
                AuthorizationResult.Approved(updatedAccount)
            }
            category != Category.CASH && account.canAuthorize(transaction.amount, Category.CASH) -> {
                val updatedAccount = account.debit(transaction.amount, Category.CASH)
                AuthorizationResult.Approved(updatedAccount)
            }
            else -> AuthorizationResult.Declined.InsufficientFunds
        }
    }
}