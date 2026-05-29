package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConduit {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public void save(UserRecord userRecord) {
        userRepository.save(userRecord);
    }

    public void process(Transaction transaction) {

        UserRecord sender =
                userRepository.findById(transaction.getSenderId()).orElse(null);

        UserRecord recipient =
                userRepository.findById(transaction.getRecipientId()).orElse(null);

        if (sender == null || recipient == null) {
            return;
        }

        float amount = transaction.getAmount();

        sender.setBalance(sender.getBalance() - amount);

        recipient.setBalance(recipient.getBalance() + amount);

        userRepository.save(sender);
        userRepository.save(recipient);

        TransactionRecord record = new TransactionRecord();

        record.setAmount(amount);
        record.setSender(sender);
        record.setRecipient(recipient);

        transactionRepository.save(record);
    }
}