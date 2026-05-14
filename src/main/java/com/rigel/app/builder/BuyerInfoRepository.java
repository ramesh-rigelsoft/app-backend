package com.rigel.app.builder;


import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.rigel.app.model.BuyerInfo;
import com.rigel.app.model.dto.SearchCriteria;
import com.rigel.app.builder.*;


@Slf4j
@Repository
public class BuyerInfoRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SalesQueryBuilder queryBuilder;

    @Autowired
    private BuyerInfoRowMapper rowMapper;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    // ==========================
    // SEARCH WITH PAGINATION
    // ==========================
    public List<BuyerInfoDTO> getBuyerSearch(SearchCriteria searchCriteria) {

        List<Object> preparedStmtList = new ArrayList<>();

        String query = queryBuilder.getBuyerSalesSearch(searchCriteria, preparedStmtList);

        log.info("Final Buyer Query: {}", query);
        return jdbcTemplate.query(query, rowMapper, preparedStmtList.toArray());
    }
}