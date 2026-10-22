package cn.cosx.ledgerplan.business.ledger.dto.convert;

import cn.cosx.ledgerplan.business.ledger.domain.entity.Ledger;
import cn.cosx.ledgerplan.business.ledger.dto.request.LedgerCreateRequest;
import cn.cosx.ledgerplan.business.ledger.dto.request.LedgerUpdateRequest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LedgerRequestConvertEntity {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "ledgerName", source = "ledgerName")
    @Mapping(target = "icon", source = "icon")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "currency", source = "currency")
    Ledger toEntity(LedgerCreateRequest request);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "ledgerName", source = "ledgerName")
    @Mapping(target = "icon", source = "icon")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "currency", source = "currency")
    Ledger toEntity(LedgerUpdateRequest request);
}
