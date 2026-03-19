package com.gentech.hrportal.service;

import com.gentech.hrportal.dto.EmployeeFullDetailsResponse;
import com.gentech.hrportal.dto.EmployeeProfileResponse;
import com.gentech.hrportal.entity.*;
import com.gentech.hrportal.entity.Attendance.AttendanceStatus;
import com.gentech.hrportal.entity.Leave.LeaveStatus;
import com.gentech.hrportal.entity.Leave.LeaveType;
import com.gentech.hrportal.entity.Project.ProjectStatus;
import com.gentech.hrportal.entity.ResignationRequest.ResignationStatus;
import com.gentech.hrportal.entity.SalarySlip.SalarySlipStatus;
import com.gentech.hrportal.entity.TeamMember.AllocationStatus;
import com.gentech.hrportal.repository.*;
import com.gentech.hrportal.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for employee search and comprehensive details retrieval
 */
@Service
public class EmployeeSearchService {

    @Autowired
    private EmployeeProfileRepository employeeProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private SalarySlipRepository salarySlipRepository;

    @Autowired
    private ResignationRepository resignationRepository;

    // Cache for employee profile responses to reduce repeated lookups
    private final Map<Long, EmployeeProfileResponse> profileCache = new HashMap<>();

    /**
     * Clear cache periodically or when needed
     */
    public void clearCache() {
        profileCache.clear();
    }

    /**
     * Search employees by employee code or name
     * @param searchTerm can be employee code or name (full or partial)
     * @return list of matching employee profiles
     */
    public List<EmployeeProfileResponse> searchEmployee(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        String normalizedSearchTerm = searchTerm.trim().toLowerCase();
        
        // Get admin's company for filtering
        Company adminCompany = getCurrentAdminCompany();
        if (adminCompany == null) {
            throw new RuntimeException("Admin is not assigned to any company");
        }
        
        // Search by employee code or name within admin's company
        List<EmployeeProfile> profiles = employeeProfileRepository.searchByEmployeeCodeOrName(normalizedSearchTerm);
        
        // Filter by company and map to response
        return profiles.stream()
                .filter(profile -> profile.getCompany() != null && 
                        profile.getCompany().getId().equals(adminCompany.getId()))
                .map(this::mapToProfileResponse)
                .collect(Collectors.toList());
    }

    /**
     * Search employees by employee code
     * @param code employee code (can be partial)
     * @return list of matching employee profiles
     */
    public List<EmployeeProfileResponse> searchByEmployeeCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        Company adminCompany = getCurrentAdminCompany();
        if (adminCompany == null) {
            throw new RuntimeException("Admin is not assigned to any company");
        }
        
        List<EmployeeProfile> profiles = employeeProfileRepository.findByEmployeeCodeContainingIgnoreCase(code.trim());
        
