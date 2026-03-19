package com.gentech.hrportal.repository;

import com.gentech.hrportal.entity.ResignationRequest;
import com.gentech.hrportal.entity.ResignationRequest.ResignationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResignationRepository extends JpaRepository<ResignationRequest, Long> {
    
    List<ResignationRequest> findByEmployeeId(Long employeeId);
    
    List<ResignationRequest> findByStatus(ResignationStatus status);
    
    List<ResignationRequest> findByEmployee_Company_Id(Long companyId);
    
    List<ResignationRequest> findByStatusAndEmployee_Company_Id(ResignationStatus status, Long companyId);
    
    List<ResignationRequest> findByEmployeeIdAndStatus(Long employeeId, ResignationStatus status);
    
    boolean existsByEmployeeIdAndStatusIn(Long employeeId, List<ResignationStatus> statuses);
}
