package com.id.martin.workspace.martinworkspace.worklog;

import com.id.martin.workspace.martinworkspace.AbstractIntegrationTest;
import com.id.martin.workspace.martinworkspace.task.Task;
import com.id.martin.workspace.martinworkspace.task.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WorkLogControllerIntegrationTest extends AbstractIntegrationTest {

    @Test
    void workLogCrudFlowPersistsAndKeepsTaskRelation() throws Exception {
        Task task = createTask("Prepare deploy", "Draft VPS checklist");

        String createResponse = mockMvc.perform(post("/api/tasks/{taskId}/work-logs", task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "logDate": "2026-04-11",
                                  "note": "Created initial deployment notes"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.taskId").value(task.getId()))
                .andExpect(jsonPath("$.logDate").value("2026-04-11"))
                .andExpect(jsonPath("$.note").value("Created initial deployment notes"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        WorkLogResponse createdWorkLog = objectMapper.readValue(createResponse, WorkLogResponse.class);
        assertThat(createdWorkLog.id()).isNotNull();
        WorkLog persisted = workLogRepository.findById(createdWorkLog.id()).orElseThrow();
        assertThat(persisted.getTask().getId()).isEqualTo(task.getId());
        assertThat(persisted.getNote()).isEqualTo("Created initial deployment notes");

        mockMvc.perform(get("/api/tasks/{taskId}/work-logs", task.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(createdWorkLog.id()))
                .andExpect(jsonPath("$[0].taskId").value(task.getId()));

        mockMvc.perform(get("/api/tasks/{taskId}", task.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workLogs.length()").value(1))
                .andExpect(jsonPath("$.workLogs[0].taskId").value(task.getId()));

        mockMvc.perform(put("/api/tasks/{taskId}/work-logs/{workLogId}", task.getId(), createdWorkLog.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "logDate": "2026-04-12",
                                  "note": "Updated deployment checklist"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.logDate").value("2026-04-12"))
                .andExpect(jsonPath("$.note").value("Updated deployment checklist"));

        WorkLog updated = workLogRepository.findById(createdWorkLog.id()).orElseThrow();
        assertThat(updated.getLogDate().toString()).isEqualTo("2026-04-12");
        assertThat(updated.getNote()).isEqualTo("Updated deployment checklist");

        mockMvc.perform(delete("/api/tasks/{taskId}/work-logs/{workLogId}", task.getId(), createdWorkLog.id()))
                .andExpect(status().isNoContent());

        assertThat(workLogRepository.findById(createdWorkLog.id())).isEmpty();
    }

    @Test
    void workLogValidationReturnsBadRequestWithErrorBody() throws Exception {
        Task task = createTask("Track progress", "Need daily work log");

        mockMvc.perform(post("/api/tasks/{taskId}/work-logs", task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "note": " "
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details.length()").value(2));
    }

    @Test
    void workLogNotFoundReturns404WithErrorBody() throws Exception {
        Task task = createTask("Track progress", "Need daily work log");

        mockMvc.perform(get("/api/tasks/{taskId}/work-logs", 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Task with id 999 was not found"));

        mockMvc.perform(put("/api/tasks/{taskId}/work-logs/{workLogId}", task.getId(), 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "logDate": "2026-04-12",
                                  "note": "Missing work log"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Work log with id 999 for task %d was not found".formatted(task.getId())));
    }

    private Task createTask(String title, String description) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(TaskStatus.TODO);
        return taskRepository.save(task);
    }
}
