package cn.xpbootcamp.legacy_code;

import cn.xpbootcamp.legacy_code.entity.Bill;
import cn.xpbootcamp.legacy_code.enums.STATUS;
import cn.xpbootcamp.legacy_code.service.WalletService;
import cn.xpbootcamp.legacy_code.service.WalletServiceImpl;
import cn.xpbootcamp.legacy_code.utils.IdGenerator;
import cn.xpbootcamp.legacy_code.utils.RedisDistributedLock;

import javax.transaction.InvalidTransactionException;

public class WalletTransaction {
    private String id;
    Bill bill;
    RedisDistributedLock redisDistributedLock;

    public WalletTransaction(String preAssignedId,Bill bill,RedisDistributedLock redisDistributedLock) {
        this.bill = bill;
        generatorID(preAssignedId);
        this.redisDistributedLock =redisDistributedLock;
    }

    private void generatorID(String preAssignedId) {
        if (preAssignedId != null) {
            this.id = preAssignedId;
        } else {
            this.id = IdGenerator.generateTransactionId();
        }
        if (!this.id.startsWith("t_")) {
            this.id = "t_" + preAssignedId;
        }
    }

    public boolean execute() throws InvalidTransactionException {
        bill.validateInfo();
        if (bill.getStatus() == STATUS.EXECUTED) {
            return true;
        }
        boolean isLocked = false;
        try {
            isLocked = redisDistributedLock.lock(id);
            if (!isLocked) {
                return false;
            }
            if (validateForMoreThan20Days()) return false;
            WalletService walletService = new WalletServiceImpl();
            String walletTransactionId = walletService.moveMoney(id, bill);
            if (walletTransactionId != null) {
                bill.setStatus(STATUS.EXECUTED);
                return true;
            } else {
                bill.setStatus(STATUS.FAILED);
                return false;
            }
        } finally {
            if (isLocked) {
                redisDistributedLock.unlock(id);
            }
        }
    }

    private boolean validateForMoreThan20Days() {
        long executionInvokedTimestamp = System.currentTimeMillis();
        if (executionInvokedTimestamp - bill.getCreatedTimestamp() > 1728000000) {
            bill.setStatus(STATUS.EXPIRED);
            return true;
        }
        return false;
    }


}