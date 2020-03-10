package cn.xpbootcamp.legacy_code.service;

import cn.xpbootcamp.legacy_code.entity.Bill;
import cn.xpbootcamp.legacy_code.entity.User;
import cn.xpbootcamp.legacy_code.repository.UserRepository;
import cn.xpbootcamp.legacy_code.repository.UserRepositoryImpl;

import java.util.UUID;

public class WalletServiceImpl implements WalletService {
    private UserRepository userRepository = new UserRepositoryImpl();

    public String moveMoney(String id, Bill bill) {
        User buyer = userRepository.find(bill.getBuyerId());
        Double amount = bill.getAmount();
        if (buyer.getBalance() >= amount) {
            User seller = userRepository.find(bill.getSellerId());
            seller.setBalance(seller.getBalance() + amount);
            buyer.setBalance(buyer.getBalance() - amount);
            return UUID.randomUUID().toString() + id;
        } else {
            return null;
        }
    }
}
