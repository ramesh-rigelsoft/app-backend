package com.rigel.app.dao;

import java.util.List;

import com.rigel.app.model.Notification;
import com.rigel.app.model.dto.SearchCriteria;

public interface INotificationDao {
	
	public Notification saveNotification(Notification notification);
	public List<Notification> findNotification(SearchCriteria search);
	public int notificationCount(SearchCriteria search);

}
