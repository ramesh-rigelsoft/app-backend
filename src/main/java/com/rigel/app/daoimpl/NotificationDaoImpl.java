package com.rigel.app.daoimpl;

import java.time.LocalDateTime;
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
	            "FROM Notification n " +
	            "WHERE n.status = true AND n.ownerId = :ownerId " +
	            "ORDER BY n.seenStatus ASC, n.createdAt DESC",
	            Notification.class)
	        .setParameter("ownerId", search.getUserId())
	        .setFirstResult(search.getStartIndex())
	        .setMaxResults(search.getMaxRecords())
	        .getResultList();
	}
	
	@Override
	public int unSeenNotificationCount(SearchCriteria search) {

	    Long count = entityManager.createQuery(
	            "select count(n) from Notification n " +
	            "where n.status = true AND seenStatus=false AND n.ownerId = :ownerId",
	            Long.class)
	        .setParameter("ownerId", search.getUserId())
	        .getSingleResult();
	    return count.intValue();
	}
	
	@Override
	public int notificationCount(SearchCriteria search) {

	    Long count = entityManager.createQuery(
	            "SELECT COUNT(n) FROM Notification n " +
	            "WHERE n.status = true AND n.ownerId = :ownerId",
	            Long.class)
	        .setParameter("ownerId", search.getUserId())
	        .getSingleResult();

	    return count.intValue();
	}

	@Override
	public int notificationUpdate(SearchCriteria search) {

	    int updatedCount = entityManager.createQuery(
	            "UPDATE Notification n " +
	            "SET n.seenStatus = true, n.seenAt = :seenAt " +
	            "WHERE n.id = :id")
	        .setParameter("id", search.getId())
	        .setParameter("seenAt", LocalDateTime.now()) // ✅ current time
	        .executeUpdate();

	    return updatedCount;
	}
}
