
package com.ishyiga.controller;

import com.ishyiga.entities.Sale;
import com.ishyiga.service.SaleService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/sales")
@Slf4j
class SaleController {
    @Autowired
    private SaleService saleService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA','BANK')")
    public List<Sale> getAllSales() {
        return saleService.getAllSales();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA')")
    public Sale createSale(@RequestBody Sale sale) {
        return saleService.saveSale(sale);
    }

    @PostMapping(value = "/list", consumes = "application/json")
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA')")
    public Map<String, List<?>> updateSales(@Valid @RequestBody List<Sale> sales){
        return saleService.updateSales(sales);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA')")
    public void deleteSale(@PathVariable Long id) {
        saleService.deleteSale(id);
    }
}
