
package com.ishyiga.service;

import com.ishyiga.entities.Sale;

import java.util.List;
import java.util.Map;

public interface SaleService {
    List<Sale> getAllSales();
    Sale saveSale(Sale sale);

    Map<String, List<?>> updateSales(List<Sale> sales);
    void deleteSale(Long id);
}
