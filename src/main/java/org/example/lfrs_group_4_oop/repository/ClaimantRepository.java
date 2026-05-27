package org.example.lfrs_group_4_oop.repository;

import org.example.lfrs_group_4_oop.entity.Claimant;

/**
 * Contract for Claimant-related data operations.
 */
public interface ClaimantRepository extends BaseRepository<Claimant> {
    Claimant findByIdNumber(String idNumber);
}
