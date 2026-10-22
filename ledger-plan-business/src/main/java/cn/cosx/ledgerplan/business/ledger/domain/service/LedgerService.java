package cn.cosx.ledgerplan.business.ledger.domain.service;

import cn.cosx.ledgerplan.business.ledger.domain.LedgerListDTO;
import cn.cosx.ledgerplan.business.ledger.domain.LedgerDTO;
import cn.cosx.ledgerplan.business.ledger.domain.entity.Ledger;
import com.baomidou.mybatisplus.extension.service.IService;
import java.math.BigDecimal;

/**
 * 账本服务，直接使用 MyBatis-Plus 的通用 CRUD。
 */
public interface LedgerService extends IService<Ledger> {
    LedgerListDTO getLedgerList(Long userId);

    LedgerDTO getLedger(Long userId, Long ledgerId);

    LedgerDTO createLedger(Long userId, Ledger ledger);

    LedgerDTO updateLedger(Long userId, Long ledgerId, Ledger changes);

    void deleteLedger(Long userId, Long ledgerId);

    void adjustLedgerTotal(Long userId, Long ledgerId, BigDecimal amountChange);
}
