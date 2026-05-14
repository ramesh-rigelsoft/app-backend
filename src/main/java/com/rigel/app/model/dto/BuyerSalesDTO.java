package com.rigel.app.model.dto;

import java.util.List;

import com.rigel.app.model.BuyerInfo;
import com.rigel.app.model.SalesInfo;

import lombok.Data;

@Data
public class BuyerSalesDTO {

    private BuyerInfo buyerInfo;

    private List<SalesInfo> salesInfos;
}