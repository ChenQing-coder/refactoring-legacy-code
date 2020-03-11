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
    WalletService walletService;

    public WalletTransaction(String preAssignedId,Bill bill) {
        this.bill = bill;
        this.redisDistributedLock =new RedisDistributedLock() ;
        this.walletService = new WalletServiceImpl(bill);
        generateID(preAssignedId);
    }

    private void generateID(String preAssignedId) {
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
        boolean isLocked = false;
        bill.validateInfo();
        if (bill.getStatus() == STATUS.EXECUTED) {
            return true;
        }
        try {
            isLocked = redisDistributedLock.lock(id);
            if (!isLocked || isDayMoreThan20Days()) {
                return false;
            }
            return moveMoney();
        } finally {
            if (isLocked) {
                redisDistributedLock.unlock(id);
            }
        }
    }

    private boolean moveMoney() {
        String walletTransactionId = walletService.moveMoney(id);
        if (walletTransactionId != null) {
            bill.setStatus(STATUS.EXECUTED);
            return true;
        } else {
            bill.setStatus(STATUS.FAILED);
            return false;
        }
    }

    private boolean isDayMoreThan20Days() {
        long executionInvokedTimestamp = System.currentTimeMillis();
        if (executionInvokedTimestamp - bill.getCreatedTimestamp() > 1728000000) {
            bill.setStatus(STATUS.EXPIRED);
            return true;
        }
        return false;
    }


}