package com.id.martin.workspace.martinworkspace.task;

import com.id.martin.workspace.martinworkspace.AbstractIntegrationTest;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TaskControllerIntegrationTest extends AbstractIntegrationTest {

    @Test
    void taskCrudAndStatusFlowPersistsToDatabase() throws Exception {
        String createResponse = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Build API",
                                  "description": "Implement task endpoints"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Build API"))
                .andExpect(jsonPath("$.description").value("Implement task endpoints"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        TaskSummaryResponse createdTask = objectMapper.readValue(createResponse, TaskSummaryResponse.class);
        mockMvc.perform(get("/api/tasks/{taskId}", createdTask.id()))
                .andExpect(status().isOk());
        assertThat(taskRepository.findById(createdTask.id())).isPresent();
        assertThat(taskRepository.findById(createdTask.id()).orElseThrow().getStatus()).isEqualTo(TaskStatus.TODO);

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(createdTask.id()))
                .andExpect(jsonPath("$[0].title").value("Build API"));

        mockMvc.perform(get("/api/tasks/{taskId}", createdTask.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdTask.id()))
                .andExpect(jsonPath("$.title").value("Build API"))
                .andExpect(jsonPath("$.workLogs.length()").value(0));

        mockMvc.perform(put("/api/tasks/{taskId}", createdTask.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Build REST API",
                                  "description": "Refine task endpoints"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Build REST API"))
                .andExpect(jsonPath("$.description").value("Refine task endpoints"));

        assertThat(taskRepository.findById(createdTask.id()).orElseThrow().getTitle()).isEqualTo("Build REST API");

        mockMvc.perform(patch("/api/tasks/{taskId}/status", createdTask.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "IN_PROGRESS"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        assertThat(taskRepository.findById(createdTask.id()).orElseThrow().getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);

        mockMvc.perform(delete("/api/tasks/{taskId}", createdTask.id()))
                .andExpect(status().isNoContent());

        assertThat(taskRepository.findById(createdTask.id())).isEmpty();
    }

    @Test
    void taskValidationReturnsBadRequestWithErrorBody() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": " ",
                                  "description": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details.length()").value(2));
    }

    @Test
    void taskNotFoundReturns404WithErrorBody() throws Exception {
        mockMvc.perform(get("/api/tasks/{taskId}", 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Task with id 999 was not found"))
                .andExpect(jsonPath("$.details.length()").value(0));

        mockMvc.perform(patch("/api/tasks/{taskId}/status", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(Map.of("status", "DONE"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Task with id 999 was not found"));
    }
}
