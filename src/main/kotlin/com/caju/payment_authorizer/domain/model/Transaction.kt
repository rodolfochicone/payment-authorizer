package com.caju.payment_authorizer.domain.model

import java.util.UUID

data class Transaction(
    val id: UUID = UUID.randomUUID(),
    val accountId: String,
    val amount: Money,
    val merchant: Merchant,
    val mcc: MCC
)