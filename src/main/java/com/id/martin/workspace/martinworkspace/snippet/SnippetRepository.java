package com.id.martin.workspace.martinworkspace.snippet;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SnippetRepository extends JpaRepository<Snippet, Long> {

    List<Snippet> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrderByUpdatedAtDesc(
            String title,
            String content
    );
}
