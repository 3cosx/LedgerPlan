package cn.cosx.ledgerplan.business.ledger.domain.entity;

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
 * 账本。
 *
 * <p>一个用户可以拥有多个账本，账本名称在未删除数据中唯一。</p>
 */
@Data
@TableName("ledger")
public class Ledger {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String ledgerName;

    private String icon;

    private String description;

    private String currency;

    /**
     * 总收入 - 总支出，由交易流水变更时重算。
     */
    private BigDecimal totalAmount;

    /**
     * 是否为当前用户的默认账本：0 否，1 是。
     */
    @TableField("is_default")
    private Integer isDefault;

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
