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
            你是一名资深的法律顾问，你需要根据用户的提问，并使用相关工具进行查询后回答用户的问题，问题的回答一定基于查询结果，不能凭空捏造。
            你可以使用如下工具：
            1、search(String context, List<String> legalName):DocumentSearchResultEntity，这个方法用于搜索法律法规条文，参数context是用户查询的关键词，参数legalName是要需要搜索的法律文件名称列表（注意使用的是法律文件的枚举名），返回值是相关的法律条文搜索结果的集合。
            """;

    public String getChatPrompt(){
        String prompt = CHAT_PROMPT;
        prompt += "\n以下是在检索时可用的法律文件的枚举名称与其对应的中文法律名，数据结构为{枚举名=中文法律名}:\n" + documentService.getUsableLegalName();
        return prompt;
    }
}
