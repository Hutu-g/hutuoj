package com.hutu.hutuojmodel.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 评论
 * @author hutu-g
 */
@TableName(value = "comment")
@Data
public class Comment implements Serializable {

    /**
     * 评论id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 题目id
     */
    private Long questionId;

    /**
     * 评论人id
     */
    private Long userId;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 评论父级id
     */
    private Long parentId;

    /**
     * 回复目标对象id
     */
    private Long targetId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 子评论
     * */
    @TableField(exist = false)
    private List<Comment> children;

    /**
     * 是否删除 0未删除 1删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}