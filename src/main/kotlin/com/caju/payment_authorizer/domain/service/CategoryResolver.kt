package com.caju.payment_authorizer.domain.service

import com.caju.payment_authorizer.domain.model.Category
import com.caju.payment_authorizer.domain.model.MCC
import com.caju.payment_authorizer.domain.model.Merchant
import org.springframework.stereotype.Service


@Service
class CategoryResolver {
    private val merchantCategoryOverrides = mapOf(
        "UBER TRIP" to Category.CASH,
        "UBER EATS" to Category.MEAL,
        "PAG*" to Category.CASH,
        "PICPAY*BILHETEUNICO" to Category.CASH
    )

    private val mccCategoryMap = mapOf(
        "5411" to Category.FOOD,
        "5412" to Category.FOOD,
        "5811" to Category.MEAL,
        "5812" to Category.MEAL
    )

    fun resolve(merchant: Merchant, mcc: MCC): Category {
        return merchantCategoryOverrides.entries
            .find { merchant.name.startsWith(it.key) }
            ?.value
            ?: mccCategoryMap[mcc.code]
            ?: Category.CASH
    }
}