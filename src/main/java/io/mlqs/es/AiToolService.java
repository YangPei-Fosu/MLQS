package io.mlqs.es;

import dev.langchain4j.agent.tool.Tool;
import io.mlqs.es.entity.DocumentSearchResultEntity;

import java.util.List;

public interface AiToolService {
    @Tool("搜索工具")
    DocumentSearchResultEntity search(String context, List<String> legalName);
}
