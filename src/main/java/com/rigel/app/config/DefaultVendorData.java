//package com.rigel.app.config;
//
//import java.time.LocalDateTime;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.rigel.app.dao.ISupplierDao;
//import com.rigel.app.model.User;
//import com.rigel.app.model.Vendors;
//import com.rigel.app.model.dto.SearchCriteria;
//import com.rigel.app.model.dto.SupplierCreteria;
//import com.rigel.app.model.dto.VendorsDTO;
//
//import jakarta.persistence.EntityManager;
//import jakarta.servlet.http.HttpSession;
//
//@Configuration
//public class DefaultVendorData {
//
//	@Autowired
//	private ISupplierDao supplierDao;
//	
//	@Autowired
//	private ObjectMapper objectMapper;
//	
//	@Autowired
//	private HttpSession session;
//	
//	
//    @Bean
//    CommandLineRunner loadDefaultVendors() {
//        return args -> {
//        	User user=null;
////            Vendors venderData=supplierDao.searchSupplier(SupplierCreteria.builder().userId(user.getId()).pan(user.getPanNumber()).build()).stream().findFirst().orElse(null);
////            // check if already exists
////            if (venderData==null) {
////
////                Vendors vendor = new Vendors();
////                vendor.setcompanyName(user.getCompanyName());
////                vendor.setGstNumber(user.getGstNumber()==null?"Null":user.getGstNumber());
////                vendor.setPanNumber(user.getPanNumber());
////                vendor.setPinCode(user.getPincode());
////                vendor.setEmail(user.getEmail_id());
////                vendor.setPhone(user.getMobile_no());
////                vendor.setAddress(user.getAddressLine1());
////                vendor.setStatus("active");
//////                vendor.setDistrict(user.g);
////                vendor.setState(user.getState());
//////                vendor.setStateCode(user.getS);
////                vendor.setOwnerId(user.getId());
////                vendor.setCreatedAt(LocalDateTime.now());
////                vendor.setAdditionalDetails("System Default Vendor");
////                VendorsDTO vendor1=objectMapper.convertValue(vendor, VendorsDTO.class);
////                supplierDao.saveSupplier(vendor1);
////
////                System.out.println("Default Vendor Saved");
////            }
////        };
//    }
//}