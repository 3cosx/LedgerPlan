package cn.cosx.ledgerplan.business.ledger.domain.service;

import cn.cosx.ledgerplan.business.ledger.dto.convert.LedgerDtoConvertVo;
import cn.cosx.ledgerplan.business.ledger.domain.entity.Ledger;
import cn.cosx.ledgerplan.business.ledger.dto.convert.LedgerRequestConvertEntity;
import cn.cosx.ledgerplan.business.ledger.dto.request.LedgerCreateRequest;
import cn.cosx.ledgerplan.business.ledger.dto.request.LedgerUpdateRequest;
import cn.cosx.ledgerplan.business.ledger.dto.vo.LedgerVO;
import cn.cosx.ledgerplan.common.exception.BizException;
import cn.cosx.ledgerplan.common.tool.CommonErrorCode;
import cn.cosx.ledgerplan.business.ledger.dto.vo.LedgerListVO;
import cn.cosx.ledgerplan.common.context.UserContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LedgerAggregateService {

    private final LedgerService ledgerService;

    private final LedgerDtoConvertVo ledgerDtoConvertVo;
    private final LedgerRequestConvertEntity ledgerRequestConvertEntity;

    public LedgerListVO getLedgerList() {
        return ledgerDtoConvertVo.toVo(ledgerService.getLedgerList(currentUserId()));
    }

    public LedgerVO getLedger(Long ledgerId) {
        return ledgerDtoConvertVo.toVo(ledgerService.getLedger(currentUserId(), ledgerId));
    }

    public LedgerVO createLedger(LedgerCreateRequest request) {
        Long userId = currentUserId();
        Ledger ledger = ledgerRequestConvertEntity.toEntity(request);
        ledger.setLedgerName(normalizeName(ledger.getLedgerName()));
        ledger.setCurrency(normalizeCurrency(ledger.getCurrency()));
        return ledgerDtoConvertVo.toVo(ledgerService.createLedger(userId, ledger));
    }

    public LedgerVO updateLedger(Long ledgerId, LedgerUpdateRequest request) {
        Long userId = currentUserId();
        Ledger changes = ledgerRequestConvertEntity.toEntity(request);
        if (changes.getLedgerName() != null) changes.setLedgerName(normalizeName(changes.getLedgerName()));
        changes.setCurrency(normalizeCurrency(changes.getCurrency()));
        return ledgerDtoConvertVo.toVo(ledgerService.updateLedger(userId, ledgerId, changes));
    }

    public void deleteLedger(Long ledgerId) {
        ledgerService.deleteLedger(currentUserId(), ledgerId);
    }

    private Long currentUserId() {
        Long userId = UserContextHolder.getRequired().userId();
        if (userId == null) throw new BizException(CommonErrorCode.UNAUTHORIZED, "请先登录");
        return userId;
    }

    private String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new BizException(CommonErrorCode.BAD_REQUEST, "账本名称不能为空");
        }
        return name.trim();
    }

    private String normalizeCurrency(String currency) {
        if (currency == null) return null;
        if (currency.isBlank()) throw new BizException(CommonErrorCode.BAD_REQUEST, "货币不能为空");
        return currency.trim();
    }
}
