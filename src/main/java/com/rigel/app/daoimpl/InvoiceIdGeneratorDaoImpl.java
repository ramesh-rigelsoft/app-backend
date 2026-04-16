package com.rigel.app.daoimpl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.rigel.app.dao.IInvoiceIdGeneratorDao;
import com.rigel.app.model.Invoice;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class InvoiceIdGeneratorDaoImpl implements IInvoiceIdGeneratorDao {
	
	@Autowired
	private EntityManager entityManager;

	@Override
	public synchronized String generateInvoiceId(String formate_name) {

		String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        String sql = "SELECT last_number FROM invoice WHERE formate_name ="+formate_name+"";

        Invoice invoice = ((Invoice) entityManager.createNativeQuery(sql).getSingleResult());
        Long lastNumber = invoice.getLastNumber() + 1l;
        int zeroSize=8-String.valueOf(lastNumber).length();
        
        String seqFormatted = invoice.getFormate()+String.format("%0" + zeroSize + "d", lastNumber);
        

        return today + "-" + seqFormatted;
    }

}
