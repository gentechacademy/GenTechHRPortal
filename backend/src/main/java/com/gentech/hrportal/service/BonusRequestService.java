package com.gentech.hrportal.service;

import com.gentech.hrportal.dto.BonusRequestDto;
import com.gentech.hrportal.entity.BonusRequest;
import com.gentech.hrportal.entity.BonusRequest.BonusStatus;
import com.gentech.hrportal.entity.Company;
import com.gentech.hrportal.entity.User;
import com.gentech.hrportal.repository.BonusRequestRepository;
import com.gentech.hrportal.repository.CompanyRepository;
import com.gentech.hrportal.repository.SalarySlipRepository;
import com.gentech.hrportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BonusRequestService {

    @Autowired
    private BonusRequestRepository bonusRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private SalarySlipRepository salarySlipRepository;

    @Transactional
    public BonusRequest createBonusRequest(BonusRequestDto dto, Long requestedById) {
        User employee = userRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        User requestedBy = userRepository.findById(requestedById)
                .orElseThrow(() -> new RuntimeException("Requester not found"));
        
        Company company = employee.getCompany();
        if (company == null) {
            throw new RuntimeException("Employee does not belong to any company");
        }

        // Check if salary slip already exists for this employee/month/year
        boolean salarySlipExists = salarySlipRepository.existsByEmployeeIdAndMonthAndYear(
                dto.getEmployeeId(), dto.getMonth(), dto.getYear());
        if (salarySlipExists) {
            throw new RuntimeException("Cannot create bonus request: Salary slip already generated for this employee for the specified month and year");
        }

        BonusRequest request = BonusRequest.builder()
                .employee(employee)
                .company(company)
                .amount(dto.getAmount())
                .reason(dto.getReason())
                .month(dto.getMonth())
                .year(dto.getYear())
                .status(BonusStatus.PENDING)
                .requestedBy(requestedBy)
                .requestedDate(LocalDateTime.now())
                .build();

        return bonusRequestRepository.save(request);
    }

    @Transactional
    public BonusRequest approveBonusRequest(Long requestId, Long approvedById) {
        BonusRequest request = bonusRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Bonus request not found"));

        if (request.getStatus() != BonusStatus.PENDING) {
            throw new RuntimeException("Request has already been processed");
        }

        User approvedBy = userRepository.findById(approvedById)
                .orElseThrow(() -> new RuntimeException("Approver not found"));

        request.setStatus(BonusStatus.APPROVED);
        request.setApprovedBy(approvedBy);
        request.setApprovalDate(LocalDateTime.now());

        return bonusRequestRepository.save(request);
    }

    @Transactional
    public BonusRequest rejectBonusRequest(Long requestId, Long approvedById, String rejectionReason) {
        BonusRequest request = bonusRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Bonus request not found"));

        if (request.getStatus() != BonusStatus.PENDING) {
            throw new RuntimeException("Request has already been processed");
        }

        User approvedBy = userRepository.findById(approvedById)
                .orElseThrow(() -> new RuntimeException("Approver not found"));

        request.setStatus(BonusStatus.REJECTED);
        request.setApprovedBy(approvedBy);
        request.setApprovalDate(LocalDateTime.now());
        request.setRejectionReason(rejectionReason);

        return bonusRequestRepository.save(request);
    }

    public List<BonusRequest> getCompanyBonusRequests(Long companyId) {
        return bonusRequestRepository.findByCompanyId(companyId);
    }

    public List<BonusRequest> getCompanyBonusRequestsByStatus(Long companyId, BonusStatus status) {
        return bonusRequestRepository.findByCompanyIdAndStatus(companyId, status);
    }

    public List<BonusRequest> getEmployeeBonusRequests(Long employeeId) {
        return bonusRequestRepository.findByEmployeeId(employeeId);
    }

    public List<BonusRequest> getMyBonusRequests(Long requestedById) {
        return bonusRequestRepository.findByRequestedById(requestedById);
    }

    public List<BonusRequest> getAllPendingRequests() {
        return bonusRequestRepository.findByStatus(BonusStatus.PENDING);
    }

    public List<BonusRequest> getAllBonusRequests() {
        return bonusRequestRepository.findAll();
    }

    public BonusRequest getBonusRequestById(Long id) {
        return bonusRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bonus request not found"));
    }

    public Double getTotalApprovedBonusForEmployeeMonth(Long employeeId, Integer month, Integer year) {
        List<BonusRequest> approvedBonuses = bonusRequestRepository
                .findByEmployeeIdAndMonthAndYearAndStatus(employeeId, month, year, BonusStatus.APPROVED);
        
        return approvedBonuses.stream()
                .mapToDouble(BonusRequest::getAmount)
                .sum();
    }

    public boolean hasApprovedBonusForEmployeeMonth(Long employeeId, Integer month, Integer year) {
        return bonusRequestRepository.existsByEmployeeIdAndMonthAndYearAndStatus(
                employeeId, month, year, BonusStatus.APPROVED);
    }

    public boolean hasPendingBonusForEmployeeMonth(Long employeeId, Integer month, Integer year) {
        return bonusRequestRepository.existsByEmployeeIdAndMonthAndYearAndStatus(
                employeeId, month, year, BonusStatus.PENDING);
    }

    @Transactional
    public void deleteBonusRequest(Long id) {
        BonusRequest request = bonusRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bonus request not found"));
        
        if (request.getStatus() == BonusStatus.APPROVED) {
            throw new RuntimeException("Cannot delete an approved bonus request");
        }
        
        bonusRequestRepository.delete(request);
    }
}
