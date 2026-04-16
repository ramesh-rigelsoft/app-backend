package com.rigel.app.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.rigel.app.model.Invoice;
import com.rigel.app.repo.InvoiceRepository;

import jakarta.transaction.Transactional;

@Component
public class DataLoader implements CommandLineRunner {

    private final InvoiceRepository invoiceRepo;

    public DataLoader(InvoiceRepository invoiceRepo) {
        this.invoiceRepo = invoiceRepo;
    }

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        long count = invoiceRepo.count();

        if (count == 0) {
            Invoice inv1 = Invoice.builder()
                    .formate("INV")
                    .formateName("invoiceNumber")
                    .fy("2026-2027")
                    .lastNumber(0)
                    .build();

            Invoice inv2 = Invoice.builder()
                    .formate("ITM")
                    .formateName("itemCode")
                    .fy("2026-2027")
                    .lastNumber(0)
                    .build();

            invoiceRepo.save(inv1);
            invoiceRepo.save(inv2);

            System.out.println("Default invoice data inserted.");
        }
    }
}
