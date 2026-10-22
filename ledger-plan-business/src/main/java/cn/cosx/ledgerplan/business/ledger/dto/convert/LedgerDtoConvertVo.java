package cn.cosx.ledgerplan.business.ledger.dto.convert;

import cn.cosx.ledgerplan.business.ledger.domain.LedgerListDTO;
import cn.cosx.ledgerplan.business.ledger.domain.LedgerDTO;
import cn.cosx.ledgerplan.business.ledger.dto.vo.LedgerListVO;
import cn.cosx.ledgerplan.business.ledger.dto.vo.LedgerVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LedgerDtoConvertVo {
    LedgerVO toVo(LedgerDTO ledger);

    LedgerListVO toVo(LedgerListDTO ledgerList);
}
