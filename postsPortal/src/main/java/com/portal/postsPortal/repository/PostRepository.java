package com.portal.postsPortal.repository;

import com.portal.postsPortal.model.Message;
import com.portal.postsPortal.model.Post;
import com.portal.postsPortal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUser(User user);
    @Query("SELECT p FROM Post p ORDER BY p.timestamp ASC")
    List<Post> findAllPostsOrderedByTimestampAsc();

    @Query("SELECT p FROM Post p ORDER BY p.timestamp DESC")
    List<Post> findAllPostsOrderedByTimestampDesc();

    List<Post> findByCategory(String category);
    List<Post> findByUserNot(User user);
    @Query("SELECT p FROM Post p WHERE (p.title LIKE %:query% OR p.description LIKE %:query% OR p.category LIKE %:query%) AND p.user != :user")
    List<Post> searchPostsExcludingUser(String query, User user);

}
