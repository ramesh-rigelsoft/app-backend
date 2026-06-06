package com.rigel.app.service;

import java.util.List;

import com.rigel.app.model.Notification;
import com.rigel.app.model.dto.SearchCriteria;

public interface INotificationService {
	
	public Notification saveNotification(Notification notification);
	public List<Notification> findNotification(SearchCriteria search);
	public int notificationCount(SearchCriteria search);
	public int unSeenNotificationCount(SearchCriteria search);
	public int notificationUpdate(SearchCriteria search);


}
