package com.ishyiga.service;

import com.ishyiga.entities.Invoice;
import com.ishyiga.model.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface InvoiceService {
    Invoice saveInvoice(Invoice invoice);
    Page<Invoice> getAllInvoices(Pageable pageable);
    Optional<Invoice> getInvoiceById(Integer id);

    Optional<Invoice> getInvoiceByIdInvoice(Integer id);

    Response updateInvoice(Integer id, Invoice invoice);
    Invoice saveInvoiceWithListItems(Invoice invoice);
    void deleteInvoice(Integer id);
}

