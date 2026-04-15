package com.id.martin.workspace.martinworkspace.snippet;

import com.id.martin.workspace.martinworkspace.common.ResourceNotFoundException;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SnippetService {

    private final SnippetRepository snippetRepository;

    public SnippetService(SnippetRepository snippetRepository) {
        this.snippetRepository = snippetRepository;
    }

    public SnippetResponse create(SnippetRequest request) {
        Snippet snippet = new Snippet();
        snippet.setTitle(request.title().trim());
        snippet.setContent(request.content().trim());
        snippet.setCategory(request.category());
        return toResponse(snippetRepository.save(snippet));
    }

    @Transactional(readOnly = true)
    public List<SnippetResponse> findAll(String query) {
        if (query != null && !query.isBlank()) {
            String normalizedQuery = query.trim();
            return snippetRepository
                    .findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrderByUpdatedAtDesc(
                            normalizedQuery,
                            normalizedQuery
                    )
                    .stream()
                    .map(this::toResponse)
                    .toList();
        }

        return snippetRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Snippet::getUpdatedAt).reversed())
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SnippetResponse findById(Long snippetId) {
        return toResponse(getSnippet(snippetId));
    }

    public SnippetResponse update(Long snippetId, SnippetRequest request) {
        Snippet snippet = getSnippet(snippetId);
        snippet.setTitle(request.title().trim());
        snippet.setContent(request.content().trim());
        snippet.setCategory(request.category());
        snippet.touch();
        return toResponse(snippet);
    }

    public void delete(Long snippetId) {
        snippetRepository.delete(getSnippet(snippetId));
    }

    private Snippet getSnippet(Long snippetId) {
        return snippetRepository.findById(snippetId)
                .orElseThrow(() -> new ResourceNotFoundException("Snippet with id " + snippetId + " was not found"));
    }

    private SnippetResponse toResponse(Snippet snippet) {
        return new SnippetResponse(
                snippet.getId(),
                snippet.getTitle(),
                snippet.getContent(),
                snippet.getCategory(),
                snippet.getCreatedAt(),
                snippet.getUpdatedAt()
        );
    }
}
