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
            1、search(String context):List<DocumentEntity>，这个方法的参数是用户希望得到相关信息的关键字串，返回值是一个文档实体的列表并且按照相关度排序。
            """;

    public String getChatPrompt(){
        String prompt = CHAT_PROMPT;
        prompt += "\n以下是在检索时可用的法律文件的枚举名称与其对应的中文法律名，数据结构为枚举名=中文法律名:\n" + documentService.getUsableLegalName();
        return prompt;
    }
}
