package io.mlqs.es;

import com.mysql.cj.log.Log;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import io.mlqs.es.entity.DocumentSearchResultEntity;
import io.mlqs.utils.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AiToolServiceImpl implements AiToolService{
    @Autowired
    private DocumentService documentService;

    @Override
    @Tool("法律条文搜索工具，根据用户问题搜索相关的法律法规条文。参数context是查询的关键词或短句，参数legalName是一个字典，键是要搜索的法律文件名称，值是法条关联度阈值，只有高于阈值的搜索结果才会被返回，特别注意：当键为CASE_ENTITY时表示需要检索案件，将返回高于指定阈值的案件")
    public DocumentSearchResultEntity search(@ToolMemoryId String sessionId,String context, Map<String, Double> legalName) {
        LogUtils.debug(this.getClass(), "搜索关键字："+  context);
        return documentService.hybridSearch(context,  legalName);
    }
}
