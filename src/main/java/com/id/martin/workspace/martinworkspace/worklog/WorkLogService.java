package com.id.martin.workspace.martinworkspace.worklog;

import com.id.martin.workspace.martinworkspace.common.ResourceNotFoundException;
import com.id.martin.workspace.martinworkspace.task.Task;
import com.id.martin.workspace.martinworkspace.task.TaskService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WorkLogService {

    private final WorkLogRepository workLogRepository;
    private final TaskService taskService;

    public WorkLogService(WorkLogRepository workLogRepository, TaskService taskService) {
        this.workLogRepository = workLogRepository;
        this.taskService = taskService;
    }

    public WorkLogResponse create(Long taskId, WorkLogRequest request) {
        Task task = taskService.getTask(taskId);

        WorkLog workLog = new WorkLog();
        workLog.setTask(task);
        workLog.setLogDate(request.logDate());
        workLog.setNote(request.note().trim());
        task.touch();
        return WorkLogResponse.from(workLogRepository.save(workLog));
    }

    @Transactional(readOnly = true)
    public List<WorkLogResponse> findByTaskId(Long taskId) {
        taskService.getTask(taskId);
        return workLogRepository.findByTaskIdOrderByLogDateDescCreatedAtDesc(taskId)
                .stream()
                .map(WorkLogResponse::from)
                .toList();
    }

    public WorkLogResponse update(Long taskId, Long workLogId, WorkLogRequest request) {
        WorkLog workLog = getWorkLog(taskId, workLogId);
        workLog.setLogDate(request.logDate());
        workLog.setNote(request.note().trim());
        workLog.touch();
        workLog.getTask().touch();
        return WorkLogResponse.from(workLog);
    }

    public void delete(Long taskId, Long workLogId) {
        WorkLog workLog = getWorkLog(taskId, workLogId);
        workLog.getTask().touch();
        workLogRepository.delete(workLog);
    }

    private WorkLog getWorkLog(Long taskId, Long workLogId) {
        return workLogRepository.findByIdAndTaskId(workLogId, taskId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Work log with id " + workLogId + " for task " + taskId + " was not found"
                ));
    }
}
