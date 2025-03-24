package com.ishyiga.controller;

import com.ishyiga.entities.Invoice;
import com.ishyiga.exception.DatabaseException;
import com.ishyiga.exception.GlobalExceptionHandler;
import com.ishyiga.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/invoices")
@Slf4j
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA')")
    public ResponseEntity<?> createInvoice(@RequestBody Invoice invoice) {
        try {
            Invoice createdInvoice = invoiceService.saveInvoiceWithListItems(invoice);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdInvoice);
        }catch (Exception e){
            log.error("EXCEPTION ERROR {}",e.getMessage());
            return new ResponseEntity<> (new DatabaseException(e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRoles('ADMIN','ISHYIGA','BANK)")
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA','BANK')")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable Integer id) {
        Optional<Invoice> invoice = invoiceService.getInvoiceById(id);
        return invoice.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA','BANK')")
    public ResponseEntity<?> updateInvoice(@PathVariable Integer id, @RequestBody Invoice invoice) {
        return new ResponseEntity<>(invoiceService.updateInvoice(id, invoice),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ISHYIGA')")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Integer id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }

}

