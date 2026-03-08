package com.gentech.hrportal.repository;

import com.gentech.hrportal.entity.ProfileEditRequest;
import com.gentech.hrportal.entity.ProfileEditRequest.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileEditRequestRepository extends JpaRepository<ProfileEditRequest, Long> {
    
    /**
     * Find all edit requests by employee ID
     * @param employeeId the employee ID
     * @return list of edit requests
     */
    List<ProfileEditRequest> findByEmployeeId(Long employeeId);
    
    /**
     * Find all edit requests by company ID
     * @param companyId the company ID
     * @return list of edit requests
     */
    List<ProfileEditRequest> findByCompanyId(Long companyId);
    
    /**
     * Find all edit requests by company ID and status
     * @param companyId the company ID
     * @param status the request status
     * @return list of edit requests
     */
    List<ProfileEditRequest> findByCompanyIdAndStatus(Long companyId, RequestStatus status);
    
    /**
     * Find all edit requests by status
     * @param status the request status
     * @return list of edit requests
     */
    List<ProfileEditRequest> findByStatus(RequestStatus status);
    
    /**
     * Find all edit requests where user is the requester
     * @param requestedById the requester user ID
     * @return list of edit requests
     */
    List<ProfileEditRequest> findByRequestedById(Long requestedById);
    
    /**
     * Find all edit requests where user is the approver
     * @param approvedById the approver user ID
     * @return list of edit requests
     */
    List<ProfileEditRequest> findByApprovedById(Long approvedById);
}
