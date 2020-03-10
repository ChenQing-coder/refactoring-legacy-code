package cn.xpbootcamp.legacy_code;

import cn.xpbootcamp.legacy_code.entity.Bill;
import cn.xpbootcamp.legacy_code.utils.RedisDistributedLock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

import javax.transaction.InvalidTransactionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)

public class WalletTransactionTest {

    @Mock
    private RedisDistributedLock redisDistributedLock;

    @Test
    public void should_return_throw_exception_when_call_execute_giving__amount_is_0_and_sellerId_is_null() throws InvalidTransactionException {
        Bill bill = new Bill(111l, null, 0d);
        WalletTransaction walletTransaction = new WalletTransaction("preAssignedId",bill,redisDistributedLock);

        assertThatThrownBy(()->walletTransaction.execute())
                .isInstanceOf(InvalidTransactionException.class);
    }

    @Test
    public void should_return_false_when_call_execute_giving_be_lock() throws InvalidTransactionException {

        when(redisDistributedLock.lock(any())).thenReturn(false);
        Bill bill = new Bill(111l, 111l, 2d);
        WalletTransaction walletTransaction = new WalletTransaction("preAssignedId",bill,redisDistributedLock);


        boolean result = walletTransaction.execute();

        assertThat(result).isEqualTo(false);
    }


}
