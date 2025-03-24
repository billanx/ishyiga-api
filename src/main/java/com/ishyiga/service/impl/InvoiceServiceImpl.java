package com.ishyiga.service.impl;

import com.ishyiga.entities.Invoice;
import com.ishyiga.entities.ListItem;
import com.ishyiga.exception.DatabaseException;
import com.ishyiga.repo.InvoiceRepository;
import com.ishyiga.repo.ListItemRepository;
import com.ishyiga.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    @Override
    public Optional<Invoice> getInvoiceById(Integer id) {
        return invoiceRepository.findById(id);
    }

    @Override
    public Invoice updateInvoice(Integer id, Invoice updatedInvoice) {
        return invoiceRepository.findById(id).map(invoice -> {
            invoice.setNumClient(updatedInvoice.getNumClient());
            invoice.setDate(updatedInvoice.getDate());
            invoice.setTotal(updatedInvoice.getTotal());
            return invoiceRepository.save(invoice);
        }).orElseThrow(() -> new RuntimeException("Invoice not found"));
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

