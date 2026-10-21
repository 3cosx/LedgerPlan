package cn.cosx.ledgerplan.user.support;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EmailNormalizerTest {

    @Test
    void trimsAndLowercasesEmail() {
        assertEquals("test@example.com", EmailNormalizer.normalize("  Test@Example.COM "));
    }

    @Test
    void keepsNullAsNull() {
        assertNull(EmailNormalizer.normalize(null));
    }
}
