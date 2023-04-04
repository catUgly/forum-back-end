package net.sunofbeach.blog.pojo;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Data
@Table(name="tb_images")
public class Images {
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "url")
    private String url;
    @Column(name = "state")
    private String state;
    @Column(name = "source")
    private String source;
    @Column(name="article_id")
    private String articleId;
    @Column(name = "create_time")
    private Date createTime;
    @Column(name = "update_time")
    private Date updateTime;
}
