package io.mlqs.agent;

import io.mlqs.es.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Prompt类，专门用来塞各种提示语
 */
@Component
public class Prompt {
    @Autowired
    private DocumentService documentService;

    public static final String CHAT_PROMPT = """
            你是一名资深的法律顾问，你需要根据用户的提问，使用相关工具进行查询后回答用户的问题。
            注意：在回复中不要显示工具调用的详细信息，直接给出基于搜索结果的法律建议。
            你可以使用如下工具：
            1、search(String context, List<String> legalName):DocumentSearchResultEntity，这个方法用于搜索法律法规条文，参数context是用户查询的关键词，参数legalName是一个字典，键是要搜索的法律文件名称（注意使用的是法律文件的枚举名），值是法条关联度阈值，范围在0到1之间，只有高于阈值的搜索结果才会被返回，返回值是相关的法律条文搜索结果的集合，注意搜索集合内的各个法律文献的搜索结果关联度越大的记录Index越大。
            """;

    public String getChatPrompt(){
        String prompt = CHAT_PROMPT;
        prompt += "\n以下是在检索时可用的法律文件的枚举名称与其对应的中文法律名，数据结构为{枚举名=中文法律名}:\n" + documentService.getUsableLegalName();
        return prompt;
    }
}
