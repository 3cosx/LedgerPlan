package cn.cosx.ledgerplan.business.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易流水。
 *
 * <p>{@code transaction} 是 MySQL 关键字，表名使用反引号转义。</p>
 */
@Data
@TableName("`transaction`")
public class Transaction {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long ledgerId;

    private Long categoryId;

    /**
     * 1 现金，2 储蓄卡，3 信用卡，4 支付宝，5 微信。
     */
    private Integer payChannel;

    /**
     * 1 收入，2 支出。
     */
    private Integer type;

    private BigDecimal amount;

    private LocalDateTime transactionTime;

    private String remark;

    /**
     * 逗号分隔的标签。
     */
    private String tags;

    private String attachUrl;

    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private Long createBy;

    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    @Version
    private Integer version;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic(value = "0", delval = "1")
    private Integer isDeleted;
}
