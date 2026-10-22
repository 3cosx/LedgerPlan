package cn.cosx.ledgerplan.business.transaction.domain.mapper;


import cn.cosx.ledgerplan.business.transaction.domain.entity.Transaction;
import cn.cosx.ledgerplan.business.transaction.dto.TransactionAndCategoryDTO;
import cn.cosx.ledgerplan.business.transaction.dto.TransactionSingleLedgerQueryDTO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface TransactionMapper extends BaseMapper<Transaction> {
    List<TransactionAndCategoryDTO> listLedgerTransactions(TransactionSingleLedgerQueryDTO transactionSingleLedgerQueryDTO);
}
