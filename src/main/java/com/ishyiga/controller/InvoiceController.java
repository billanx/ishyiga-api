package com.ishyiga.controller;

import com.ishyiga.dto.ErrorResponse;
import com.ishyiga.entities.Invoice;
import com.ishyiga.entities.ListItem;
import com.ishyiga.exception.DatabaseException;
import com.ishyiga.model.Response;
import com.ishyiga.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/invoices")
@Slf4j
@Validated
@Tag(name = "Invoice Management", description = "APIs for managing invoices")
@CrossOrigin(origins = "*")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA')")
    @Operation(summary = "Create an invoice", description = "Creates a new invoice with optional list items")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Invoice created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> createInvoice(@Valid @RequestBody Invoice invoice) {
        try {
            validateInvoice(invoice);
            Invoice createdInvoice = invoiceService.saveInvoiceWithListItems(invoice);
            log.info("Invoice created successfully with ID: {}", createdInvoice.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdInvoice);
        } catch (Exception e) {
            log.error("Failed to create invoice: {}", e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to create invoice", e.getMessage()));
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA','BANK')")
    @Operation(summary = "Get all invoices", description = "Retrieves a paginated list of all invoices")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved invoices"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getAllInvoices(
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
            Page<Invoice> invoices = invoiceService.getAllInvoices(pageRequest);
            return ResponseEntity.ok(invoices);
        } catch (Exception e) {
            log.error("Failed to retrieve invoices: {}", e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to retrieve invoices", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA','BANK')")
    @Operation(summary = "Get invoice by Invoice ID", description = "Retrieves an invoice by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invoice found"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Invoice not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getInvoiceByIdInvoice(
            @Parameter(description = "ID of the invoice to retrieve")
            @PathVariable @Min(1) Integer id) {
        try {
            Optional<Invoice> invoice = invoiceService.getInvoiceByIdInvoice(id);
            if (invoice.isPresent()) {
                return ResponseEntity.ok(invoice.get());
            }
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Invoice not found", "No invoice found with id: " + id));
        } catch (Exception e) {
            log.error("Failed to retrieve invoice {}: {}", id, e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve invoice", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA','BANK')")
    @Operation(summary = "Update an invoice", description = "Updates an existing invoice by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Invoice updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Invoice not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> updateInvoice(
            @Parameter(description = "ID of the invoice to update")
            @PathVariable @Min(1) Integer id,
            @Valid @RequestBody Invoice invoice) {
        try {
//            validateInvoice(invoice);
            Response response = invoiceService.updateInvoice(id, invoice);
            if (response.isStatus()) {
                return ResponseEntity.ok(response);
            }
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Invoice not found", "No invoice found with id: " + id));
        } catch (Exception e) {
            log.error("Failed to update invoice {}: {}", id, e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to update invoice", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA')")
    @Operation(summary = "Delete an invoice", description = "Deletes an invoice by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Invoice deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Invoice not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> deleteInvoice(
            @Parameter(description = "ID of the invoice to delete")
            @PathVariable @Min(1) Integer id) {
        try {
            invoiceService.deleteInvoice(id);
            log.info("Invoice {} deleted successfully", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Failed to delete invoice {}: {}", id, e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to delete invoice", e.getMessage()));
        }
    }

    private void validateInvoice(Invoice invoice) {
        if (invoice.getNumClient() == null || invoice.getNumClient().trim().isEmpty()) {
            throw new IllegalArgumentException("Client number is required");
        }
        if (invoice.getTotal() == null || invoice.getTotal() < 0) {
            throw new IllegalArgumentException("Total amount must be non-negative");
        }
        if (invoice.getDate() == null || invoice.getDate().trim().isEmpty()) {
            throw new IllegalArgumentException("Date cannot be empty");
        }
        if (invoice.getEmploye() == null || invoice.getEmploye().trim().isEmpty()) {
            throw new IllegalArgumentException("Employee cannot be empty");
        }
        if (invoice.getListItems() != null && !invoice.getListItems().isEmpty()) {
            for (ListItem item : invoice.getListItems()) {
                if (item.getQuantite() == null || item.getQuantite() < 0) {
                    throw new IllegalArgumentException("List item quantity must be non-negative");
                }
                if (item.getPrice() == null || item.getPrice() < 0) {
                    throw new IllegalArgumentException("List item price must be non-negative");
                }
            }
        }
    }
}



