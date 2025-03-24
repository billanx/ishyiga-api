
package com.ishyiga.service.impl;

import com.ishyiga.entities.RefundCancelled;
import com.ishyiga.repo.RefundCancelledRepository;
import com.ishyiga.service.RefundCancelledService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RefundCancelledServiceImpl implements RefundCancelledService {
    @Autowired
    private RefundCancelledRepository refundCancelledRepository;
    public List<RefundCancelled> getAllRefundsCancelled() { return refundCancelledRepository.findAll(); }
    public RefundCancelled saveRefundCancelled(RefundCancelled refundCancelled) { return refundCancelledRepository.save(refundCancelled); }
    public void deleteRefundCancelled(Long id) { refundCancelledRepository.deleteById(id); }
}
