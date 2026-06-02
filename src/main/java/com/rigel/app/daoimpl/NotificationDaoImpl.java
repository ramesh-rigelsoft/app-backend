package com.rigel.app.daoimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.rigel.app.dao.INotificationDao;
import com.rigel.app.model.Notification;
import com.rigel.app.model.dto.SearchCriteria;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class NotificationDaoImpl implements INotificationDao {
	
	@Autowired
	private EntityManager entityManager;

	@Override
	public Notification saveNotification(Notification notification) {
		return entityManager.merge(notification);
	}

	@Override
	public List<Notification> findNotification(SearchCriteria search) {

	 	    return entityManager.createQuery(
	            "from Notification where status=true AND  ownerId = :ownerId",
	            Notification.class)
	        .setParameter("ownerId", search.getUserId())
	        .setFirstResult(search.getStartIndex())  // offset
	        .setMaxResults(search.getMaxRecords())          // limit
	        .getResultList();
	}
	
	@Override
	public int notificationCount(SearchCriteria search) {

	    Long count = entityManager.createQuery(
	            "select count(n) from Notification n " +
	            "where n.status = true seenStatus=false and n.ownerId = :ownerId",
	            Long.class)
	        .setParameter("ownerId", search.getUserId())
	        .getSingleResult();
	    return count.intValue();
	}
}
