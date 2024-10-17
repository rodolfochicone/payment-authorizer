package com.caju.payment_authorizer.domain.model

import java.math.BigDecimal

data class Money(val amount: BigDecimal) {
    operator fun compareTo(other: Money) = this.amount.compareTo(other.amount)
    operator fun minus(other: Money) = Money(this.amount - other.amount)
}