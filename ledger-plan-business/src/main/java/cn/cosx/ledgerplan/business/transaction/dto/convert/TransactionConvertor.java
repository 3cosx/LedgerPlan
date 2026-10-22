package cn.cosx.ledgerplan.business.transaction.dto.convert;

import cn.cosx.ledgerplan.business.transaction.dto.TransactionSingleLedgerQueryDTO;
import cn.cosx.ledgerplan.business.transaction.dto.request.TransactionQueryRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Transaction 查询对象转换
 */
@Mapper
public interface TransactionConvertor {

    TransactionConvertor INSTANCE = Mappers.getMapper(TransactionConvertor.class);

    TransactionSingleLedgerQueryDTO request2Dto(TransactionQueryRequest request);

    TransactionSingleLedgerQueryDTO transactionQuery2Dto(TransactionQueryRequest.TransactionQuery transactionQuery);
}
