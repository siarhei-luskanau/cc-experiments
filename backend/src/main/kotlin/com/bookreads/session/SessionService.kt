package com.bookreads.session

import com.bookreads.dto.SessionDto
import com.bookreads.dto.SessionSyncRequest
import com.bookreads.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class SessionService(
    private val sessionRepository: SessionRepository,
    private val userRepository: UserRepository,
) {
    @Transactional
    fun syncSession(request: SessionSyncRequest): SessionDto {
        val user =
            userRepository
                .findByUsername(request.username)
                .orElseThrow { NoSuchElementException("User not found: ${request.username}") }
        val existing = sessionRepository.findByClientId(request.clientId)
        val session =
            if (existing.isPresent) {
                val s = existing.get()
                s.endedAt = request.endedAt?.let { Instant.parse(it) }
                s.durationSec = request.durationSec
                sessionRepository.save(s)
            } else {
                sessionRepository.save(
                    ReadingSession(
                        clientId = request.clientId,
                        user = user,
                        bookTitle = request.bookTitle,
                        startedAt = Instant.parse(request.startedAt),
                        endedAt = request.endedAt?.let { Instant.parse(it) },
                        durationSec = request.durationSec,
                    ),
                )
            }
        return session.toDto()
    }

    @Transactional(readOnly = true)
    fun getSession(clientId: String): SessionDto? =
        sessionRepository
            .findByClientId(clientId)
            .map { it.toDto() }
            .orElse(null)

    private fun ReadingSession.toDto() =
        SessionDto(
            clientId = clientId,
            username = user.username,
            bookTitle = bookTitle,
            startedAt = startedAt.toString(),
            endedAt = endedAt?.toString(),
            durationSec = durationSec,
        )
}