        return profiles.stream()
                .filter(profile -> profile.getCompany() != null && 
                        profile.getCompany().getId().equals(adminCompany.getId()))
                .map(this::mapToProfileResponse)
                .collect(Collectors.toList());
    }

    /**
     * Search employees by name
     * @param name employee name (can be partial)
     * @return list of matching employee profiles
     */
    public List<EmployeeProfileResponse> searchByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        Company adminCompany = getCurrentAdminCompany();
        if (adminCompany == null) {
            throw new RuntimeException("Admin is not assigned to any company");
        }
        
        List<EmployeeProfile> profiles = employeeProfileRepository.findByUserFullNameContainingIgnoreCase(name.trim());
        
        return profiles.stream()
                .filter(profile -> profile.getCompany() != null && 
                        profile.getCompany().getId().equals(adminCompany.getId()))
                .map(this::mapToProfileResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get comprehensive details for an employee - Optimized version
     * Loads heavy data on demand to improve initial response time
     * @param employeeId the employee user ID
     * @return full employee details
     */
    @Transactional(readOnly = true)
    public EmployeeFullDetailsResponse getEmployeeFullDetails(Long employeeId) {
        // Verify admin has access to this employee
        Company adminCompany = getCurrentAdminCompany();
        if (adminCompany == null) {
            throw new RuntimeException("Admin is not assigned to any company");
        }
        
        EmployeeProfile profile = employeeProfileRepository.findByUserId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        // Verify employee belongs to admin's company
        if (profile.getCompany() == null || !profile.getCompany().getId().equals(adminCompany.getId())) {
            throw new RuntimeException("Employee not found in your company");
        }
        
        User user = profile.getUser();
        if (user == null) {
            throw new RuntimeException("Employee user data not found");
        }
        
        EmployeeFullDetailsResponse.Builder builder = EmployeeFullDetailsResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .employeeCode(profile.getEmployeeCode())
                .department(profile.getDepartment())
                .designation(profile.getDesignation())
                .dateOfJoining(profile.getDateOfJoining())
                .dateOfBirth(profile.getDateOfBirth())
                .phoneNumber(profile.getPhoneNumber())
                .address(profile.getAddress())
                .emergencyContact(profile.getEmergencyContact())
                .salary(profile.getSalary())
                .profilePictureUrl(profile.getProfilePictureUrl())
                .companyId(adminCompany.getId())
                .companyName(adminCompany.getName())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt());
        
        // Determine employment status - lightweight
        determineEmploymentStatus(builder, employeeId);
        
        // Return basic response - heavy data can be loaded via separate endpoint
        return builder.build();
    }

    /**
     * Get employee work history
     * @param employeeId the employee user ID
     * @return work history details
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getEmployeeWorkHistory(Long employeeId) {
        // Verify admin has access
        Company adminCompany = getCurrentAdminCompany();
        if (adminCompany == null) {
            throw new RuntimeException("Admin is not assigned to any company");
        }
        
        EmployeeProfile profile = employeeProfileRepository.findByUserId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        if (profile.getCompany() == null || !profile.getCompany().getId().equals(adminCompany.getId())) {
            throw new RuntimeException("Employee not found in your company");
        }
        
        Map<String, Object> workHistory = new HashMap<>();
        
        // Basic employee info
        User user = profile.getUser();
        workHistory.put("employeeId", employeeId);
        workHistory.put("employeeName", user != null ? user.getFullName() : null);
        workHistory.put("employeeCode", profile.getEmployeeCode());
        workHistory.put("dateOfJoining", profile.getDateOfJoining());
        workHistory.put("department", profile.getDepartment());
        workHistory.put("designation", profile.getDesignation());
        
        // Project history
        List<EmployeeFullDetailsResponse.ProjectDetail> allProjects = new ArrayList<>();
        allProjects.addAll(getEmployeeCurrentProjects(employeeId));
        allProjects.addAll(getEmployeePastProjects(employeeId));
        workHistory.put("projects", allProjects);
        
        // Attendance history (last 6 months)
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(6);
        workHistory.put("attendanceSummary", getEmployeeAttendanceSummary(employeeId, startDate, endDate));
        
        // Leave history
        workHistory.put("leaveSummary", getEmployeeLeaveSummary(employeeId));
        
        // Resignation history
        List<ResignationRequest> resignations = resignationRepository.findByEmployeeId(employeeId);
        workHistory.put("resignations", resignations.stream()
                .map(this::mapResignationToMap)
                .collect(Collectors.toList()));
        
        // Salary history
        workHistory.put("salaryHistory", getEmployeeSalarySlipSummary(employeeId));
        
        return workHistory;
    }

    /**
     * Get attendance summary for an employee
     * @param employeeId the employee user ID
     * @param from start date (optional)
     * @param to end date (optional)
     * @return attendance summary
     */
    public EmployeeFullDetailsResponse.AttendanceSummary getEmployeeAttendanceSummary(
            Long employeeId, LocalDate from, LocalDate to) {
        
        LocalDate startDate = from != null ? from : LocalDate.now().minusMonths(1);
        LocalDate endDate = to != null ? to : LocalDate.now();
        
        List<Attendance> attendances = attendanceRepository
                .findByEmployeeIdAndAttendanceDateBetween(employeeId, startDate, endDate);
        
        EmployeeFullDetailsResponse.AttendanceSummary summary = 
                new EmployeeFullDetailsResponse.AttendanceSummary();
        
        if (attendances == null || attendances.isEmpty()) {
            summary.setTotalDays(0);
            summary.setPresentDays(0);
            summary.setAbsentDays(0);
            summary.setLeaveDays(0);
            summary.setWorkFromHomeDays(0);
            summary.setHalfDays(0);
            summary.setAttendancePercentage(0.0);
            return summary;
        }
        
        int present = 0;
        int absent = 0;
        int leave = 0;
        int wfh = 0;
        int halfDay = 0;
        
        for (Attendance attendance : attendances) {
            if (attendance.getStatus() == null) continue;
            
            switch (attendance.getStatus()) {
                case PRESENT:
                    present++;
                    break;
                case ABSENT:
                    absent++;
                    break;
                case ON_LEAVE:
                    leave++;
                    break;
                case WORK_FROM_HOME:
                    wfh++;
                    break;
                case HALF_DAY:
                    halfDay++;
                    break;
                default:
                    break;
            }
        }
        
        int total = present + absent + leave + wfh + halfDay;
        double percentage = total > 0 ? ((double) (present + wfh + (halfDay * 0.5)) / total) * 100 : 0.0;
        
        summary.setTotalDays(total);
        summary.setPresentDays(present);
        summary.setAbsentDays(absent);
        summary.setLeaveDays(leave);
        summary.setWorkFromHomeDays(wfh);
        summary.setHalfDays(halfDay);
        summary.setAttendancePercentage(Math.round(percentage * 100.0) / 100.0);
        
        return summary;
    }

    /**
     * Get leave summary for an employee
     * @param employeeId the employee user ID
     * @return leave summary
     */
    public EmployeeFullDetailsResponse.LeaveSummary getEmployeeLeaveSummary(Long employeeId) {
        List<Leave> leaves = leaveRepository.findByEmployeeId(employeeId);
        
        EmployeeFullDetailsResponse.LeaveSummary summary = 
                new EmployeeFullDetailsResponse.LeaveSummary();
        
        if (leaves == null || leaves.isEmpty()) {
            summary.setTotalLeaves(0);
            summary.setApprovedLeaves(0);
            summary.setPendingLeaves(0);
            summary.setRejectedLeaves(0);
            summary.setSickLeaves(0);
            summary.setPrivilegeLeaves(0);
            summary.setCasualLeaves(0);
            summary.setEarnedLeaves(0);
            return summary;
        }
        
        int approved = 0;
        int pending = 0;
        int rejected = 0;
        int sl = 0;
        int pl = 0;
        int cl = 0;
        int el = 0;
        
        for (Leave leave : leaves) {
            // Count by status
            if (leave.getStatus() != null) {
                switch (leave.getStatus()) {
                    case APPROVED:
                        approved++;
                        break;
                    case PENDING:
                        pending++;
                        break;
                    case REJECTED:
                        rejected++;
                        break;
                }
            }
            
            // Count by type
            if (leave.getLeaveType() != null) {
                int days = leave.getNumberOfDays() != null ? leave.getNumberOfDays() : 1;
                switch (leave.getLeaveType()) {
                    case SL:
                        sl += days;
                        break;
                    case PL:
                        pl += days;
                        break;
                    case CL:
                        cl += days;
                        break;
                    case EL:
                        el += days;
                        break;
                }
            }
        }
        
        summary.setTotalLeaves(leaves.size());
        summary.setApprovedLeaves(approved);
        summary.setPendingLeaves(pending);
        summary.setRejectedLeaves(rejected);
        summary.setSickLeaves(sl);
        summary.setPrivilegeLeaves(pl);
        summary.setCasualLeaves(cl);
        summary.setEarnedLeaves(el);
        
        return summary;
    }

    /**
     * Get current projects for an employee
     * @param employeeId the employee user ID
     * @return list of current project details
     */
    public List<EmployeeFullDetailsResponse.ProjectDetail> getEmployeeCurrentProjects(Long employeeId) {
        List<TeamMember> teamMembers = teamMemberRepository.findByEmployeeId(employeeId);
        
        if (teamMembers == null || teamMembers.isEmpty()) {
            return Collections.emptyList();
        }
        
        return teamMembers.stream()
                .filter(tm -> tm.getStatus() == AllocationStatus.ACTIVE)
                .map(this::mapTeamMemberToProjectDetail)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Get past projects for an employee
     * @param employeeId the employee user ID
     * @return list of past project details
     */
    public List<EmployeeFullDetailsResponse.ProjectDetail> getEmployeePastProjects(Long employeeId) {
        List<TeamMember> teamMembers = teamMemberRepository.findByEmployeeId(employeeId);
        
        if (teamMembers == null || teamMembers.isEmpty()) {
            return Collections.emptyList();
        }
        
        return teamMembers.stream()
                .filter(tm -> tm.getStatus() != AllocationStatus.ACTIVE)
                .map(this::mapTeamMemberToProjectDetail)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Get project history for an employee (combines current and past projects)
     * @param employeeId the employee user ID
     * @return map with current and past projects
     */
    public Map<String, List<EmployeeFullDetailsResponse.ProjectDetail>> getEmployeeProjectHistory(Long employeeId) {
        Map<String, List<EmployeeFullDetailsResponse.ProjectDetail>> history = new HashMap<>();
        history.put("currentProjects", getEmployeeCurrentProjects(employeeId));
        history.put("pastProjects", getEmployeePastProjects(employeeId));
        return history;
    }

    /**
     * Get salary slip summary for an employee
     * @param employeeId the employee user ID
     * @return salary slip summary
     */
    public EmployeeFullDetailsResponse.SalarySlipSummary getEmployeeSalarySlipSummary(Long employeeId) {
        List<SalarySlip> slips = salarySlipRepository.findByEmployeeId(employeeId);
        
        EmployeeFullDetailsResponse.SalarySlipSummary summary = 
                new EmployeeFullDetailsResponse.SalarySlipSummary();
        
        if (slips == null || slips.isEmpty()) {
            summary.setTotalSlipsGenerated(0);
            summary.setTotalSlipsDownloaded(0);
            summary.setRecentSlips(Collections.emptyList());
            return summary;
        }
        
        // Sort by year and month descending
        slips.sort((a, b) -> {
            int yearCompare = b.getYear().compareTo(a.getYear());
            if (yearCompare != 0) return yearCompare;
            return b.getMonth().compareTo(a.getMonth());
        });
        
        // Count downloaded slips
        long downloaded = slips.stream()
                .filter(s -> s.getStatus() == SalarySlipStatus.DOWNLOADED)
                .count();
        
        // Get last paid details
        SalarySlip latest = slips.get(0);
        
        // Get recent 6 slips
        List<EmployeeFullDetailsResponse.SalarySlipBasicInfo> recentSlips = slips.stream()
                .limit(6)
                .map(this::mapToSalarySlipBasicInfo)
                .collect(Collectors.toList());
        
        summary.setTotalSlipsGenerated(slips.size());
        summary.setTotalSlipsDownloaded((int) downloaded);
        summary.setLastGrossSalary(latest.getGrossSalary());
        summary.setLastNetSalary(latest.getNetSalary());
        summary.setLastPaidMonth(latest.getMonth());
        summary.setLastPaidYear(latest.getYear());
        summary.setLastGeneratedDate(latest.getGeneratedDate());
        summary.setRecentSlips(recentSlips);
        
        return summary;
    }

    // Helper Methods
    
    private void determineEmploymentStatus(EmployeeFullDetailsResponse.Builder builder, Long employeeId) {
        List<ResignationRequest> resignations = resignationRepository.findByEmployeeId(employeeId);
        
        boolean hasActiveResignation = resignations.stream()
                .anyMatch(r -> r.getStatus() == ResignationStatus.APPROVED);
        
        boolean hasPendingResignation = resignations.stream()
                .anyMatch(r -> r.getStatus() == ResignationStatus.PENDING_MANAGER || 
                        r.getStatus() == ResignationStatus.PENDING_ADMIN ||
                        r.getStatus() == ResignationStatus.MANAGER_APPROVED);
        
        if (hasActiveResignation) {
            builder.isActive(false);
            builder.employmentStatus("EXITED");
            
            // Find the approved resignation for last working date
            resignations.stream()
                    .filter(r -> r.getStatus() == ResignationStatus.APPROVED)
                    .findFirst()
                    .ifPresent(r -> builder.lastWorkingDate(r.getActualLastWorkingDay()));
        } else if (hasPendingResignation) {
            builder.isActive(true);
            builder.employmentStatus("RESIGNATION_PENDING");
        } else {
            builder.isActive(true);
            builder.employmentStatus("ACTIVE");
        }
    }
    
    private EmployeeFullDetailsResponse.ExitDetails getEmployeeExitDetails(Long employeeId) {
        List<ResignationRequest> resignations = resignationRepository.findByEmployeeId(employeeId);
        
        if (resignations == null || resignations.isEmpty()) {
            return null;
        }
        
        // Get the most recent resignation
        ResignationRequest latest = resignations.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .findFirst()
                .orElse(null);
        
        if (latest == null) {
            return null;
        }
        
        EmployeeFullDetailsResponse.ExitDetails details = new EmployeeFullDetailsResponse.ExitDetails();
        details.setResignationId(latest.getId());
        details.setRequestDate(latest.getRequestDate());
        details.setProposedLastWorkingDay(latest.getProposedLastWorkingDay());
        details.setActualLastWorkingDay(latest.getActualLastWorkingDay());
        details.setNoticePeriodDays(latest.getNoticePeriodDays());
        details.setReason(latest.getReason());
        details.setStatus(latest.getStatus() != null ? latest.getStatus().name() : null);
        details.setManagerRemarks(latest.getManagerRemarks());
        details.setAdminRemarks(latest.getAdminRemarks());
        details.setManagerApprovedBy(latest.getManagerApprovedBy() != null ? 
                latest.getManagerApprovedBy().getFullName() : null);
        details.setAdminApprovedBy(latest.getAdminApprovedBy() != null ? 
                latest.getAdminApprovedBy().getFullName() : null);
        details.setManagerApprovalDate(latest.getManagerApprovalDate());
        details.setAdminApprovalDate(latest.getAdminApprovalDate());
        
        return details;
    }
    
    private EmployeeFullDetailsResponse.ProjectDetail mapTeamMemberToProjectDetail(TeamMember teamMember) {
        if (teamMember == null || teamMember.getProject() == null) {
            return null;
        }
        
        Project project = teamMember.getProject();
        
        EmployeeFullDetailsResponse.ProjectDetail detail = new EmployeeFullDetailsResponse.ProjectDetail();
        detail.setProjectId(project.getId());
        detail.setProjectName(project.getName());
        detail.setDescription(project.getDescription());
        detail.setRoleInProject(teamMember.getRoleInProject());
        detail.setStatus(teamMember.getStatus() != null ? teamMember.getStatus().name() : null);
        detail.setStartDate(project.getStartDate());
        detail.setEndDate(project.getEndDate());
        detail.setAllocationPercentage(teamMember.getAllocationPercentage());
        detail.setManagerName(project.getManager() != null ? project.getManager().getFullName() : null);
        
        return detail;
    }
    
    private EmployeeFullDetailsResponse.SalarySlipBasicInfo mapToSalarySlipBasicInfo(SalarySlip slip) {
        if (slip == null) {
            return null;
        }
        
        EmployeeFullDetailsResponse.SalarySlipBasicInfo info = 
                new EmployeeFullDetailsResponse.SalarySlipBasicInfo();
        info.setSlipId(slip.getId());
        info.setMonth(slip.getMonth());
        info.setYear(slip.getYear());
        info.setGrossSalary(slip.getGrossSalary());
        info.setNetSalary(slip.getNetSalary());
        info.setStatus(slip.getStatus() != null ? slip.getStatus().name() : null);
        info.setGeneratedDate(slip.getGeneratedDate());
        
        return info;
    }
    
    private EmployeeProfileResponse mapToProfileResponse(EmployeeProfile profile) {
        if (profile == null) {
            return null;
        }
        
        User user = profile.getUser();
        Company company = profile.getCompany();
        
        return EmployeeProfileResponse.builder()
                .id(user != null ? user.getId() : null)
                .username(user != null ? user.getUsername() : null)
                .email(user != null ? user.getEmail() : null)
                .fullName(user != null ? user.getFullName() : null)
                .role(user != null && user.getRole() != null ? user.getRole().name() : null)
                .companyId(company != null ? company.getId() : null)
                .companyName(company != null ? company.getName() : null)
                .companyLogoUrl(company != null ? company.getLogoUrl() : null)
                .employeeCode(profile.getEmployeeCode())
                .department(profile.getDepartment())
                .designation(profile.getDesignation())
                .dateOfJoining(profile.getDateOfJoining())
                .dateOfBirth(profile.getDateOfBirth())
                .phoneNumber(profile.getPhoneNumber())
                .address(profile.getAddress())
                .emergencyContact(profile.getEmergencyContact())
                .salary(profile.getSalary())
                .profilePictureUrl(profile.getProfilePictureUrl())
                .createdAt(profile.getCreatedAt())
                .build();
    }
    
    private Map<String, Object> mapResignationToMap(ResignationRequest resignation) {
        Map<String, Object> map = new HashMap<>();
        if (resignation == null) {
            return map;
        }
        
        map.put("id", resignation.getId());
        map.put("requestDate", resignation.getRequestDate());
        map.put("proposedLastWorkingDay", resignation.getProposedLastWorkingDay());
        map.put("actualLastWorkingDay", resignation.getActualLastWorkingDay());
        map.put("noticePeriodDays", resignation.getNoticePeriodDays());
        map.put("reason", resignation.getReason());
        map.put("status", resignation.getStatus() != null ? resignation.getStatus().name() : null);
        map.put("managerRemarks", resignation.getManagerRemarks());
        map.put("adminRemarks", resignation.getAdminRemarks());
        map.put("managerApprovedBy", resignation.getManagerApprovedBy() != null ? 
                resignation.getManagerApprovedBy().getFullName() : null);
        map.put("adminApprovedBy", resignation.getAdminApprovedBy() != null ? 
                resignation.getAdminApprovedBy().getFullName() : null);
        map.put("managerApprovalDate", resignation.getManagerApprovalDate());
        map.put("adminApprovalDate", resignation.getAdminApprovalDate());
        map.put("createdAt", resignation.getCreatedAt());
        
        return map;
    }
    
    private Company getCurrentAdminCompany() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            return null;
        }
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        return userRepository.findById(userDetails.getId())
                .map(User::getCompany)
                .orElse(null);
    }
}
