package cn.cosx.ledgerplan.business.dto.vo;

import java.util.List;

/**
 * 分页响应。
 *
 * @param records 当前页数据
 * @param total   总记录数
 * @param page    当前页码，从 1 开始
 * @param size    每页大小
 */
public record PageVO<T>(
        List<T> records,
        long total,
        long page,
        long size) {
}
