package com.bookreads.leaderboard

import com.bookreads.dto.LeaderboardEntryDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/leaderboard")
class LeaderboardController(private val leaderboardService: LeaderboardService) {

    @GetMapping
    fun getLeaderboard(
        @RequestParam(defaultValue = "alltime") window: String
    ): ResponseEntity<List<LeaderboardEntryDto>> =
        ResponseEntity.ok(leaderboardService.getLeaderboard(window))

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(ex: IllegalArgumentException): ResponseEntity<Map<String, String>> =
        ResponseEntity.badRequest().body(mapOf("error" to (ex.message ?: "Bad request")))
}
