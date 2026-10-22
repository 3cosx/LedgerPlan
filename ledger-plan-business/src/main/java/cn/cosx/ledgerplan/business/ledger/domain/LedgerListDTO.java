package cn.cosx.ledgerplan.business.ledger.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LedgerListDTO {

    private List<LedgerDTO> ledgers;
}
