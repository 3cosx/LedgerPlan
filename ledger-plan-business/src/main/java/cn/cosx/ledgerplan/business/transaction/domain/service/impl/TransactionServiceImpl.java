package cn.cosx.ledgerplan.business.transaction.domain.service.impl;

import cn.cosx.ledgerplan.business.transaction.domain.entity.Transaction;
import cn.cosx.ledgerplan.business.transaction.domain.mapper.TransactionMapper;
import cn.cosx.ledgerplan.business.transaction.domain.service.TransactionService;
import cn.cosx.ledgerplan.business.transaction.dto.TransactionAndCategoryDTO;
import cn.cosx.ledgerplan.business.transaction.dto.TransactionSingleLedgerQueryDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import java.util.List;


/**
 * 交易流水服务实现，仅提供 MyBatis-Plus 通用 CRUD。
 */
@Service
public class TransactionServiceImpl extends ServiceImpl<TransactionMapper, Transaction> implements TransactionService {

    @Override
    public List<TransactionAndCategoryDTO> listLedgerTransactions(TransactionSingleLedgerQueryDTO transactionSingleLedgerQueryDTO) {
        return baseMapper.listLedgerTransactions(transactionSingleLedgerQueryDTO);
    }
}
