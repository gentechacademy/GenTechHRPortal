package com.gentech.hrportal.repository;

import com.gentech.hrportal.entity.Leave;
import com.gentech.hrportal.entity.Leave.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {
    
    List<Leave> findByEmployeeId(Long employeeId);
    
    List<Leave> findByCompanyId(Long companyId);
    
    List<Leave> findByCompanyIdAndStatus(Long companyId, LeaveStatus status);
    
    List<Leave> findByStatus(LeaveStatus status);
    
    List<Leave> findByEmployeeIdAndStatus(Long employeeId, LeaveStatus status);
    
    @Query("SELECT l FROM Leave l WHERE l.employee.id = :employeeId " +
           "AND l.startDate <= :endDate AND l.endDate >= :startDate " +
           "AND l.status != 'REJECTED'")
    List<Leave> findOverlappingLeaves(@Param("employeeId") Long employeeId, 
                                      @Param("startDate") LocalDate startDate, 
                                      @Param("endDate") LocalDate endDate);
    
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Leave l " +
           "WHERE l.employee.id = :employeeId " +
           "AND l.startDate <= :endDate AND l.endDate >= :startDate " +
           "AND l.status != 'REJECTED'")
    boolean existsByEmployeeIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            @Param("employeeId") Long employeeId, 
            @Param("endDate") LocalDate endDate, 
            @Param("startDate") LocalDate startDate);
    
    /**
     * Find all leaves where user is the applicant
     * @param appliedById the applicant user ID
     * @return list of leaves
     */
    List<Leave> findByAppliedById(Long appliedById);
    
    /**
     * Find all leaves where user is the approver
     * @param approvedById the approver user ID
     * @return list of leaves
     */
    List<Leave> findByApprovedById(Long approvedById);
}
