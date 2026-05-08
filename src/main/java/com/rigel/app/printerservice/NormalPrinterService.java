package com.rigel.app.printerservice;
import javax.print.*;

public class NormalPrinterService implements PrinterService {
   
	@Override
    public void print(String data) throws Exception {
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        PrintService printer = null;
        for (PrintService service : services) {
            if (service.getName().toLowerCase().contains("epson")) {
                printer = service;
                break;
            }
        }
        if (printer == null) throw new RuntimeException("Normal printer not found!");

        DocPrintJob job = printer.createPrintJob();
        byte[] bytes = data.getBytes();
        Doc doc = new SimpleDoc(bytes, DocFlavor.BYTE_ARRAY.AUTOSENSE, null);
        job.print(doc, null);
    }
}
