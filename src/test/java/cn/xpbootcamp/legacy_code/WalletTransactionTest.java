package cn.xpbootcamp.legacy_code;

import org.junit.jupiter.api.Test;

import javax.transaction.InvalidTransactionException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class WalletTransactionTest {

    @Test
    public void should_return_true_when_call_execute_giving__amount_is_0_and_sellerId_is_null() throws InvalidTransactionException {

        WalletTransaction walletTransaction = new WalletTransaction("preAssignedId", 111l, null, 1l, "orderId",0d);

        assertThatThrownBy(()->walletTransaction.execute())
                .isInstanceOf(InvalidTransactionException.class);
    }
}
