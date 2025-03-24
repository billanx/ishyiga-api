package com.ishyiga.service;

import com.ishyiga.entities.Invoice;
import com.ishyiga.model.Response;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface InvoiceService {
    Invoice saveInvoice(Invoice invoice);
    List<Invoice> getAllInvoices();
    Optional<Invoice> getInvoiceById(Integer id);
    Response updateInvoice(Integer id, Invoice invoice);
    Invoice saveInvoiceWithListItems(Invoice invoice);
    void deleteInvoice(Integer id);
}

