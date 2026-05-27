package org.example.lfrs_group_4_oop.entity;

/**
 * Represents a person making a claim for an item.
 */
public class Claimant extends BaseEntity {
    private String name;
    private String idNumber;

    public Claimant() {
    }

    public Claimant(Integer id, String name, String idNumber) {
        this.id = id;
        this.name = name;
        this.idNumber = idNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    @Override
    public String toString() {
        return "Claimant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", idNumber='" + idNumber + '\'' +
                '}';
    }
}
