package com.portal.postsPortal.repository;

import com.portal.postsPortal.model.Conversation;
import com.portal.postsPortal.model.Message;
import com.portal.postsPortal.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByPostOrderByTimestampDesc(Post post);
    List<Message> findByConversation(Conversation conversation);
    List<Message> findByConversationOrderByTimestampAsc(Conversation conversation);
}