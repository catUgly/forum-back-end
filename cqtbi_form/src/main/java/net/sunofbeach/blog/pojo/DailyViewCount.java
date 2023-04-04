package net.sunofbeach.blog.pojo;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Data
@Table(name="tb_daily_view_count")
public class DailyViewCount {
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "view_count")
    private int viewCount;
    @Column(name = "create_time")
    private Date createTime;
    @Column(name = "update_time")
    private Date updateTime;
}
