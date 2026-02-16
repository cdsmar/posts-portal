package com.portal.postsPortal.repository;

import com.portal.postsPortal.model.Conversation;
import com.portal.postsPortal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    @Query("SELECT c FROM Conversation c WHERE " +
            "(c.user1 = :userA AND c.user2 = :userB) OR " +
            "(c.user1 = :userB AND c.user2 = :userA)")
    Optional<Conversation> findConversationBetweenUsers(
            @Param("userA") User userA,
            @Param("userB") User userB);

}

