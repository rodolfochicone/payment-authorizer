package com.caju.payment_authorizer.domain.service

import com.caju.payment_authorizer.domain.model.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.math.BigDecimal
import java.time.Duration
import kotlin.test.Test

class AuthorizationServiceTest {

    private val categoryResolver: CategoryResolver = mock()
    private val redisTemplate: RedisTemplate<String, String> = mock()
    private val valueOperations: ValueOperations<String, String> = mock()
    private val authorizationService = AuthorizationService(categoryResolver, redisTemplate)

    private val lockTimeoutMillis = 100L

    @Test
    fun `authorize should approve transaction when sufficient funds are available in primary category`() {
        val transaction = Transaction(
            accountId = "123",
            amount = Money(BigDecimal("50")),
            merchant = Merchant("RESTAURANT"),
            mcc = MCC("5811")
        )
        val account = Account("123", mapOf(
            Category.MEAL to Money(BigDecimal("100")),
            Category.CASH to Money(BigDecimal("100"))
        ))

        val lockKey = "transaction_lock:${account::id}"

        whenever(categoryResolver.resolve(transaction.merchant, transaction.mcc)).thenReturn(Category.MEAL)
        whenever(redisTemplate.opsForValue()).thenReturn(valueOperations)
        whenever(valueOperations.setIfAbsent(lockKey, "locked", Duration.ofMillis(lockTimeoutMillis)))
            .thenReturn(true)

        val result = authorizationService.authorize(transaction, account)

        assertTrue(result is AuthorizationResult.Approved)
        val approvedResult = result as AuthorizationResult.Approved
        assertEquals(Money(BigDecimal("50")), approvedResult.updatedAccount.balances[Category.MEAL])
    }

    @Test
    fun `authorize should approve transaction using CASH when insufficient funds in primary category`() {
        val transaction = Transaction(
            accountId = "123",
            amount = Money(BigDecimal("60")),
            merchant = Merchant("RESTAURANT"),
            mcc = MCC("5811")
        )
        val account = Account("123", mapOf(
            Category.MEAL to Money(BigDecimal("50")),
            Category.CASH to Money(BigDecimal("100"))
        ))
        val lockKey = "transaction_lock:${account::id}"

        whenever(categoryResolver.resolve(transaction.merchant, transaction.mcc)).thenReturn(Category.MEAL)
        whenever(redisTemplate.opsForValue()).thenReturn(valueOperations)
        whenever(valueOperations.setIfAbsent(lockKey, "locked", Duration.ofMillis(lockTimeoutMillis)))
            .thenReturn(true)

        val result = authorizationService.authorize(transaction, account)

        assertTrue(result is AuthorizationResult.Approved)
        val approvedResult = result as AuthorizationResult.Approved
        assertEquals(Money(BigDecimal("50")), approvedResult.updatedAccount.balances[Category.MEAL])
        assertEquals(Money(BigDecimal("40")), approvedResult.updatedAccount.balances[Category.CASH])
    }

    @Test
    fun `authorize should decline transaction when payment not made in both primary and CASH categories`() {
        val transaction = Transaction(
            accountId = "123",
            amount = Money(BigDecimal("200")),
            merchant = Merchant("RESTAURANT"),
            mcc = MCC("5811")
        )
        val account = Account("123", mapOf(
            Category.MEAL to Money(BigDecimal("50")),
            Category.CASH to Money(BigDecimal("100"))
        ))
        val lockKey = "transaction_lock:${account::id}"

        whenever(categoryResolver.resolve(transaction.merchant, transaction.mcc)).thenReturn(Category.MEAL)
        whenever(redisTemplate.opsForValue()).thenReturn(valueOperations)
        whenever(valueOperations.setIfAbsent(lockKey, "locked", Duration.ofMillis(lockTimeoutMillis)))
            .thenReturn(false)

        val result = authorizationService.authorize(transaction, account)

        assertTrue(result is AuthorizationResult.Declined.PaymentNotMade)
    }

    @Test
    fun `authorize should decline transaction when insufficient funds in both primary and CASH categories`() {
        val transaction = Transaction(
            accountId = "123",
            amount = Money(BigDecimal("200")),
            merchant = Merchant("RESTAURANT"),
            mcc = MCC("5811")
        )
        val account = Account("123", mapOf(
            Category.MEAL to Money(BigDecimal("50")),
            Category.CASH to Money(BigDecimal("100"))
        ))
        val lockKey = "transaction_lock:${account::id}"

        whenever(categoryResolver.resolve(transaction.merchant, transaction.mcc)).thenReturn(Category.MEAL)
        whenever(redisTemplate.opsForValue()).thenReturn(valueOperations)
        whenever(valueOperations.setIfAbsent(lockKey, "locked", Duration.ofMillis(lockTimeoutMillis)))
            .thenReturn(true)

        val result = authorizationService.authorize(transaction, account)

        assertTrue(result is AuthorizationResult.Declined.InsufficientFunds)
    }
}