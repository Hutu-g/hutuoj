package com.hutu.hutuojmodel.model.dto.Comment;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 评论
 * @author hutu-g
 */
@Data
public class CommentAddRequest implements Serializable {

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

    private static final long serialVersionUID = 1L;
}