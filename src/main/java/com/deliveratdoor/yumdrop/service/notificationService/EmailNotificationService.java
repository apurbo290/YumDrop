package com.deliveratdoor.yumdrop.service.notificationService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailNotificationService extends NotificationService {

    @Override
    public void notifyByEmail(String userId) {
        log.info("Email notification sent to : {}", userId);
    }
}
