package com.caju.payment_authorizer.domain.model

data class Account(
    val id: String,
    val balances: Map<Category, Money>
) {
    fun canAuthorize(amount: Money, category: Category): Boolean =
        balances[category]?.let { it >= amount } ?: false

    fun debit(amount: Money, category: Category): Account {
        require(canAuthorize(amount, category)) { "Insufficient funds" }
        return copy(balances = balances.mapValues { (key, value) ->
            if (key == category) value - amount else value
        })
    }
}
