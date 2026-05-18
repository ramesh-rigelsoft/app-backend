package com.rigel.app.daoimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.rigel.app.dao.IVendorPaymentDao;
import com.rigel.app.model.VendorPayments;
import com.rigel.app.model.dto.SearchCriteria;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class VendorPaymentDaoImp implements IVendorPaymentDao {

	@Autowired
	private EntityManager entityManager;

	@Override
	public VendorPayments saveVendor(VendorPayments vendorPayments) {
		return entityManager.merge(vendorPayments);
	}

	@Override
	public VendorPayments update(VendorPayments vendorPayments) {
		return entityManager.merge(vendorPayments);
	}

	@Override
	public List<Object> vendorList(SearchCriteria criteria) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
