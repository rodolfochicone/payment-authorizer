package com.caju.payment_authorizer.infrastructure.web

import com.caju.payment_authorizer.application.dto.AuthorizationResponseDTO
import com.caju.payment_authorizer.application.dto.TransactionDTO
import com.caju.payment_authorizer.application.service.TransactionApplicationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TransactionController(private val transactionApplicationService: TransactionApplicationService) {

    @PostMapping("/authorize")
    fun authorizeTransaction(@RequestBody transactionDTO: TransactionDTO): ResponseEntity<AuthorizationResponseDTO> {
        return try {
            val response = transactionApplicationService.processTransaction(transactionDTO)
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity.ok(AuthorizationResponseDTO("07"))
        }
    }
}