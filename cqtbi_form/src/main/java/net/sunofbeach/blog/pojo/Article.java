package net.sunofbeach.blog.pojo;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name="tb_article")
public class Article {
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "title")
    private String title;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "category_id")
    private String categoryId;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "content")
    private String content;
    @Column(name = "type")
    private String type;
    @Column(name = "state")
    private String state = "1";
    @Column(name = "summary")
    private String summary;
    @Column(name = "labels")
    private String labels;
    @Column(name = "comment_count")
    private int commentCount = 0;
    @Column(name = "view_count")
    private int viewCount = 0;
    @Column(name = "create_time")
    private Date createTime;
    @Column(name = "update_time")
    private Date updateTime;

    @OneToOne(targetEntity = SobUser.class)
    @JoinColumn(name = "user_id",referencedColumnName = "id",insertable = false,updatable = false)
    private SobUser sobUser;
}
