package cn.cosx.ledgerplan.business.service.impl;

import cn.cosx.ledgerplan.business.entity.Transaction;
import cn.cosx.ledgerplan.business.mapper.TransactionMapper;
import cn.cosx.ledgerplan.business.service.TransactionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 交易流水服务实现，仅提供 MyBatis-Plus 通用 CRUD。
 */
@Service
public class TransactionServiceImpl extends ServiceImpl<TransactionMapper, Transaction> implements TransactionService {
}
