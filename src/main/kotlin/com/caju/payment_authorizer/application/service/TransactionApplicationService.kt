package com.caju.payment_authorizer.application.service

import com.caju.payment_authorizer.application.dto.AuthorizationResponseDTO
import com.caju.payment_authorizer.application.dto.TransactionDTO
import com.caju.payment_authorizer.domain.model.*
import com.caju.payment_authorizer.domain.service.AuthorizationService
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class TransactionApplicationService(private val authorizationService: AuthorizationService) {

    private val accounts = mutableMapOf(
        "123" to Account("123", mapOf(
            Category.FOOD to Money(BigDecimal("1000")),
            Category.MEAL to Money(BigDecimal("1000")),
            Category.CASH to Money(BigDecimal("1000"))
        ))
    )

    fun processTransaction(transactionDTO: TransactionDTO): AuthorizationResponseDTO {
        val account = accounts[transactionDTO.accountId] ?: throw IllegalArgumentException("Account not found")
        val transaction = Transaction(
            accountId = transactionDTO.accountId,
            amount = Money(transactionDTO.amount),
            merchant = Merchant(transactionDTO.merchant),
            mcc = MCC(transactionDTO.mcc)
        )

        return when (val result = authorizationService.authorize(transaction, account)) {
            is AuthorizationResult.Approved -> {
                accounts[transactionDTO.accountId] = result.updatedAccount
                AuthorizationResponseDTO("00")
            }
            is AuthorizationResult.Declined.InsufficientFunds -> AuthorizationResponseDTO("51")
            is AuthorizationResult.Declined.PaymentNotMade -> AuthorizationResponseDTO("51")
        }
    }
}