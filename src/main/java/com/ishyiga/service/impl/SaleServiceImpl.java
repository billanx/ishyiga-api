package com.ishyiga.service.impl;

import com.ishyiga.entities.Sale;
import com.ishyiga.exception.BadRequestException;
import com.ishyiga.exception.DatabaseException;
import com.ishyiga.repo.SaleRepository;
import com.ishyiga.service.SaleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SaleServiceImpl implements SaleService {
    @Autowired
    private SaleRepository saleRepository;

    @Override
    public Page<Sale> getAllSales(Pageable pageable) {
        return saleRepository.findAll(pageable);
    }

    @Override
    public Sale saveSale(Sale sale) {
        try {
            return saleRepository.save(sale);
        } catch (Exception e) {
            throw new DatabaseException("Error while saving the sale: " + e.getCause());
        }
    }

    @Override
    public Map<String, List<?>> updateSales(List<Sale> sales) {
        List<Sale> successList = new ArrayList<>();
        List<String> failedList = new ArrayList<>();

        for (Sale dto : sales) {
            try {
                successList.add(saveSale(dto));
            } catch (BadRequestException e) {
                log.error("Skipping sale updates for sales {} due to error: {}", sales, e.getMessage());
                failedList.add("Failed to update sale for " + sales + ": " + e.getMessage());
            }
        }

        return Map.of("success", successList, "failed", failedList);
    }

    @Override
    public void deleteSale(Long id) {
        saleRepository.deleteById(id);
    }
}
