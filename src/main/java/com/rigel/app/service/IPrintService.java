package com.rigel.app.service;

public interface IPrintService {
	
	public boolean billPrint(String invoiceNumber);
	
	public boolean barCodePrint(String invoiceNumber);

}
