package com.ishyiga.controller;

import com.ishyiga.entities.RefundCancelled;
import com.ishyiga.service.RefundCancelledService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
import com.ishyiga.dto.ErrorResponse;

@RestController
@RequestMapping("/api/v1/refunds_cancelled")
@Slf4j
@Validated
@Tag(name = "Refund Cancelled Management", description = "APIs for managing cancelled refunds")
public class RefundCancelledController {
    private final RefundCancelledService refundCancelledService;

    @Autowired
    public RefundCancelledController(RefundCancelledService refundCancelledService) {
        this.refundCancelledService = refundCancelledService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA','BANK')")
    @Operation(summary = "Get all cancelled refunds", description = "Retrieves a paginated list of all cancelled refunds")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved cancelled refunds"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getAllRefundsCancelled(
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
            Page<RefundCancelled> refunds = refundCancelledService.getAllRefundsCancelled(pageRequest);
            return ResponseEntity.ok(refunds);
        } catch (Exception e) {
            log.error("Failed to retrieve cancelled refunds: {}", e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to retrieve cancelled refunds", e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA')")
    @Operation(summary = "Create a cancelled refund", description = "Creates a new cancelled refund record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cancelled refund created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> createRefundCancelled(@Valid @RequestBody RefundCancelled refundCancelled) {
        try {
            validateRefundCancelled(refundCancelled);
            RefundCancelled createdRefund = refundCancelledService.saveRefundCancelled(refundCancelled);
            log.info("Cancelled refund created successfully with ID: {}", createdRefund.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRefund);
        } catch (IllegalArgumentException e) {
            log.error("Invalid input for cancelled refund: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Invalid input", e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to create cancelled refund: {}", e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to create cancelled refund", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA')")
    @Operation(summary = "Delete a cancelled refund", description = "Deletes a cancelled refund record by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cancelled refund deleted successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> deleteRefundCancelled(
            @Parameter(description = "ID of the cancelled refund to delete")
            @PathVariable @Min(1) Long id) {
        try {
            refundCancelledService.deleteRefundCancelled(id);
            log.info("Cancelled refund {} deleted successfully", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Failed to delete cancelled refund {}: {}", id, e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to delete cancelled refund", e.getMessage()));
        }
    }

    private void validateRefundCancelled(RefundCancelled refundCancelled) {
        if (refundCancelled.getMonth() < 1 || refundCancelled.getMonth() > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        if (refundCancelled.getInvoiceCount() < 0) {
            throw new IllegalArgumentException("Invoice count must be non-negative");
        }
        if (refundCancelled.getSalesValue() == null || refundCancelled.getSalesValue() < 0) {
            throw new IllegalArgumentException("Sales value must be non-negative");
        }
        if (refundCancelled.getTotalVat() == null || refundCancelled.getTotalVat() < 0) {
            throw new IllegalArgumentException("Total VAT must be non-negative");
        }
        if (refundCancelled.getCash() == null || refundCancelled.getCash() < 0) {
            throw new IllegalArgumentException("Cash amount must be non-negative");
        }
        if (refundCancelled.getCredit() == null || refundCancelled.getCredit() < 0) {
            throw new IllegalArgumentException("Credit amount must be non-negative");
        }
    }
}
