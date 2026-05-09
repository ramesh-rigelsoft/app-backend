package com.rigel.app.util;

import java.util.HashMap;
import java.util.Map;

public class StateUtil {

    private static final Map<String, String> STATE_MAP = new HashMap<>();

    static {
        STATE_MAP.put("01", "Jammu & Kashmir");
        STATE_MAP.put("02", "Himachal Pradesh");
        STATE_MAP.put("03", "Punjab");
        STATE_MAP.put("04", "Chandigarh");
        STATE_MAP.put("05", "Uttarakhand");
        STATE_MAP.put("06", "Haryana");
        STATE_MAP.put("07", "Delhi");
        STATE_MAP.put("08", "Rajasthan");
        STATE_MAP.put("09", "Uttar Pradesh");
        STATE_MAP.put("10", "Bihar");
        STATE_MAP.put("11", "Sikkim");
        STATE_MAP.put("12", "Arunachal Pradesh");
        STATE_MAP.put("13", "Nagaland");
        STATE_MAP.put("14", "Manipur");
        STATE_MAP.put("15", "Mizoram");
        STATE_MAP.put("16", "Tripura");
        STATE_MAP.put("17", "Meghalaya");
        STATE_MAP.put("18", "Assam");
        STATE_MAP.put("19", "West Bengal");
        STATE_MAP.put("20", "Jharkhand");
        STATE_MAP.put("21", "Odisha");
        STATE_MAP.put("22", "Chhattisgarh");
        STATE_MAP.put("23", "Madhya Pradesh");
        STATE_MAP.put("24", "Gujarat");
        STATE_MAP.put("25", "Daman & Diu");
        STATE_MAP.put("26", "Dadra & Nagar Haveli & Daman & Diu");
        STATE_MAP.put("27", "Maharashtra");
        STATE_MAP.put("28", "Andhra Pradesh");
        STATE_MAP.put("29", "Karnataka");
        STATE_MAP.put("30", "Goa");
        STATE_MAP.put("31", "Lakshadweep");
        STATE_MAP.put("32", "Kerala");
        STATE_MAP.put("33", "Tamil Nadu");
        STATE_MAP.put("34", "Puducherry");
        STATE_MAP.put("35", "Andaman & Nicobar Islands");
        STATE_MAP.put("36", "Telangana");
        STATE_MAP.put("37", "Andhra Pradesh (New)");
    }

    public static String getStateName(String stateCode) {
        return STATE_MAP.getOrDefault(stateCode, "Unknown State");
    }
}