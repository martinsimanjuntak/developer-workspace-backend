package com.id.martin.workspace.martinworkspace.worklog;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkLogRepository extends JpaRepository<WorkLog, Long> {

    List<WorkLog> findByTaskIdOrderByLogDateDescCreatedAtDesc(Long taskId);

    Optional<WorkLog> findByIdAndTaskId(Long id, Long taskId);
}
