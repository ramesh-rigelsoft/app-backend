package com.rigel.app.service;

public interface IPrintService {
	
	public boolean billPrint(String invoiceNumber,int ownerId,String username);
	
	public boolean barCodePrint(String invoiceNumber);

}
