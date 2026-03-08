package com.gentech.hrportal.repository;

import com.gentech.hrportal.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    List<TeamMember> findByProjectId(Long projectId);

    List<TeamMember> findByEmployeeId(Long employeeId);

    Optional<TeamMember> findByProjectIdAndEmployeeId(Long projectId, Long employeeId);

    boolean existsByProjectIdAndEmployeeId(Long projectId, Long employeeId);

    List<TeamMember> findByProjectIdAndStatus(Long projectId, TeamMember.AllocationStatus status);

    void deleteByProjectIdAndEmployeeId(Long projectId, Long employeeId);
}
