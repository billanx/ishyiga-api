package com.ishyiga.service.impl;

import com.ishyiga.entities.Invoice;
import com.ishyiga.entities.ListItem;
import com.ishyiga.exception.BadRequestException;
import com.ishyiga.exception.DatabaseException;
import com.ishyiga.model.Response;
import com.ishyiga.repo.InvoiceRepository;
import com.ishyiga.repo.ListItemRepository;
import com.ishyiga.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ListItemRepository listItemRepository;

    @Override
    public Invoice saveInvoice(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    @Override
    public Page<Invoice> getAllInvoices(Pageable pageable) {
        return invoiceRepository.findAll(pageable);
    }

    @Override
    public Optional<Invoice> getInvoiceById(Integer id) {
        return invoiceRepository.findById(id);
    }

    @Override
    public Response updateInvoice(Integer id, Invoice updatedInvoice) {
        Response response = new Response();

        return invoiceRepository.findById(id).map(invoice -> {
            invoice.setStatus(updatedInvoice.getStatus());
            invoiceRepository.save(invoice);
            response.setStatus(true);
            response.setMessage("Notification Received");
            return response;
        }).orElseGet(() -> {
            response.setStatus(false);
            response.setMessage("Invoice not found");
            return response;
        });
    }


    @Override
    public void deleteInvoice(Integer id) {
        invoiceRepository.deleteById(id);
    }


    @Transactional
    @Override
    public Invoice saveInvoiceWithListItems(Invoice invoice) {
        try {

            // Save the invoice first
            Invoice savedInvoice = invoiceRepository.save(invoice);

            // Save associated list items if there are any
            if (invoice.getListItems() != null && !invoice.getListItems().isEmpty()) {
                for (ListItem listItem : invoice.getListItems()) {
                    listItem.setInvoice(savedInvoice);  // Set the relationship
                    listItemRepository.save(listItem);  // Save each list item
                }
            }

            return savedInvoice;
        } catch (Exception e) {
            throw new DatabaseException("Error while saving the invoice: " + e.getCause());
        }
    }
}

