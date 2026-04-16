package com.rigel.app.daoimpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.rigel.app.dao.IUserDao;
import com.rigel.app.model.LoginActivity;
import com.rigel.app.model.User;

@Repository
@Transactional
public class UserDaoimpl implements IUserDao {
	
	@Autowired
	EntityManager entityManager;

	@Override
	public User saveUser(User user) {
		return entityManager.merge(user);
	}

	@Override
	public User findUserById(int id) {
		return entityManager.find(User.class, id);
	}

	@Override
	public User findUserByEmailId(String email) {
		try {
			User user=(User) entityManager.createQuery("from User where email_id='"+email+"'").getSingleResult();
			return user;
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}

//	@Override
//	public LoginActivity findLoginActivity(String username) {
//		try {
//			LoginActivity login=(LoginActivity) entityManager.createQuery("from LoginActivity").getSingleResult();
//			return login;
//		} catch (NoResultException e) {
//			return null;
//		} catch (Exception e) {
//			return null;
//		}
//	}

	@Override
	public LoginActivity findLoginActivity(String username) {
	    try {
	        LoginActivity login;

	        if (username == null || username.trim().isEmpty()) {
	            // 🔹 username null → first record
	            login = (LoginActivity) entityManager
	                    .createQuery("from LoginActivity")
	                    .setMaxResults(1)   // first record only
	                    .getSingleResult();
	        } else {
	            // 🔹 username provided → filter by emailId
	            login = (LoginActivity) entityManager
	                    .createQuery("from LoginActivity WHERE emailIds = :email")
	                    .setParameter("email", username)
	                    .getSingleResult();
	        }

	        return login;

	    } catch (NoResultException e) {
	        return null;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}
	
}
