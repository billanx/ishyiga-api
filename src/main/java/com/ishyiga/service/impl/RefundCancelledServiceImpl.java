package com.ishyiga.service.impl;

import com.ishyiga.entities.RefundCancelled;
import com.ishyiga.exception.DatabaseException;
import com.ishyiga.repo.RefundCancelledRepository;
import com.ishyiga.service.RefundCancelledService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RefundCancelledServiceImpl implements RefundCancelledService {
    @Autowired
    private RefundCancelledRepository refundCancelledRepository;

    @Override
    public Page<RefundCancelled> getAllRefundsCancelled(Pageable pageable) {
        try {
            return refundCancelledRepository.findAll(pageable);
        } catch (Exception e) {
            log.error("Error retrieving refunds cancelled: {}", e.getMessage(), e);
            throw new DatabaseException("Error retrieving refunds cancelled: " + e.getMessage());
        }
    }

    @Override
    public RefundCancelled saveRefundCancelled(RefundCancelled refundCancelled) {
        try {
            return refundCancelledRepository.save(refundCancelled);
        } catch (Exception e) {
            log.error("Error saving refund cancelled: {}", e.getMessage(), e);
            throw new DatabaseException("Error saving refund cancelled: " + e.getMessage());
        }
    }

    @Override
    public void deleteRefundCancelled(Long id) {
        try {
            refundCancelledRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Error deleting refund cancelled with ID {}: {}", id, e.getMessage(), e);
            throw new DatabaseException("Error deleting refund cancelled: " + e.getMessage());
        }
    }
}
