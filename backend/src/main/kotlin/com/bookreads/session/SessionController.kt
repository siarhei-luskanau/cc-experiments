package com.bookreads.session

import com.bookreads.dto.SessionDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/sessions")
class SessionController(private val sessionService: SessionService) {

    data class StartSessionRequest(val username: String, val bookTitle: String)
    data class EndSessionRequest(val username: String)

    @PostMapping("/start")
    fun startSession(@RequestBody request: StartSessionRequest): ResponseEntity<SessionDto> =
        ResponseEntity.ok(sessionService.startSession(request.username, request.bookTitle))

    @PostMapping("/end")
    fun endSession(@RequestBody request: EndSessionRequest): ResponseEntity<SessionDto> =
        ResponseEntity.ok(sessionService.endSession(request.username))

    @GetMapping("/active/{username}")
    fun getActiveSession(@PathVariable username: String): ResponseEntity<SessionDto> {
        val session = sessionService.getActiveSession(username)
        return if (session != null) ResponseEntity.ok(session) else ResponseEntity.noContent().build()
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(ex: NoSuchElementException): ResponseEntity<Map<String, String>> =
        ResponseEntity.status(404).body(mapOf("error" to (ex.message ?: "Not found")))

    @ExceptionHandler(IllegalStateException::class)
    fun handleConflict(ex: IllegalStateException): ResponseEntity<Map<String, String>> =
        ResponseEntity.status(409).body(mapOf("error" to (ex.message ?: "Conflict")))
}
