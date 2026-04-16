package com.rigel.app.util;

import java.math.BigDecimal;

public class IndianNumberFormat {

    public String format(BigDecimal number) {
    	if (number == null) return "0.00";
        number = number.setScale(2, BigDecimal.ROUND_HALF_UP);
        String[] parts = number.toPlainString().split("\\.");
        String intPart = parts[0];
        String decimalPart = parts[1];

        StringBuilder sb = new StringBuilder();

        // Handle last 3 digits
        int len = intPart.length();
        if (len > 3) {
            String last3 = intPart.substring(len - 3);
            String remaining = intPart.substring(0, len - 3);

            // Process remaining in 2-digit groups from right
            StringBuilder remSb = new StringBuilder();
            while (remaining.length() > 2) {
                remSb.insert(0, "," + remaining.substring(remaining.length() - 2));
                remaining = remaining.substring(0, remaining.length() - 2);
            }

            // Add the leftover 1 or 2 digits
            if (!remaining.isEmpty()) {
                remSb.insert(0, remaining);
            }

            sb.append(remSb).append(",").append(last3);
        } else {
            sb.append(intPart);
        }

        sb.append(".").append(decimalPart);
        return sb.toString();
    }

//    public static String format(Object amt) {
//        if (amt == null) return "0.00";
//        try {
//            BigDecimal number = new BigDecimal(amt.toString());
//            return format(number);
//        } catch (Exception e) {
//            return "0.00";
//        }
//    }

//    public static void main(String[] args) {
//        BigDecimal amt1 = new BigDecimal("152542.32");
//        BigDecimal amt2 = new BigDecimal("540000.00");
//        BigDecimal amt3 = new BigDecimal("123456789.99");
//        BigDecimal amt4 = new BigDecimal("54000.00");
//
//        System.out.println(format(amt1)); // 1,52,542.32
//        System.out.println(format(amt2)); // 5,40,000.00
//        System.out.println(format(amt3)); // 12,34,56,789.99
//        System.out.println(format(amt4)); // 54,000.00
//    }
}