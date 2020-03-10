package cn.xpbootcamp.legacy_code;

import cn.xpbootcamp.legacy_code.entity.Bill;
import org.junit.jupiter.api.Test;

import javax.transaction.InvalidTransactionException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class WalletTransactionTest {

    @Test
    public void should_return_true_when_call_execute_giving__amount_is_0_and_sellerId_is_null() throws InvalidTransactionException {
        Bill bill = new Bill(111l, null, 0d);
        WalletTransaction walletTransaction = new WalletTransaction("preAssignedId",bill);

        assertThatThrownBy(()->walletTransaction.execute())
                .isInstanceOf(InvalidTransactionException.class);
    }
}
