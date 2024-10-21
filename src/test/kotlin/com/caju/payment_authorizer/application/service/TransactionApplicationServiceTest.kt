package com.caju.payment_authorizer.application.service

import com.caju.payment_authorizer.application.dto.AuthorizationResponseDTO
import com.caju.payment_authorizer.application.dto.TransactionDTO
import com.caju.payment_authorizer.domain.model.Account
import com.caju.payment_authorizer.domain.model.AuthorizationResult
import com.caju.payment_authorizer.domain.model.Category
import com.caju.payment_authorizer.domain.model.Money
import com.caju.payment_authorizer.domain.service.AuthorizationService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import kotlin.test.Test

class TransactionApplicationServiceTest {

    private val authorizationService: AuthorizationService = mock()
    private val transactionApplicationService = TransactionApplicationService(authorizationService)

    @Test
    fun `processTransaction should return approved response when transaction is authorized`() {
        val transactionDTO = TransactionDTO("123", BigDecimal("50"), "RESTAURANT", "5811")
        val updatedAccount = Account("123", mapOf(Category.MEAL to Money(BigDecimal("50"))))

        whenever(authorizationService.authorize(any(), any())).thenReturn(AuthorizationResult.Approved(updatedAccount))

        val result = transactionApplicationService.processTransaction(transactionDTO)

        assertEquals(AuthorizationResponseDTO("00"), result)
    }

    @Test
    fun `processTransaction should return declined response when transaction is not authorized`() {
        val transactionDTO = TransactionDTO("123", BigDecimal("200"), "RESTAURANT", "5811")

        whenever(authorizationService.authorize(any(), any())).thenReturn(AuthorizationResult.Declined.InsufficientFunds)

        val result = transactionApplicationService.processTransaction(transactionDTO)

        assertEquals(AuthorizationResponseDTO("51"), result)
    }

    @Test
    fun `processTransaction should throw exception when account is not found`() {
        val transactionDTO = TransactionDTO("999", BigDecimal("50"), "RESTAURANT", "5811")

        assertThrows<IllegalArgumentException> {
            transactionApplicationService.processTransaction(transactionDTO)
        }
    }
}
