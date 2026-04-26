package com.rigel.app.daoimpl;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.rigel.app.dao.IDashboardDao;
import com.rigel.app.model.Expense;
import com.rigel.app.model.Inventory;
import com.rigel.app.model.SalesInfo;
import com.rigel.app.model.dto.ItemSalesCompare;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class DashboardDaoImpl implements IDashboardDao {

	@Autowired
	private EntityManager entityManager;

	@Override
	public Map<String, Object> viewDashboard(String cycle, int ownerId) {
		LocalDateTime start = null;
		LocalDateTime end = null;
		if(cycle.equals("Day")) {
			start = LocalDate.now()
			        .minusDays(1)
			        .atTime(0, 1);
			end = LocalDateTime.now();
		} else if(cycle.equals("Month")) {
			start = LocalDateTime.now().minusMonths(1).withDayOfMonth(1);
			end = LocalDateTime.now();
		} else if(cycle.equals("Year")) {
			start = LocalDateTime.now()
					.minusYears(1)
			        .withMonth(1)
			        .withDayOfMonth(1);
			end = LocalDateTime.now();
		}
		
		List<SalesInfo> sales = entityManager
				.createQuery("SELECT e FROM SalesInfo e " + "WHERE e.ownerId = :ownerId "
						+ "AND e.createdAt BETWEEN :start AND :end", SalesInfo.class)
				.setParameter("ownerId", ownerId).setParameter("start", start).setParameter("end", end).getResultList();

		List<Inventory> inventory = entityManager
				.createQuery("SELECT e FROM Inventory e " + "WHERE e.ownerId = :ownerId ", Inventory.class)
				.setParameter("ownerId", ownerId).getResultList();

		List<Expense> expense = entityManager
				.createQuery("SELECT e FROM Expense e " + "WHERE e.ownerId = :ownerId "
						+ "AND e.date BETWEEN :start AND :end ", Expense.class)
				.setParameter("ownerId", ownerId).setParameter("start", start).setParameter("end", end).getResultList();

		// sales section
		Map<String, Object> salesInfo = new HashMap<>();
//		Double totalValues = inventory.stream().mapToDouble(s -> s.getQuantity() * s.getInitialPrice()).sum();
		BigDecimal totalValues = inventory.stream()
			    .map(s -> BigDecimal.valueOf(s.getQuantity())
			            .multiply(BigDecimal.valueOf(s.getInitialPrice())))
			    .reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal totalQuantity = inventory.stream()
			    .map(s -> BigDecimal.valueOf(s.getQuantity()))
			    .reduce(BigDecimal.ZERO, BigDecimal::add);
//		int totalQuantity = inventory.stream().mapToInt(Inventory::getQuantity).sum();
		BigDecimal totalExpense = expense.stream()
			    .map(s -> BigDecimal.valueOf(s.getAmount()))
			    .reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal totalSalesAmount = sales.stream()
			    .map(s -> BigDecimal.valueOf(s.getQuantity())
			            .multiply(BigDecimal.valueOf(s.getSellingPrice())))
			    .reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal totalProfitValue = sales.stream()
			    .map(s -> BigDecimal.valueOf(s.getQuantity())
			        .multiply(
			            BigDecimal.valueOf(s.getSellingPrice())
			                .subtract(BigDecimal.valueOf(s.getInitialPrice()))
			        )
			    )
			    .reduce(BigDecimal.ZERO, BigDecimal::add);
//		Double totalExpense = expense.stream().mapToDouble(s -> s.getAmount()).sum();
//		double totalSalesAmount = sales.stream().mapToDouble(s -> s.getQuantity() * s.getSellingPrice()).sum();
		BigDecimal profite = totalProfitValue.subtract(totalExpense);
		long lowStockCount = inventory.stream().filter(i -> i.getQuantity() != null && i.getQuantity() < 10).count();

		Map<String, Long> itemCount = sales.stream()
				.collect(Collectors.groupingBy(SalesInfo::getCategoryType, Collectors.counting()));
		List<ItemSalesCompare> itemSalesCompare = getItemSalesCompare(cycle,start,end,inventory, sales);
		salesInfo.put("totalValue", totalValues);
		salesInfo.put("totalStock", totalQuantity);
		salesInfo.put("totalExpense", totalExpense);
		salesInfo.put("totalSalesAmount", totalSalesAmount);
		salesInfo.put("profit", profite);
		salesInfo.put("lowStock", lowStockCount);
		salesInfo.put("itemSalesCompare", itemSalesCompare);
	
		return salesInfo;
	}
	
	 public static List<ItemSalesCompare> getItemSalesCompare(String cycle,LocalDateTime start1,LocalDateTime end1,List<Inventory> inventory, List<SalesInfo> sales) {

	        LocalDate today = LocalDate.now();

	        // 1️⃣ Inventory map
	        Map<String, Integer> stockMap = inventory.stream()
	                .collect(Collectors.groupingBy(Inventory::getCategory,
	                        Collectors.summingInt(Inventory::getQuantity)));

	        // 2️⃣ Sales map
	        Map<String, List<SalesInfo>> salesMap = sales.stream()
	                .collect(Collectors.groupingBy(SalesInfo::getCategory));

	        // 3️⃣ All unique items
	        Set<String> allItems = new HashSet<>();
	        allItems.addAll(stockMap.keySet());
	        allItems.addAll(salesMap.keySet());

	        // 4️⃣ Build result
	        return allItems.stream().map(category -> {

	            int totalStock = stockMap.getOrDefault(category, 0);
	            List<SalesInfo> itemSales = salesMap.getOrDefault(category, Collections.emptyList());

	            int day = 0, previousDayQty = 0;
	            int month = 0, previousMonthQty = 0;
	            int year = 0, previousYearQty = 0;
	            double selling = 0;
	            ZoneOffset offset = ZoneOffset.ofHoursMinutes(5, 30); // +05:30

	            for (SalesInfo s : itemSales) {
	            	LocalDateTime createdAt = s.getCreatedAt(); // example Date
	            	LocalDate saleDate = createdAt.toLocalDate();
	            	int qty = s.getQuantity();
	            	
	            	if(cycle.equals("Day")) {
	            	  LocalDate start = LocalDate.now().minusDays(1);
	            	  LocalDate end = LocalDate.now();
		            	  if(start.equals(saleDate)) {
		            		  previousDayQty=previousDayQty+qty; 
		            	  } 
		            	  
		            	  if(end.equals(saleDate)) {
		            		  day=day+qty; 
		            	  }
	        		} else if(cycle.equals("Month")) {
	        		  LocalDate start = LocalDate.now().minusMonths(1);
	        		  LocalDate end = LocalDate.now();
	        		  if(start.getMonth().equals(saleDate.getMonth())&&start.getYear()==saleDate.getYear()) {
	            		  previousMonthQty=previousMonthQty+qty; 
	            	  } 
	            	  
	        		  if(end.getMonth().equals(saleDate.getMonth())&&end.getYear()==saleDate.getYear()) {
	        			  month=month+qty;
	            	  }
	        		} else if(cycle.equals("Year")) {
	        		  LocalDate start = LocalDate.now().minusYears(1);
	        		  LocalDate end = LocalDate.now();
	        		  if(start.getYear()==saleDate.getYear()) {
	        			  previousYearQty=previousYearQty+qty; 
	            	  } 
	            	  
	        		  if(end.getYear()==saleDate.getYear()) {
	        			  year=year+qty;
	            	  }
	        		}
	            }
	            System.out.println("category---"+category);
                return ItemSalesCompare.builder()
	                    .name(category)
	                    .qty(totalStock)
	                    .day(day)
	                    .month(month)
	                    .year(year)
	                    .previousDay(previousDayQty)
	                    .previousMonth(previousMonthQty)
	                    .previousYear(previousYearQty)
	                    .cycle(cycle)
	                    .date(today)
	                    .build();

	        }).collect(Collectors.toList());
	    }

}