package com.creed.usecase

import com.creed.domain.models.Arena
import com.creed.domain.repository.ArenaRepository

class FetchArenaDetailUseCase(
    private val arenaRepository: ArenaRepository
) {
    suspend fun execute(arenaId: String, lat: Double? = null, lng: Double? = null): Result<Arena?> {
        return try {
            arenaRepository.getArenaDetail(arenaId, lat, lng)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
