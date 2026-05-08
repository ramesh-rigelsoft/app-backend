package com.rigel.app.printerservice;

import java.io.OutputStream;
import java.net.Socket;

public class LabelPrinterService implements PrinterService {
    @Override
    public void print(String data) throws Exception {
        String zpl = "^XA^FO50,50^ADN,36,20^FD" + data + "^FS^XZ";
        try (Socket clientSocket = new Socket("192.168.1.100", 9100);
             OutputStream out = clientSocket.getOutputStream()) {
            out.write(zpl.getBytes());
            out.flush();
        }
    }
}

