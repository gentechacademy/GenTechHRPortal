package com.gentech.hrportal.repository;

import com.gentech.hrportal.entity.BonusRequest;
import com.gentech.hrportal.entity.BonusRequest.BonusStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BonusRequestRepository extends JpaRepository<BonusRequest, Long> {
    
    List<BonusRequest> findByCompanyId(Long companyId);
    
    List<BonusRequest> findByCompanyIdAndStatus(Long companyId, BonusStatus status);
    
    List<BonusRequest> findByEmployeeId(Long employeeId);
    
    List<BonusRequest> findByRequestedById(Long requestedById);
    
    List<BonusRequest> findByStatus(BonusStatus status);
    
    List<BonusRequest> findByEmployeeIdAndMonthAndYearAndStatus(Long employeeId, Integer month, Integer year, BonusStatus status);
    
    boolean existsByEmployeeIdAndMonthAndYearAndStatus(Long employeeId, Integer month, Integer year, BonusStatus status);
}
