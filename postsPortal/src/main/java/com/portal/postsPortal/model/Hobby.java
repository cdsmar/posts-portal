package com.portal.postsPortal.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import java.util.Set;

@Entity
public class Hobby {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // Default constructor (required by JPA)
    public Hobby() {}

    // Constructor with name
    public Hobby(String name) {
        this.name = name;
    }

    // Optional constructor with id (if you want)
    public Hobby(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters and setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
