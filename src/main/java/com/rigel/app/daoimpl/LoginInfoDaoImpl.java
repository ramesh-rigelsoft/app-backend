package com.rigel.app.daoimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.rigel.app.dao.ILoginInfoDao;
import com.rigel.app.model.LoginActivity;
import com.rigel.app.model.dto.SearchCriteria;

import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Transactional
@Repository
public class LoginInfoDaoImpl implements ILoginInfoDao {

	@Autowired
	private EntityManager entityManager;

	@Override
	public List<LoginActivity> saveLoginActivity(List<LoginActivity> loginActivity) {
		loginActivity.forEach(l -> {
			entityManager.merge(l);
		});
		return null;
	}

	@Override
	public LoginActivity updateLoginActivity(LoginActivity loginActivity) {
		return entityManager.merge(loginActivity);
	}

	@Override
	public List<LoginActivity> searchSalesInfo(SearchCriteria criteria) {
		return null;// entityManager.createQuery("from LoginActivity").getFirstResult();
	}

	@Override
	public LoginActivity findLoginActivityByUsername(String username) {
		try {
			return entityManager.createQuery("FROM LoginActivity WHERE emailId = :username OR mobileNumber = :username",
					LoginActivity.class).setParameter("username", username).getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

}
