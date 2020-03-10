package cn.xpbootcamp.legacy_code.service;

import cn.xpbootcamp.legacy_code.entity.Bill;

public interface WalletService {
    String moveMoney(String id, Bill bill);
}
