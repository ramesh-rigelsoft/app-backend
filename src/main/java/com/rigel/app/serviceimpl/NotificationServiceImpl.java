package com.rigel.app.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rigel.app.dao.INotificationDao;
import com.rigel.app.model.Notification;
import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.service.INotificationService;

@Service
public class NotificationServiceImpl implements INotificationService {

	@Autowired
	private INotificationDao notificationDao;
	
	@Override
	public Notification saveNotification(Notification notification) {
		return notificationDao.saveNotification(notification);
	}

	@Override
	public List<Notification> findNotification(SearchCriteria search) {
		return notificationDao.findNotification(search);
	}

	@Override
	public int notificationCount(SearchCriteria search) {
		return notificationDao.notificationCount(search);
	}

}
