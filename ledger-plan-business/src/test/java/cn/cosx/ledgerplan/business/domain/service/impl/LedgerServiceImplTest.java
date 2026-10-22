package cn.cosx.ledgerplan.business.ledger.domain.service.impl;

import cn.cosx.ledgerplan.business.ledger.domain.entity.Ledger;
import cn.cosx.ledgerplan.business.ledger.domain.convert.LedgerEntityConvertDto;
import org.mapstruct.factory.Mappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class LedgerServiceImplTest {

    @Test
    void newLedgerBecomesDefaultWhenUserHasNoDefault() {
        assertDefaultFlag(false, null, 1);
    }

    @Test
    void newLedgerIsNotDefaultWhenUserAlreadyHasDefault() {
        assertDefaultFlag(true, null, 0);
    }

    @Test
    void suppliedDefaultFlagCannotOverrideExistingDefault() {
        assertDefaultFlag(true, 1, 0);
    }

    @SuppressWarnings("unchecked")
    private void assertDefaultFlag(boolean hasDefault, Integer suppliedFlag, int expectedFlag) {
        LedgerServiceImpl service = spy(new LedgerServiceImpl(Mappers.getMapper(LedgerEntityConvertDto.class)));
        LambdaQueryChainWrapper<Ledger> nameQuery = mock(LambdaQueryChainWrapper.class, RETURNS_SELF);
        LambdaQueryChainWrapper<Ledger> defaultQuery = mock(LambdaQueryChainWrapper.class, RETURNS_SELF);
        doReturn(nameQuery).when(nameQuery).eq(any(), any());
        doReturn(defaultQuery).when(defaultQuery).eq(any(), any());
        doReturn(nameQuery, defaultQuery).when(service).lambdaQuery();
        when(nameQuery.exists()).thenReturn(false);
        when(defaultQuery.exists()).thenReturn(hasDefault);
        doReturn(true).when(service).save(any(Ledger.class));

        Ledger ledger = new Ledger();
        ledger.setLedgerName("Daily");
        ledger.setIsDefault(suppliedFlag);
        assertEquals(expectedFlag, service.createLedger(7L, ledger).getIsDefault());
        verify(defaultQuery).eq(any(), eq(7L));
        verify(defaultQuery).eq(any(), eq(1));
        verify(defaultQuery).exists();
        verify(service).save(ledger);
    }
}
