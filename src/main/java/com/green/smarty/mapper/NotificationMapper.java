package com.green.smarty.mapper;

import com.green.smarty.dto.NotificationDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NotificationMapper {
    int insertByNotificationId(NotificationDTO notificationDTO);
}
