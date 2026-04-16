package com.rigel.app.serviceimpl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.rigel.app.dao.IInventoryDao;
import com.rigel.app.model.BuyerInfo;
import com.rigel.app.model.Inventory;
import com.rigel.app.model.Items;
import com.rigel.app.model.SalesInfo;
import com.rigel.app.model.User;
import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.service.IInventoryService;
import com.rigel.app.service.ISalesService;
import com.rigel.app.util.BannerUtility;
import com.rigel.app.util.RAUtility;
import com.rigel.app.util.SalesSlipPDF;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

@Lazy 
@Service
public class BarcodeService  {
	
	@Autowired
	ISalesService salesService;
	
	public void barcodeGenerate(String text,int count) {
		Map<String,Object> json=new HashMap<>();
		json.put("data", "success");
		int defaultImgWidth=500;
		int defaultImgHeight=90;
		int perRow=1;
		List<String> ls=new ArrayList<>();
		for (int i = 0; i < count; i++) {
			ls.add(text+i+"");
		}
		RAUtility.getBarCodePDF(ls, defaultImgWidth, defaultImgHeight,perRow,text);
		
	}
}