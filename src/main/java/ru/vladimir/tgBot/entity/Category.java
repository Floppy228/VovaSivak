package ru.vladimir.tgBot.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    public Category() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }
}
