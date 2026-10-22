package cn.cosx.ledgerplan.business.ledger.dto.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 分页响应。
 *
 * @param records 当前页数据
 * @param total   总记录数
 * @param page    当前页码，从 1 开始
 * @param size    每页大小
 */
@Data
@AllArgsConstructor
public class PageVO<T> {

    private T records;

    private long total;

    private long page;

    private long size;
}
