package com.caju.payment_authorizer.domain.service

import com.caju.payment_authorizer.domain.model.Category
import com.caju.payment_authorizer.domain.model.MCC
import com.caju.payment_authorizer.domain.model.Merchant
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class CategoryResolverTest {

    private val categoryResolver = CategoryResolver()

    @Test
    fun `resolve should return correct category based on merchant name`() {
        assertEquals(Category.CASH, categoryResolver.resolve(Merchant("UBER TRIP SAO PAULO"), MCC("1234")))
        assertEquals(Category.MEAL, categoryResolver.resolve(Merchant("UBER EATS SAO PAULO"), MCC("1234")))
        assertEquals(Category.CASH, categoryResolver.resolve(Merchant("PAG*JoseDaSilva"), MCC("1234")))
        assertEquals(Category.CASH, categoryResolver.resolve(Merchant("PICPAY*BILHETEUNICO"), MCC("1234")))
    }

    @Test
    fun `resolve should return correct category based on MCC when no merchant override exists`() {
        assertEquals(Category.FOOD, categoryResolver.resolve(Merchant("SUPERMARKET"), MCC("5411")))
        assertEquals(Category.FOOD, categoryResolver.resolve(Merchant("GROCERY STORE"), MCC("5412")))
        assertEquals(Category.MEAL, categoryResolver.resolve(Merchant("RESTAURANT"), MCC("5811")))
        assertEquals(Category.MEAL, categoryResolver.resolve(Merchant("FAST FOOD"), MCC("5812")))
    }

    @Test
    fun `resolve should return CASH category when no merchant override or MCC match exists`() {
        assertEquals(Category.CASH, categoryResolver.resolve(Merchant("RANDOM STORE"), MCC("9999")))
    }
}
