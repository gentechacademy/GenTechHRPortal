package com.gentech.hrportal.repository;

import com.gentech.hrportal.entity.Attendance;
import com.gentech.hrportal.entity.Attendance.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    
    List<Attendance> findByEmployeeId(Long employeeId);
    
    List<Attendance> findByEmployeeIdAndAttendanceDateBetween(Long employeeId, LocalDate startDate, LocalDate endDate);
    
    List<Attendance> findByCompanyId(Long companyId);
    
    List<Attendance> findByCompanyIdAndApprovalStatus(Long companyId, ApprovalStatus approvalStatus);
    
    List<Attendance> findByApprovalStatus(ApprovalStatus approvalStatus);
    
    @Query("SELECT a FROM Attendance a WHERE a.employee.id = :employeeId AND a.attendanceDate = :date")
    Attendance findByEmployeeIdAndAttendanceDate(@Param("employeeId") Long employeeId, @Param("date") LocalDate date);
    
    @Query("SELECT a FROM Attendance a WHERE a.company.id = :companyId AND a.attendanceDate BETWEEN :startDate AND :endDate")
    List<Attendance> findByCompanyIdAndDateRange(@Param("companyId") Long companyId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    boolean existsByEmployeeIdAndAttendanceDate(Long employeeId, LocalDate attendanceDate);
    
    void deleteByEmployeeId(Long employeeId);
    
    /**
     * Find all attendance records approved by a specific user
     * @param approvedById the approver user ID
     * @return list of attendance records
     */
    List<Attendance> findByApprovedById(Long approvedById);
}
