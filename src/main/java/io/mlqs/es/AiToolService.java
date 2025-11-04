package io.mlqs.es;

import dev.langchain4j.agent.tool.Tool;
import io.mlqs.es.db.DocumentEntity;

import java.util.List;

public interface AiToolService {
    @Tool("搜索工具")
    List<DocumentEntity> search(String context);
}
