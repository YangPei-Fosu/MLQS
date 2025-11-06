package io.mlqs.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * 通用返回类
 */
public class HttpRespondDTO {
    private int code;
    private Map<String, Object> content;

    public static HttpRespondDTO ok(){
        return new HttpRespondDTO(200, new HashMap<>());
    }

    public static HttpRespondDTO error(){
        return new HttpRespondDTO(500, new HashMap<>());
    }

    public static HttpRespondDTO build(int code){
        return new HttpRespondDTO(code, new HashMap<>());
    }

    public HttpRespondDTO put(String key, Object value){
        this.content.put(key, value);
        return this;
    }
}
