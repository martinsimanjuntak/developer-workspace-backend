package com.id.martin.workspace.martinworkspace.snippet;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/snippets")
public class SnippetController {

    private final SnippetService snippetService;

    public SnippetController(SnippetService snippetService) {
        this.snippetService = snippetService;
    }

    @PostMapping
    public ResponseEntity<SnippetResponse> create(@Valid @RequestBody SnippetRequest request) {
        SnippetResponse response = snippetService.create(request);
        return ResponseEntity.created(URI.create("/api/snippets/" + response.id())).body(response);
    }

    @GetMapping
    public List<SnippetResponse> findAll(@RequestParam(required = false) String query) {
        return snippetService.findAll(query);
    }

    @GetMapping("/{snippetId}")
    public SnippetResponse findById(@PathVariable Long snippetId) {
        return snippetService.findById(snippetId);
    }

    @PutMapping("/{snippetId}")
    public SnippetResponse update(@PathVariable Long snippetId, @Valid @RequestBody SnippetRequest request) {
        return snippetService.update(snippetId, request);
    }

    @DeleteMapping("/{snippetId}")
    public ResponseEntity<Void> delete(@PathVariable Long snippetId) {
        snippetService.delete(snippetId);
        return ResponseEntity.noContent().build();
    }
}
