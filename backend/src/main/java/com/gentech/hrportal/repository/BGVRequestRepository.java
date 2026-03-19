package com.gentech.hrportal.repository;

import com.gentech.hrportal.entity.BGVRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BGVRequestRepository extends JpaRepository<BGVRequest, Long> {
    
    List<BGVRequest> findByEmployeeId(Long employeeId);
    
    Optional<BGVRequest> findByEmployeeIdAndStatusNot(Long employeeId, BGVRequest.BGVStatus status);
    
    List<BGVRequest> findByCompanyId(Long companyId);
    
    List<BGVRequest> findByCompanyIdAndStatus(Long companyId, BGVRequest.BGVStatus status);
    
    List<BGVRequest> findByStatus(BGVRequest.BGVStatus status);
    
    Optional<BGVRequest> findByIdAndCompanyId(Long id, Long companyId);
}
