package com.ishyiga.service;

import com.ishyiga.entities.RefundCancelled;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RefundCancelledService {
    Page<RefundCancelled> getAllRefundsCancelled(Pageable pageable);
    RefundCancelled saveRefundCancelled(RefundCancelled refundCancelled);
    void deleteRefundCancelled(Long id);
}
