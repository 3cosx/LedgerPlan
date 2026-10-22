package cn.cosx.ledgerplan.business.transaction.build;

import cn.cosx.ledgerplan.business.transaction.dto.TransactionAndCategoryDTO;
import cn.cosx.ledgerplan.business.transaction.dto.TransactionDTO;
import cn.cosx.ledgerplan.common.enums.PayChannelEnum;
import cn.cosx.ledgerplan.common.enums.TransactionTypeEnum;
import cn.cosx.ledgerplan.common.pair.IntegerStringPair;
import cn.cosx.ledgerplan.common.pair.LongStringPair;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Component
public class TransactionBuilder {

    public List<TransactionDTO> buildTransactionAndCategoryDTOList2TransactionDTO(List<TransactionAndCategoryDTO> dtos) {
        if (CollectionUtils.isEmpty(dtos)) {
            return Collections.emptyList();
        }
        return dtos.stream()
                .map(this::buildTransaction)
                .toList();
    }

    private TransactionDTO buildTransaction(TransactionAndCategoryDTO dto) {
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setId(dto.getId());
        transactionDTO.setLedgerPair(buildLongStringPair(dto.getLedgerId(), dto.getLedgerName()));
        transactionDTO.setCategoryPair(buildLongStringPair(dto.getCategoryId(), dto.getCategoryName()));
        transactionDTO.setPayChannel(buildIntegerStringPair(dto.getPayChannel(), payChannelDesc(dto.getPayChannel())));
        transactionDTO.setTransactionType(buildIntegerStringPair(dto.getType(), transactionTypeDesc(dto.getType())));
        transactionDTO.setAmount(dto.getAmount());
        transactionDTO.setTransactionTime(dto.getTransactionTime());
        transactionDTO.setRemark(dto.getRemark());
        transactionDTO.setTags(dto.getTags());
        transactionDTO.setAttachUrl(dto.getAttachUrl());
        return transactionDTO;
    }

    private LongStringPair buildLongStringPair(Long key, String value) {
        LongStringPair pair = new LongStringPair();
        pair.setKey(key);
        pair.setValue(value);
        return pair;
    }

    private IntegerStringPair buildIntegerStringPair(Integer key, String value) {
        IntegerStringPair pair = new IntegerStringPair();
        pair.setKey(key);
        pair.setValue(value);
        return pair;
    }

    private String payChannelDesc(Integer payChannel) {
        PayChannelEnum payChannelEnum = PayChannelEnum.getByCode(payChannel);
        return payChannelEnum == null ? null : payChannelEnum.getDesc();
    }

    private String transactionTypeDesc(Integer type) {
        TransactionTypeEnum transactionTypeEnum = TransactionTypeEnum.getByCode(type);
        return transactionTypeEnum == null ? null : transactionTypeEnum.getDesc();
    }
}
