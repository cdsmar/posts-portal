package com.portal.postsPortal.repository;


import com.portal.postsPortal.model.Post;
import com.portal.postsPortal.model.PostMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostMessageRepository extends JpaRepository<PostMessage, Long> {
    List<PostMessage> findByPostOrderByTimestampAsc(Post post);  // Fetch messages by post
}

