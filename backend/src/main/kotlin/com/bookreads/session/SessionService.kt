package com.bookreads.session

import com.bookreads.dto.SessionDto
import com.bookreads.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class SessionService(
    private val sessionRepository: SessionRepository,
    private val userRepository: UserRepository,
    private val entityManager: jakarta.persistence.EntityManager,
) {
    @Transactional
    fun startSession(
        username: String,
        bookTitle: String,
    ): SessionDto {
        val user =
            userRepository
                .findByUsername(username)
                .orElseThrow { NoSuchElementException("User not found: $username") }
        sessionRepository.findByUserIdAndEndedAtIsNull(user.id).ifPresent {
            throw IllegalStateException("User $username already has an active session")
        }
        val session = sessionRepository.save(ReadingSession(user = user, bookTitle = bookTitle))
        sessionRepository.flush()
        entityManager.refresh(session)
        return session.toDto()
    }

    @Transactional
    fun endSession(username: String): SessionDto {
        val user =
            userRepository
                .findByUsername(username)
                .orElseThrow { NoSuchElementException("User not found: $username") }
        val session =
            sessionRepository
                .findByUserIdAndEndedAtIsNull(user.id)
                .orElseThrow { NoSuchElementException("No active session for user: $username") }
        session.endedAt = Instant.now()
        sessionRepository.save(session)
        sessionRepository.flush()
        entityManager.refresh(session)
        return session.toDto()
    }

    @Transactional(readOnly = true)
    fun getActiveSession(username: String): SessionDto? {
        val user =
            userRepository
                .findByUsername(username)
                .orElseThrow { NoSuchElementException("User not found: $username") }
        return sessionRepository
            .findByUserIdAndEndedAtIsNull(user.id)
            .map { it.toDto() }
            .orElse(null)
    }

    private fun ReadingSession.toDto() =
        SessionDto(
            id = id,
            username = user.username,
            bookTitle = bookTitle,
            startedAt = startedAt.toString(),
            endedAt = endedAt?.toString(),
            durationSec = durationSec,
        )
}
