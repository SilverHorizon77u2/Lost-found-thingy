package org.example.lfrs_group_4_oop.entity;

import java.time.LocalDateTime;

/**
 * Links an Item to a Claimant.
 */
public class Claim extends BaseEntity {
    private Integer itemId;
    private Integer claimantId;
    private LocalDateTime claimDate;

    public Claim() {
    }

    public Claim(Integer id, Integer itemId, Integer claimantId, LocalDateTime claimDate) {
        this.id = id;
        this.itemId = itemId;
        this.claimantId = claimantId;
        this.claimDate = claimDate;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getClaimantId() {
        return claimantId;
    }

    public void setClaimantId(Integer claimantId) {
        this.claimantId = claimantId;
    }

    public LocalDateTime getClaimDate() {
        return claimDate;
    }

    public void setClaimDate(LocalDateTime claimDate) {
        this.claimDate = claimDate;
    }

    @Override
    public String toString() {
        return "Claim{" +
                "id=" + id +
                ", itemId=" + itemId +
                ", claimantId=" + claimantId +
                ", claimDate=" + claimDate +
                '}';
    }
}
