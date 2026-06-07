//package com.rigel.app.config;
//
//import java.time.Duration;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import com.rigel.app.model.Inventory;
//import com.rigel.app.model.Notification;
//import com.rigel.app.model.dto.SearchCriteria;
//import com.rigel.app.service.IInventoryService;
//import com.rigel.app.service.INotificationService;
//import com.rigel.app.util.Constaints;
//
//@Component
//public class BackupDataSceduler {
//
//	@Autowired
//	private IInventoryService inventoryService;
//
//	@Autowired
//	private INotificationService notificationService;
//
//	@Scheduled(cron = "0 0 22 * * ?")
//	public void notificationScheduler() {
//
//		List<Inventory> inventoryList = inventoryService.searchInventory(
//		        SearchCriteria.builder()
//		                .startIndex(0)
//		                .maxRecords(1000)
//		                .build()
//		);
//		
//		inventoryList.forEach(item -> {
//
//	        String desc = null;
//	        String type = null;
//
//	        Integer qty = item.getQuantity() != null ? item.getQuantity() : 0;
//
//	        // ================= STOCK BASED =================
//
//	        if (qty == 0) {
//	            type = Constaints.NOTIFICATION_TYPE_OUT_OF_TOCK;
//	            desc = "❌ Out of Stock: " + item.getModelName() + " (" + item.getItemCode() + ")";
//	        } 
//	        else if (qty <= 5) {
//	            type = Constaints.NOTIFICATION_TYPE_LOWS_TOCK;
//	            desc = "⚠ Low Stock: " + item.getModelName() + " (" + item.getItemCode() + ")";
//	        }
//
//	        // ================= SALES BASED =================
//
//	        if (item.getUpdatedAt() != null) {
//
//	            long days = Duration.between(item.getUpdatedAt(), LocalDateTime.now()).toDays();
//
//	            if (days >= 90) {
//	                type = Constaints.NOTIFICATION_TYPE_DEAD_STOCK;
//	                desc = "🔴 Dead Stock: " + item.getModelName()
//	                        + " Not sold for " + days + " days";
//	            } 
//	            else if (days >= 60) {
//	                type = Constaints.NOTIFICATION_TYPE_NO_SALES;
//	                desc = "⚠ No Sale: " + item.getModelName()
//	                        + " Not sold for " + days + " days";
//	            } 
//	            else if (days >= 30) {
//	                type = Constaints.NOTIFICATION_TYPE_SLOW_MOVING;
//	                desc = "🐢 Slow Moving: " + item.getModelName()
//	                        + " Low sales in last " + days + " days";
//	            } 
//	            else {
//	                type = Constaints.NOTIFICATION_TYPE_FAST_MOVING;
//	                desc = "🚀 Fast Moving: " + item.getModelName()
//	                        + " Selling frequently";
//	            }
//	        }
//
//
//	        // ================= SAVE ITEM NOTIFICATION =================
//
//	        if (desc != null && type != null) {
//	            Notification notification = new Notification();
//	            notification.setNotificationType(type);
//	            notification.setType(type);
//	            notification.setDescription(desc);
//	            notification.setOwnerId(item.getOwnerId());
//	            notification.setCreatedAt(LocalDateTime.now());
//	            notification.setSeenStatus(false);
//	            notification.setStatus(true);
//	            notificationService.saveNotification(notification);
//	        }
//
//	    });	    
//	   	}
//	}
