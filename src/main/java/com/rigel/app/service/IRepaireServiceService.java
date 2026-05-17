package com.rigel.app.service;

import java.util.List;

import com.rigel.app.model.RepaireDevice;
import com.rigel.app.model.dto.ExpenseCreteria;
import com.rigel.app.model.dto.RepaireDeviceDto;
import com.rigel.app.model.dto.SearchCriteria;

public interface IRepaireServiceService {
	
	public RepaireDevice saveRepair(RepaireDevice expaDevice);

	public RepaireDevice updateRepaire(RepaireDevice reqDevice);

	public List<RepaireDevice> searchRepair(SearchCriteria expaDevice);
	public RepaireDevice updateStatus(RepaireDevice repaireDevice);
}
