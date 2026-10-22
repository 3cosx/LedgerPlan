package cn.cosx.ledgerplan.business.transaction.domain.service;


import cn.cosx.ledgerplan.business.transaction.domain.entity.Transaction;
import cn.cosx.ledgerplan.business.transaction.dto.TransactionAndCategoryDTO;
import cn.cosx.ledgerplan.business.transaction.dto.TransactionSingleLedgerQueryDTO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 交易流水服务
 */
public interface TransactionService extends IService<Transaction> {

    List<TransactionAndCategoryDTO> listLedgerTransactions(TransactionSingleLedgerQueryDTO transactionSingleLedgerQueryDTO);
}
