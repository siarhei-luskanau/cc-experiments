package com.bookreads.session

import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface SessionRepository : JpaRepository<ReadingSession, Long> {
    fun findByClientId(clientId: String): Optional<ReadingSession>
}
