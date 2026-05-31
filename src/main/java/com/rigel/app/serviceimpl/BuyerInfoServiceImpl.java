package com.rigel.app.serviceimpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rigel.app.dao.IBuyerDao;
import com.rigel.app.dao.IInventoryDao;
import com.rigel.app.dao.ISalesDao;
import com.rigel.app.model.BuyerInfo;
import com.rigel.app.model.Inventory;
import com.rigel.app.model.SalesInfo;
import com.rigel.app.model.dto.BuyerInfoDto;
import com.rigel.app.model.dto.SalesInfoDto;
import com.rigel.app.model.dto.SalesRequest;
import com.rigel.app.model.dto.SalesResponse;
import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.model.dto.TransactionBorrow;
import com.rigel.app.service.IBuyerInfoService;
import com.rigel.app.util.Constaints;
import com.rigel.app.util.DiscountType;
import com.rigel.app.validate.SalesInfoValidator;

@Lazy
@Service
public class BuyerInfoServiceImpl implements IBuyerInfoService {

	@Autowired
	IBuyerDao buyerDao;

	@Autowired
	ISalesDao salesDao;

	@Autowired
	IInventoryDao inventoryDao;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	InvoiceGeneratorService invoiceService;

	@Autowired
	SalesInfoValidator salesInfoValidator;

	@Override
	public SalesResponse saveBuyerInfo(SalesRequest salesRequest) {

		List<SalesInfoDto> sales = objectMapper.convertValue(salesRequest.getBuyerInfoDto().getSalesInfo(),
				new TypeReference<List<SalesInfoDto>>() {
				});

		salesInfoValidator.validate(sales, salesRequest.getUserId());
		
		BigDecimal total = sales.stream()
		        .map(s -> BigDecimal.valueOf(s.getSoldPrice()))
		        .reduce(BigDecimal.ZERO, BigDecimal::add);
		
		BuyerInfo buyer = objectMapper.convertValue(salesRequest.getBuyerInfoDto(), BuyerInfo.class);
		String invoiceNumber = invoiceService.generateInvoiceNumber(salesRequest.getUserId(), "INV", "");
		String customberId = invoiceService.generateCustId(salesRequest.getUserId(), "CUST_ID", "");

		BigDecimal paidAmount = buyer.getPaidAmount();
		System.out.println("ffffffff---------"+paidAmount);
		System.out.println("ffffffff---------"+buyer.getTotalAmount());
		String transObject = null;
		try {
			List<TransactionBorrow> transactionBorrow = new ArrayList<>();
			TransactionBorrow transactionBorro1 = TransactionBorrow.builder().key(1).amount(paidAmount)
					.transType(buyer.getPaymentModes()).note("Rest amount is in borrow").paidDate(LocalDateTime.now())
					.build();
			transactionBorrow.add(transactionBorro1);
			transObject = objectMapper.writeValueAsString(transactionBorrow);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BigDecimal pendingAmount =total.subtract(buyer.getPaidAmount());
		
		buyer.setTransactionBorrow(transObject);
		buyer.setBorrowAmount(pendingAmount);
		buyer.setLastTransactionDate(LocalDateTime.now());
		buyer.setCreatedAt(LocalDateTime.now());
		buyer.setStatus(1);
		buyer.setTotalAmount(total);
		buyer.setInvoiceNumber(invoiceNumber);
		buyer.setCountryCode("91");
		buyer.setOwnerId(salesRequest.getUserId());
		buyer.setCustumberId(customberId);

		List<SalesInfo> salesSet = salesRequest.getBuyerInfoDto().getSalesInfo().stream().map(dto -> {
			SalesInfo sale = objectMapper.convertValue(dto, SalesInfo.class);
			sale.setBuyerInfo(buyer);
			sale.setCreatedAt(LocalDateTime.now());
			sale.setStatus(true);
			sale.setDiscountType(DiscountType.PERCENTAGE.toString());
			sale.setBuyerInfo(buyer);
			sale.setOwnerId(salesRequest.getUserId());
			return sale;
		}).toList();
		List<SalesInfo> list = new ArrayList<>();
		salesSet.forEach(s -> {
			list.add(s);
		});

		salesSet.stream().forEach(sal -> {
			Inventory icount = inventoryDao.findInventoryByCode(sal.getItemCode(), sal.getOwnerId());
			if (!sal.getCategory().equalsIgnoreCase(Constaints.SHOP_OWNER_CATEGORY)) {
				if (sal.getQuantity() < icount.getQuantity()) {
					int count = icount.getQuantity() - sal.getQuantity();
					inventoryDao.updateInventory(sal.getItemCode(), count, sal.getOwnerId());
				} else if (sal.getQuantity() == icount.getQuantity()) {
					inventoryDao.deleteInventory(sal.getItemCode(), sal.getOwnerId(), sal.getEntryType());
				}
			}
		});
		List<SalesInfo> savedSales = salesDao.saveSalesInfo(list);
		Set<SalesInfoDto> salesInfo = savedSales.stream().map(s -> objectMapper.convertValue(s, SalesInfoDto.class))
				.collect(java.util.stream.Collectors.toSet());

		BuyerInfoDto buyerInfoDto = salesRequest.getBuyerInfoDto();
		buyerInfoDto.setInvoiceNumber(invoiceNumber);
		buyerInfoDto.setSalesInfo(salesInfo);
		return SalesResponse.builder().buyerInfoDto(Arrays.asList(buyerInfoDto)).build();
	}

	@Override
	public int updateBuyerInfo(SalesRequest salesRequest) {

		BuyerInfoDto buyerInfoDto = salesRequest.getBuyerInfoDto();
		BuyerInfo buyerInfo = buyerDao.searchBuyerInfo(SearchCriteria.builder().userId(buyerInfoDto.getOwnerId())
				.isdownload(true).invoiceNumber(buyerInfoDto.getInvoiceNumber()).build()).stream().findFirst()
				.orElse(null);
		TransactionBorrow transObject= buyerInfoDto.getTransactionBorrow().get(0);
		transObject.setPaidDate(LocalDateTime.now());
		
		
		List<TransactionBorrow> transactionBorrowObj = null;
		if (transObject != null) {
			try {
				transactionBorrowObj = objectMapper.readValue(buyerInfo.getTransactionBorrow(),new TypeReference<List<TransactionBorrow>>(){});
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		transactionBorrowObj.add(transObject);
		
		String transObject1 = null;
		try {
			transObject1 = objectMapper.writeValueAsString(transactionBorrowObj);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BigDecimal paidAmount = transactionBorrowObj.stream()
		        .map(TransactionBorrow::getAmount)
		        .reduce(BigDecimal.ZERO, BigDecimal::add);
		
		BigDecimal borrowAmount = buyerInfo.getTotalAmount().subtract(paidAmount);
		String status="pending";
		if (borrowAmount.compareTo(BigDecimal.ZERO) == 0) {
		    status = "cleared";
		}
		return buyerDao.updateRestAmountAndDate(buyerInfo.getId(),paidAmount,status, borrowAmount,transObject1);
	}

//	@Override
//	public int deleteBuyerInfo(List<Long> buyerId) {
//		return buyerDao.deleteBuyerInfo(buyerId);
//	}

	@Override
	public SalesResponse searchBuyerInfo(SearchCriteria criteria) {
		return null;
	}

}