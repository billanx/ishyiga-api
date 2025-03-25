package com.ishyiga.controller;

import com.ishyiga.dto.ErrorResponse;
import com.ishyiga.entities.Sale;
import com.ishyiga.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/sales")
@Slf4j
@Validated
@Tag(name = "Sales Management", description = "APIs for managing sales")
@CrossOrigin(origins = "*")
public class SaleController {
    
    private final SaleService saleService;

    @Autowired
    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ISHYIGA', 'BANK')")
    @Operation(summary = "Get all sales", description = "Retrieves a paginated list of all sales")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved sales"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getAllSales(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @Parameter(description = "Field to sort by")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(defaultValue = "asc") String direction) {
        try {
            Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            Page<Sale> sales = saleService.getAllSales(pageRequest);
            return ResponseEntity.ok(sales);
        } catch (Exception e) {
            log.error("Failed to retrieve sales: {}", e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to retrieve sales", e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA')")
    @Operation(summary = "Create a sale", description = "Creates a new sale record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Sale created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> createSale(@Valid @RequestBody Sale sale) {
        try {
            validateSale(sale);
            Sale savedSale = saleService.saveSale(sale);
            log.info("Sale created successfully with ID: {}", savedSale.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedSale);
        } catch (Exception e) {
            log.error("Failed to create sale: {}", e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to create sale", e.getMessage()));
        }
    }

    @PostMapping(value = "/list", consumes = "application/json")
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA')")
    @Operation(summary = "Update multiple sales", description = "Updates a list of sales records")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sales updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> updateSales(@Valid @RequestBody List<Sale> sales) {
        try {
            Map<String, List<?>> result = saleService.updateSales(sales);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Failed to update sales: {}", e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to update sales", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ISHYIGA', 'BANK')")
    @Operation(summary = "Update a sale", description = "Updates an existing sale record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sale updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Sale not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> updateSale(
            @Parameter(description = "ID of the sale to update")
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody Sale sale) {
        try {
            validateSale(sale);
            sale.setId(id);
            Sale updatedSale = saleService.saveSale(sale);
            log.info("Sale {} updated successfully", id);
            return ResponseEntity.ok(updatedSale);
        } catch (Exception e) {
            log.error("Failed to update sale {}: {}", id, e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to update sale", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a sale", description = "Deletes an existing sale record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Sale deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Sale not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> deleteSale(
            @Parameter(description = "ID of the sale to delete")
            @PathVariable @Min(1) Long id) {
        try {
            saleService.deleteSale(id);
            log.info("Sale {} deleted successfully", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Failed to delete sale {}: {}", id, e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to delete sale", e.getMessage()));
        }
    }

    private void validateSale(Sale sale) {
        if (sale.getMonth() < 1 || sale.getMonth() > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        if (sale.getYear() < 2000) {
            throw new IllegalArgumentException("Year must be 2000 or later");
        }
        if (sale.getInvoiceCount() < 0) {
            throw new IllegalArgumentException("Invoice count must be non-negative");
        }
        if (sale.getSalesValue() == null || sale.getSalesValue() < 0) {
            throw new IllegalArgumentException("Sales value must be non-negative");
        }
        if (sale.getTotalVat() == null || sale.getTotalVat() < 0) {
            throw new IllegalArgumentException("Total VAT must be non-negative");
        }
        if (sale.getCash() == null || sale.getCash() < 0) {
            throw new IllegalArgumentException("Cash amount must be non-negative");
        }
        if (sale.getCredit() == null || sale.getCredit() < 0) {
            throw new IllegalArgumentException("Credit amount must be non-negative");
        }
        if (sale.getClient_id() == null || sale.getClient_id().trim().isEmpty()) {
            throw new IllegalArgumentException("Client ID cannot be empty");
        }
    }
}
