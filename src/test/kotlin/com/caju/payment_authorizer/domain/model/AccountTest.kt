package com.caju.payment_authorizer.domain.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import kotlin.test.Test

class AccountTest {

    @Test
    fun `canAuthorize should return true when sufficient funds are available`() {
        val account = Account("123", mapOf(
            Category.FOOD to Money(BigDecimal("100")),
            Category.MEAL to Money(BigDecimal("50")),
            Category.CASH to Money(BigDecimal("200"))
        ))

        assertTrue(account.canAuthorize(Money(BigDecimal("50")), Category.FOOD))
        assertTrue(account.canAuthorize(Money(BigDecimal("50")), Category.MEAL))
        assertTrue(account.canAuthorize(Money(BigDecimal("200")), Category.CASH))
    }

    @Test
    fun `canAuthorize should return false when insufficient funds are available`() {
        val account = Account("123", mapOf(
            Category.FOOD to Money(BigDecimal("100")),
            Category.MEAL to Money(BigDecimal("50")),
            Category.CASH to Money(BigDecimal("200"))
        ))

        assertFalse(account.canAuthorize(Money(BigDecimal("101")), Category.FOOD))
        assertFalse(account.canAuthorize(Money(BigDecimal("51")), Category.MEAL))
        assertFalse(account.canAuthorize(Money(BigDecimal("201")), Category.CASH))
    }

    @Test
    fun `debit should reduce balance when sufficient funds are available`() {
        val account = Account("123", mapOf(
            Category.FOOD to Money(BigDecimal("100")),
            Category.MEAL to Money(BigDecimal("50")),
            Category.CASH to Money(BigDecimal("200"))
        ))

        val updatedAccount = account.debit(Money(BigDecimal("50")), Category.FOOD)

        assertEquals(Money(BigDecimal("50")), updatedAccount.balances[Category.FOOD])
        assertEquals(Money(BigDecimal("50")), updatedAccount.balances[Category.MEAL])
        assertEquals(Money(BigDecimal("200")), updatedAccount.balances[Category.CASH])
    }

    @Test
    fun `debit should throw exception when insufficient funds are available`() {
        val account = Account("123", mapOf(
            Category.FOOD to Money(BigDecimal("100"))
        ))

        assertThrows<IllegalArgumentException> {
            account.debit(Money(BigDecimal("101")), Category.FOOD)
        }
    }
}