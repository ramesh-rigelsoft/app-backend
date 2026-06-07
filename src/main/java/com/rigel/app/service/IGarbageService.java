package com.rigel.app.service;

import com.rigel.app.model.GarbageItemsInfo;

public interface IGarbageService {
	
	public GarbageItemsInfo saveGarbage(GarbageItemsInfo garbage);
	public GarbageItemsInfo updateGarbage(GarbageItemsInfo garbage);
	public GarbageItemsInfo findGarbageByItemCode(String itemCode);
	public GarbageItemsInfo findGarbageById(String id);


}
