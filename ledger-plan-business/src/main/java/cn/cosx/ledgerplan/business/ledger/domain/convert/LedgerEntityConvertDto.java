package cn.cosx.ledgerplan.business.ledger.domain.convert;

import cn.cosx.ledgerplan.business.ledger.domain.LedgerDTO;
import cn.cosx.ledgerplan.business.ledger.domain.entity.Ledger;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LedgerEntityConvertDto {
    LedgerDTO toDto(Ledger ledger);

    List<LedgerDTO> toDtoList(List<Ledger> ledgers);
}
