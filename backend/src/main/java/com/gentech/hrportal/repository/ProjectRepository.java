package com.gentech.hrportal.repository;

import com.gentech.hrportal.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByManagerId(Long managerId);

    List<Project> findByCompanyId(Long companyId);

    List<Project> findByManagerIdAndStatus(Long managerId, Project.ProjectStatus status);

    List<Project> findByCompanyIdAndStatus(Long companyId, Project.ProjectStatus status);

    boolean existsByNameAndCompanyId(String name, Long companyId);
}
