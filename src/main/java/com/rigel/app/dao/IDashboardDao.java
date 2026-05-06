package com.rigel.app.dao;

import java.util.List;
import java.util.Map;

import com.rigel.app.model.BuyerInfo;
import com.rigel.app.model.dto.DashboardRequest;
import com.rigel.app.model.dto.SearchCriteria;

public interface IDashboardDao {

	public Map<String, Object> viewDashboard(DashboardRequest dashboardRequest);


}
