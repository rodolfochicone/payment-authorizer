package com.caju.payment_authorizer.application.dto

import java.math.BigDecimal

data class TransactionDTO(
    val accountId: String,
    val amount: BigDecimal,
    val merchant: String,
    val mcc: String
)