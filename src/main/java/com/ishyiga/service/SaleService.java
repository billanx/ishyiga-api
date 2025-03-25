package com.ishyiga.service;

import com.ishyiga.entities.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface SaleService {
    Page<Sale> getAllSales(Pageable pageable);
    Sale saveSale(Sale sale);

    Map<String, List<?>> updateSales(List<Sale> sales);
    void deleteSale(Long id);
}
