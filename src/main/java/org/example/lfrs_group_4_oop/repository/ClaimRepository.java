package org.example.lfrs_group_4_oop.repository;

import org.example.lfrs_group_4_oop.entity.Claim;
import java.util.List;

/**
 * Contract for Claim-related data operations.
 */
public interface ClaimRepository extends BaseRepository<Claim> {
    List<Claim> findByItemId(Integer itemId);
}
