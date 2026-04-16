package com.rigel.app.service;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import com.rigel.app.model.Inventory;
import com.rigel.app.model.SalesInfo;
import com.rigel.app.model.dto.ItemSalesCompare;

public interface IDashboardService {
	
	public Map<String, Object> viewDashboard(String cycle,int ownerId);
}