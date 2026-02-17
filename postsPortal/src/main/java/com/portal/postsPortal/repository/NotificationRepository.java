package com.portal.postsPortal.repository;

import com.portal.postsPortal.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientUserId(Long recipientUserId);
}