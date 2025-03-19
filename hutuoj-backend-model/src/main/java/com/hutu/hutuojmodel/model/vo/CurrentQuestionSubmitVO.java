package com.hutu.hutuojmodel.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;

import com.hutu.hutuojmodel.model.entity.QuestionSubmit;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @author hutu-g
 * @ createTime 2025-03-01
 * @ description 用户当前题目提交记录
 * @ version 1.0
 */
@Data
public class CurrentQuestionSubmitVO implements Serializable {

    /**
     * id()
     */
    private Long id;

    /**
     * 编程语言
     */
    private String language;

    /**
     * 判题状态（0 - 待判题、1 - 判题中、2 - 成功、3 - 失败）
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 使用内存
     */
    private Integer useMemory;

    /**
     * 耗时时间
     */
    private Integer useTime;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


    /**
     * 对象转包装类
     *
     * @param questionSubmit
     * @return
     */
    public static CurrentQuestionSubmitVO objToVo(QuestionSubmit questionSubmit) {
        if (questionSubmit == null) {
            return null;
        }
        CurrentQuestionSubmitVO currentQuestionSubmitVO = new CurrentQuestionSubmitVO();
        BeanUtils.copyProperties(questionSubmit, currentQuestionSubmitVO);
        return currentQuestionSubmitVO;
    }


}
