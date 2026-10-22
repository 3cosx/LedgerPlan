package cn.cosx.ledgerplan.business.service.impl;

import cn.cosx.ledgerplan.business.entity.Ledger;
import cn.cosx.ledgerplan.business.mapper.LedgerMapper;
import cn.cosx.ledgerplan.business.service.LedgerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 账本服务实现，仅提供 MyBatis-Plus 通用 CRUD。
 */
@Service
public class LedgerServiceImpl extends ServiceImpl<LedgerMapper, Ledger> implements LedgerService {
}
