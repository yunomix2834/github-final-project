package com.source.animeh.repository.notification;

import com.source.animeh.entity.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository
    extends JpaRepository<Notification, String> {

}
