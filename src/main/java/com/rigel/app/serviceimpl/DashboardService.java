package com.rigel.app.serviceimpl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.rigel.app.dao.IDashboardDao;
import com.rigel.app.model.Inventory;
import com.rigel.app.model.SalesInfo;
import com.rigel.app.model.dto.ItemSalesCompare;
import com.rigel.app.service.IDashboardService;

@Lazy 
@Service
public class DashboardService implements IDashboardService {

	@Autowired
	private IDashboardDao dashboardDao;
	
	@Override
	public Map<String, Object> viewDashboard(String cycle,int ownerId) {
		return dashboardDao.viewDashboard(cycle, ownerId);
	}
}
//		LocalDate today = LocalDate.now(zoneId);
//		LocalDate yesterday = today.minusDays(1);
//		LocalDate previousMonth = today.minusMonths(1);
//		LocalDate previousYear = today.minusYears(1);
//
//// 1️⃣ Inventory ko itemType wise map kar lo
//		Map<String, Integer> stockMap = inventory.stream().collect(
//				Collectors.groupingBy(Inventory::getCategoryType, Collectors.summingInt(Inventory::getQuantity)));
//
//// 2️⃣ Sales ko itemType wise group kar lo
//		Map<String, List<SalesInfo>> salesMap = sales.stream().collect(Collectors.groupingBy(SalesInfo::getItemType));
//
//// 3️⃣ Sab unique itemType nikal lo (inventory + sales dono se)
//		Set<String> allItems = new HashSet<>();
//		allItems.addAll(stockMap.keySet());
//		allItems.addAll(salesMap.keySet());
//
//// 4️⃣ Final result build karo
//		return allItems.stream().map(itemType -> {
//
//			int totalStock = stockMap.getOrDefault(itemType, 0);
//			List<SalesInfo> itemSales = salesMap.getOrDefault(itemType, Collections.emptyList());
//
//			int day = 0, previousDayQty = 0;
//			int month = 0, previousMonthQty = 0;
//			int year = 0, previousYearQty = 0;
//			double selling = 0;
//
//			for (SalesInfo s : itemSales) {
//
//				LocalDate saleDate = Instant.ofEpochMilli(s.getCreatedAt()).atZone(zoneId).toLocalDate();
//
//				int qty = s.getQuantity();
//
//				selling += qty * s.getSellingPrice();
//
//				if (saleDate.equals(today))
//					day += qty;
//				if (saleDate.equals(yesterday))
//					previousDayQty += qty;
//
//				if (saleDate.getMonth() == today.getMonth() && saleDate.getYear() == today.getYear()) {
//					month += qty;
//				}
//
//				if (saleDate.getMonth() == previousMonth.getMonth() && saleDate.getYear() == previousMonth.getYear()) {
//					previousMonthQty += qty;
//				}
//
//				if (saleDate.getYear() == today.getYear())
//					year += qty;
//				if (saleDate.getYear() == previousYear.getYear())
//					previousYearQty += qty;
//			}
//
//			return ItemSalesCompare.builder().name(itemType).qty(totalStock).day(day).month(month).year(year)
//					.previousDay(previousDayQty).previousMonth(previousMonthQty).previousYear(previousYearQty)
//					.selling(selling).date(today.toString()).build();
//
//		}).collect(Collectors.toList());
//	}
//
//}
