package com.id.martin.workspace.martinworkspace.snippet;

import com.id.martin.workspace.martinworkspace.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SnippetControllerIntegrationTest extends AbstractIntegrationTest {

    @Test
    void snippetCrudAndSearchFlowPersistsToDatabase() throws Exception {
        String createResponse = mockMvc.perform(post("/api/snippets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Spring Boot health check",
                                  "content": "curl /actuator/health",
                                  "category": "DEVOPS"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Spring Boot health check"))
                .andExpect(jsonPath("$.category").value("DEVOPS"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        SnippetResponse createdSnippet = objectMapper.readValue(createResponse, SnippetResponse.class);
        assertThat(createdSnippet.id()).isNotNull();
        assertThat(snippetRepository.findById(createdSnippet.id())).isPresent();
        assertThat(snippetRepository.findById(createdSnippet.id()).orElseThrow().getCategory())
                .isEqualTo(SnippetCategory.DEVOPS);

        mockMvc.perform(post("/api/snippets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Angular route guard",
                                  "content": "Create route guard when auth is added",
                                  "category": "FRONTEND"
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/snippets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(get("/api/snippets/{snippetId}", createdSnippet.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdSnippet.id()))
                .andExpect(jsonPath("$.content").value("curl /actuator/health"));

        mockMvc.perform(put("/api/snippets/{snippetId}", createdSnippet.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Spring Boot liveness check",
                                  "content": "curl /actuator/health/liveness",
                                  "category": "BACKEND"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Spring Boot liveness check"))
                .andExpect(jsonPath("$.category").value("BACKEND"));

        assertThat(snippetRepository.findById(createdSnippet.id()).orElseThrow().getTitle())
                .isEqualTo("Spring Boot liveness check");

        mockMvc.perform(get("/api/snippets").param("query", "liveness"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(createdSnippet.id()));

        mockMvc.perform(delete("/api/snippets/{snippetId}", createdSnippet.id()))
                .andExpect(status().isNoContent());

        assertThat(snippetRepository.findById(createdSnippet.id())).isEmpty();
    }

    @Test
    void snippetValidationReturnsBadRequestWithErrorBody() throws Exception {
        mockMvc.perform(post("/api/snippets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "",
                                  "content": " ",
                                  "category": null
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details.length()").value(3));
    }

    @Test
    void snippetNotFoundReturns404WithErrorBody() throws Exception {
        mockMvc.perform(get("/api/snippets/{snippetId}", 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Snippet with id 999 was not found"));

        mockMvc.perform(delete("/api/snippets/{snippetId}", 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Snippet with id 999 was not found"));
    }
}
