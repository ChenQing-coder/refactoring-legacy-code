package cn.xpbootcamp.legacy_code;

import cn.xpbootcamp.legacy_code.entity.Bill;
import cn.xpbootcamp.legacy_code.enums.STATUS;
import cn.xpbootcamp.legacy_code.service.WalletServiceImpl;
import cn.xpbootcamp.legacy_code.utils.RedisDistributedLock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

import javax.transaction.InvalidTransactionException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)

public class WalletTransactionTest {

    @Mock
    private RedisDistributedLock redisDistributedLock;

    @Mock
    private Bill bill ;

    @Mock
    private WalletServiceImpl walletService;

    @InjectMocks
    private WalletTransaction walletTransaction = new WalletTransaction("preAssignedId",bill);


    @Test
    public void should_return_true_when_call_execute_giving_status_is_executed() throws InvalidTransactionException {

        when(bill.getStatus()).thenReturn(STATUS.EXECUTED);

        boolean result = walletTransaction.execute();

        assertThat(result).isEqualTo(true);
    }

    @Test
    public void should_return_false_when_call_execute_giving_be_lock() throws InvalidTransactionException {

        when(redisDistributedLock.lock(any())).thenReturn(false);

        boolean result = walletTransaction.execute();

        assertThat(result).isEqualTo(false);
    }

    @Test
    public void should_return_false_when_call_execute_giving_time_is_more_than_20() throws InvalidTransactionException {

        when(redisDistributedLock.lock(any())).thenReturn(true);
        when(bill.getCreatedTimestamp()).thenReturn(1l);

        boolean result = walletTransaction.execute();

        assertThat(result).isEqualTo(false);
    }

    @Test
    public void should_return_true_when_call_execute_giving_move_money_return_value() throws InvalidTransactionException {

        when(redisDistributedLock.lock(any())).thenReturn(true);
        when(bill.getCreatedTimestamp()).thenReturn(System.currentTimeMillis());
        when(walletService.moveMoney(any(),any())).thenReturn("1");

        boolean result = walletTransaction.execute();

        assertThat(result).isEqualTo(true);
    }

    @Test
    public void should_return_false_when_call_execute_giving_move_money_null() throws InvalidTransactionException {

        when(redisDistributedLock.lock(any())).thenReturn(true);
        when(bill.getCreatedTimestamp()).thenReturn(System.currentTimeMillis());
        when(walletService.moveMoney(any(),any())).thenReturn(null);

        boolean result = walletTransaction.execute();

        assertThat(result).isEqualTo(false);
    }
}
