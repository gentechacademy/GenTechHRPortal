package com.gentech.hrportal.repository;

import com.gentech.hrportal.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<Notification> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, Notification.NotificationStatus status);
    
    long countByUserIdAndStatus(Long userId, Notification.NotificationStatus status);
    
    List<Notification> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);
}
