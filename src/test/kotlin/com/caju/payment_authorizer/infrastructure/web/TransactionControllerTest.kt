package com.caju.payment_authorizer.infrastructure.web

import com.caju.payment_authorizer.application.dto.AuthorizationResponseDTO
import com.caju.payment_authorizer.application.dto.TransactionDTO
import com.caju.payment_authorizer.application.service.TransactionApplicationService
import com.fasterxml.jackson.databind.ObjectMapper
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.math.BigDecimal
import kotlin.test.Test

@WebMvcTest(TransactionController::class)
class TransactionControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var transactionApplicationService: TransactionApplicationService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `authorizeTransaction should return 200 OK with approved response for valid transaction`() {
        val transactionDTO = TransactionDTO("123", BigDecimal("50"), "RESTAURANT", "5811")
        whenever(transactionApplicationService.processTransaction(any())).thenReturn(AuthorizationResponseDTO("00"))

        mockMvc.perform(post("/authorize")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(transactionDTO)))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value("00"))
    }

    @Test
    fun `authorizeTransaction should return 200 OK with declined response for insufficient funds`() {
        val transactionDTO = TransactionDTO("123", BigDecimal("5000"), "RESTAURANT", "5811")
        whenever(transactionApplicationService.processTransaction(any())).thenReturn(AuthorizationResponseDTO("51"))

        mockMvc.perform(post("/authorize")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(transactionDTO)))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value("51"))
    }

    @Test
    fun `authorizeTransaction should return 200 OK with error response for invalid input`() {
        val transactionDTO = TransactionDTO("123", BigDecimal("-50"), "RESTAURANT", "5811")
        whenever(transactionApplicationService.processTransaction(any())).thenThrow(IllegalArgumentException("Invalid amount"))

        mockMvc.perform(post("/authorize")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(transactionDTO)))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value("07"))
    }

    @Test
    fun `authorizeTransaction should return 400 Bad Request for invalid JSON`() {
        mockMvc.perform(post("/authorize")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{invalid json}"))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `authorizeTransaction should return 415 Unsupported Media Type for non-JSON content`() {
        val transactionDTO = TransactionDTO("123", BigDecimal("50"), "RESTAURANT", "5811")

        mockMvc.perform(post("/authorize")
            .contentType(MediaType.TEXT_PLAIN)
            .content(objectMapper.writeValueAsString(transactionDTO)))
            .andExpect(status().isUnsupportedMediaType)
    }
}
