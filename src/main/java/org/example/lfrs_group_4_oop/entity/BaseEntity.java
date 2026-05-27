package org.example.lfrs_group_4_oop.entity;

/*
* This code reduces the number of queries into accessing, modifying, updating,
* and deleting with the entity "id" in the schemas.
*
* */
public abstract class BaseEntity {
    protected Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
