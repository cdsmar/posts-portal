package com.portal.postsPortal.repository;


import com.portal.postsPortal.model.Contact;
import com.portal.postsPortal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByUser(User user);
    List<Contact> findByContactUser(User contactUser);
    boolean existsByUserAndContactUser(User user, User contactUser);
    Optional<Contact> findByUserAndContactUser(User user, User contactUser);
}

