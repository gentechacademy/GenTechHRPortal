package com.gentech.hrportal.repository;

import com.gentech.hrportal.entity.EmployeeProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface EmployeeProfileRepository extends JpaRepository<EmployeeProfile, Long> {
    Optional<EmployeeProfile> findByUserId(Long userId);
    List<EmployeeProfile> findByCompanyId(Long companyId);
    boolean existsByEmployeeCode(String employeeCode);
    
    /**
     * Find employees by employee code containing the search term (case-insensitive)
     * @param code the employee code to search for
     * @return list of matching employee profiles
     */
    List<EmployeeProfile> findByEmployeeCodeContainingIgnoreCase(String code);
    
    /**
     * Find employees by user full name containing the search term (case-insensitive)
     * @param name the name to search for
     * @return list of matching employee profiles
     */
    @Query("SELECT ep FROM EmployeeProfile ep JOIN ep.user u WHERE LOWER(u.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<EmployeeProfile> findByUserFullNameContainingIgnoreCase(@Param("name") String name);
    
    /**
     * Search employees by employee code or user full name (case-insensitive)
     * @param searchTerm the search term for employee code or name
     * @return list of matching employee profiles
     */
    @Query("SELECT ep FROM EmployeeProfile ep JOIN ep.user u WHERE " +
           "LOWER(ep.employeeCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<EmployeeProfile> searchByEmployeeCodeOrName(@Param("searchTerm") String searchTerm);
}
