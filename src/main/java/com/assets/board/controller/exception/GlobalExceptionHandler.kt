package com.assets.board.controller.exception

import com.assets.board.model.error.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFoundException(
        ex: ResourceNotFoundException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse?> {
        val errorResponse = ErrorResponse(ex.message, request.getDescription(false))
        return ResponseEntity<ErrorResponse?>(errorResponse, HttpStatus.NOT_FOUND)
    }

    // Handle a specific exception
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<String?> {
        return ResponseEntity<String?>(ex.message, HttpStatus.BAD_REQUEST)
    }

    // Handle a general exception
    @ExceptionHandler(Exception::class)
    fun handleGeneralException(ex: Exception): ResponseEntity<String?> {
        return ResponseEntity<String?>("An unexpected error occurred: " + ex.message, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
