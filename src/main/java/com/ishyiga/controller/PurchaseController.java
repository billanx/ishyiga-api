package com.ishyiga.controller;

import com.ishyiga.dto.ErrorResponse;
import com.ishyiga.entities.Purchase;
import com.ishyiga.service.PurchaseService;
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

@RestController
@RequestMapping("/api/v1/purchases")
@Slf4j
@Validated
@Tag(name = "Purchase Management", description = "APIs for managing purchases")
@CrossOrigin(origins = "*")
public class PurchaseController {
    
    private final PurchaseService purchaseService;

    @Autowired
    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA')")
    @Operation(summary = "Get all purchases", description = "Retrieves a paginated list of all purchases")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved purchases"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getAllPurchases(
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
            Page<Purchase> purchases = purchaseService.getAllPurchases(pageRequest);
            return ResponseEntity.ok(purchases);
        } catch (Exception e) {
            log.error("Failed to retrieve purchases: {}", e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to retrieve purchases", e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a purchase", description = "Creates a new purchase record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Purchase created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> createPurchase(@Valid @RequestBody Purchase purchase) {
        try {
            validatePurchase(purchase);
            Purchase savedPurchase = purchaseService.savePurchase(purchase);
            log.info("Purchase created successfully with ID: {}", savedPurchase.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPurchase);
        } catch (Exception e) {
            log.error("Failed to create purchase: {}", e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to create purchase", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA')")
    @Operation(summary = "Update a purchase", description = "Updates an existing purchase record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Purchase updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Purchase not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> updatePurchase(
            @Parameter(description = "ID of the purchase to update")
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody Purchase purchase) {
        try {
            validatePurchase(purchase);
            purchase.setId(id);
            Purchase updatedPurchase = purchaseService.savePurchase(purchase);
            log.info("Purchase {} updated successfully", id);
            return ResponseEntity.ok(updatedPurchase);
        } catch (Exception e) {
            log.error("Failed to update purchase {}: {}", id, e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to update purchase", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete purchase", description = "Deletes a purchase by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Purchase deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Purchase not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> deletePurchase(
            @Parameter(description = "ID of the purchase to delete")
            @PathVariable @Min(1) Long id) {
        try {
            purchaseService.deletePurchase(id);
            log.info("Purchase {} deleted successfully", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Failed to delete purchase {}: {}", id, e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to delete purchase", e.getMessage()));
        }
    }

    private void validatePurchase(Purchase purchase) {
        if (purchase.getMonth() < 1 || purchase.getMonth() > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        if (purchase.getYear() < 2000) {
            throw new IllegalArgumentException("Year must be 2000 or later");
        }
        if (purchase.getInvoiceCount() < 0) {
            throw new IllegalArgumentException("Invoice count must be non-negative");
        }
        if (purchase.getPoValue() == null || purchase.getPoValue() < 0) {
            throw new IllegalArgumentException("PO value must be non-negative");
        }
        if (purchase.getClient_id() == null || purchase.getClient_id().trim().isEmpty()) {
            throw new IllegalArgumentException("Client ID cannot be empty");
        }
    }
}
