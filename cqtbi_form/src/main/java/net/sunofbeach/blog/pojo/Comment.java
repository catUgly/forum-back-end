package net.sunofbeach.blog.pojo;


import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name="tb_comment")
public class Comment {
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "article_id")
    private String articleId;
    @Column(name = "parent_content")
    private String parentContent;
    @Column(name = "content")
    private String content;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "user_avatar")
    private String userAvatar;
    @Column(name = "use_name")
    private String useName;
    @Column(name = "state")
    private String state = "1";
    @Column(name = "create_time")
    private Date createTime;
    @Column(name = "update_time")
    private Date updateTime;

    @OneToOne(targetEntity = SobUser.class)
    @JoinColumn(name = "user_id",referencedColumnName = "id",insertable = false,updatable = false)
    private SobUser sobUser;

}
