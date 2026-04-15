package com.id.martin.workspace.martinworkspace;

import com.id.martin.workspace.martinworkspace.snippet.SnippetRepository;
import com.id.martin.workspace.martinworkspace.task.TaskRepository;
import com.id.martin.workspace.martinworkspace.worklog.WorkLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected TaskRepository taskRepository;

    @Autowired
    protected WorkLogRepository workLogRepository;

    @Autowired
    protected SnippetRepository snippetRepository;

    @BeforeEach
    void cleanDatabase() {
        workLogRepository.deleteAll();
        taskRepository.deleteAll();
        snippetRepository.deleteAll();
    }

    protected String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        }
        catch (Exception exception) {
            throw new IllegalStateException("Failed to serialize test payload", exception);
        }
    }
}
