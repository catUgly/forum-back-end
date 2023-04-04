package net.sunofbeach.blog.pojo.vio;

import lombok.Data;

@Data
public class SendEmail {
    private String topic;
    private String email;
    private String content;
}
