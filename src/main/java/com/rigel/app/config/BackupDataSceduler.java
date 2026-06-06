//package com.rigel.app.config;
//
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
//	 @Autowired
//	 private IInventoryService inventoryService;
//
//	 @Autowired
//	 private INotificationService notificationService;
//	
//	 @Scheduled(cron = "0 0 11 * * ?")
//	 public void runEveryDayAt11AM() {
//	    
//	 }
//	 
//	 @Scheduled(cron = "0 0 22 * * ?")
//	 public void notificationScheduler() {
//		List<Inventory> inventoryList=inventoryService.searchInventory(SearchCriteria.builder().userId(0).build());
//		inventoryList.stream().filter(i->i.getQuantity()>0).forEach(s->{
//			Notification notification=new Notification();
//			notification.setNotificationType(Constaints.NOTIFICATION_TYPE_LOWSTOCK);
//			 String desc = "⚠ Low Stock: " 
//		                + s.getModelName() +"("+s.getItemCode()+")"
//		                + " (Remaining: " + s.getQuantity() + ")";
//			notification.setDescription(desc);
//			notificationService.saveNotification(notification);
//		});
//	 }
//
//}
