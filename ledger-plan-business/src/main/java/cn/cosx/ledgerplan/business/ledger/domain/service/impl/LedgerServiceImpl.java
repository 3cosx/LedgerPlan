package cn.cosx.ledgerplan.business.ledger.domain.service.impl;


import cn.cosx.ledgerplan.business.ledger.domain.LedgerListDTO;
import cn.cosx.ledgerplan.business.ledger.domain.LedgerDTO;
import cn.cosx.ledgerplan.business.ledger.domain.convert.LedgerEntityConvertDto;
import lombok.RequiredArgsConstructor;
import cn.cosx.ledgerplan.business.ledger.domain.entity.Ledger;
import cn.cosx.ledgerplan.business.ledger.domain.mapper.LedgerMapper;
import cn.cosx.ledgerplan.business.ledger.domain.service.LedgerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.cosx.ledgerplan.common.exception.BizException;
import cn.cosx.ledgerplan.common.tool.CommonErrorCode;
import org.springframework.stereotype.Service;

import java.util.List;
import java.math.BigDecimal;

/**
 * 账本领域服务，限定当前用户的数据范围。
 */
@Service
@RequiredArgsConstructor
public class LedgerServiceImpl extends ServiceImpl<LedgerMapper, Ledger> implements LedgerService {
    private final LedgerEntityConvertDto ledgerEntityConvertDto;

    @Override
    public LedgerListDTO getLedgerList(Long userId) {

        List<Ledger> ledgers = lambdaQuery().eq(Ledger::getUserId, userId)
                .orderByAsc(Ledger::getIsDefault)
                .orderByDesc(Ledger::getCreatedAt).list();
        return new LedgerListDTO(ledgerEntityConvertDto.toDtoList(ledgers));
    }

    @Override
    public LedgerDTO getLedger(Long userId, Long ledgerId) {
        return ledgerEntityConvertDto.toDto(getRequiredLedger(userId, ledgerId));
    }

    private Ledger getRequiredLedger(Long userId, Long ledgerId) {
        Ledger ledger = lambdaQuery().eq(Ledger::getId, ledgerId).eq(Ledger::getUserId, userId).one();
        if (ledger == null) throw new BizException(CommonErrorCode.NOT_FOUND, "账本不存在");
        return ledger;
    }

    @Override
    public LedgerDTO createLedger(Long userId, Ledger ledger) {
        ensureNameAvailable(userId, ledger.getLedgerName(), null);
        ledger.setUserId(userId);
        ledger.setCreateBy(userId);
        ledger.setUpdateBy(userId);
        ledger.setVersion(0);
        ledger.setIsDeleted(0);
        if (ledger.getCurrency() == null || ledger.getCurrency().isBlank()) ledger.setCurrency("CNY");
        if (ledger.getTotalAmount() == null) ledger.setTotalAmount(java.math.BigDecimal.ZERO);
        boolean hasDefaultLedger = lambdaQuery().eq(Ledger::getUserId, userId)
                .eq(Ledger::getIsDefault, 1).exists();
        ledger.setIsDefault(hasDefaultLedger ? 0 : 1);
        if (!save(ledger)) throw new BizException(CommonErrorCode.INTERNAL_ERROR, "账本创建失败");
        return ledgerEntityConvertDto.toDto(ledger);
    }

    @Override
    public LedgerDTO updateLedger(Long userId, Long ledgerId, Ledger changes) {
        Ledger ledger = getRequiredLedger(userId, ledgerId);
        if (changes.getLedgerName() != null) {
            ensureNameAvailable(userId, changes.getLedgerName(), ledgerId);
            ledger.setLedgerName(changes.getLedgerName());
        }
        if (changes.getIcon() != null) ledger.setIcon(changes.getIcon());
        if (changes.getDescription() != null) ledger.setDescription(changes.getDescription());
        if (changes.getCurrency() != null) ledger.setCurrency(changes.getCurrency());
        ledger.setUpdateBy(userId);
        ledger.setUpdatedAt(null);
        if (!updateById(ledger)) {
            throw new BizException(CommonErrorCode.BAD_REQUEST, "账本已变更，请刷新后重试");
        }
        return ledgerEntityConvertDto.toDto(ledger);
    }

    @Override
    public void deleteLedger(Long userId, Long ledgerId) {
        Ledger ledger = getRequiredLedger(userId, ledgerId);
        if (!removeById(ledger.getId())) {
            throw new BizException(CommonErrorCode.BAD_REQUEST, "账本已变更，请刷新后重试");
        }
    }

    @Override
    public void adjustLedgerTotal(Long userId, Long ledgerId, BigDecimal amountChange) {
        Ledger ledger = getRequiredLedger(userId, ledgerId);
        BigDecimal currentAmount = ledger.getTotalAmount() == null ? BigDecimal.ZERO : ledger.getTotalAmount();
        ledger.setTotalAmount(currentAmount.add(amountChange));
        ledger.setUpdateBy(userId);
        ledger.setUpdatedAt(null);
        if (!updateById(ledger)) {
            throw new BizException(CommonErrorCode.BAD_REQUEST, "账本已变更，请重试");
        }
    }

    private void ensureNameAvailable(Long userId, String name, Long excludedId) {
        var query = lambdaQuery().eq(Ledger::getUserId, userId).eq(Ledger::getLedgerName, name);
        if (excludedId != null) query.ne(Ledger::getId, excludedId);
        if (query.exists()) throw new BizException(CommonErrorCode.BAD_REQUEST, "账本名称已存在");
    }
}
