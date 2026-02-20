package com.bookreads.session

import com.bookreads.dto.SessionDto
import com.bookreads.dto.SessionSyncRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/sessions")
class SessionController(
    private val sessionService: SessionService,
) {
    @PostMapping("/sync")
    fun syncSession(
        @RequestBody request: SessionSyncRequest,
    ): ResponseEntity<SessionDto> = ResponseEntity.ok(sessionService.syncSession(request))

    @GetMapping("/{clientId}")
    fun getSession(
        @PathVariable clientId: String,
    ): ResponseEntity<SessionDto> {
        val session = sessionService.getSession(clientId)
        return if (session != null) ResponseEntity.ok(session) else ResponseEntity.notFound().build()
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(ex: NoSuchElementException): ResponseEntity<Map<String, String>> =
        ResponseEntity.status(404).body(mapOf("error" to (ex.message ?: "Not found")))
}
