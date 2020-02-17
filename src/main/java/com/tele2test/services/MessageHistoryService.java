package com.tele2test.services;

import com.tele2test.dao.MessagesHistoryDAO;
import com.tele2test.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class MessageHistoryService {

    @Autowired
    MessagesHistoryDAO messagesHistoryDao;

    CopyOnWriteArrayList<Message> historyBuffer = new CopyOnWriteArrayList();

    public void addToBuffer(Message message) {
        if (historyBuffer.size() < 20) {
            historyBuffer.add(message);
        } else {
            historyBuffer.add(message);
            saveToDB();
        }
    }

    public void saveToDB() {
        messagesHistoryDao.saveAll(historyBuffer);
        historyBuffer.clear();
    }

    public Iterable<Message> getMessageForInitChat() {
        Pageable topTen = PageRequest.of(0, 10);
        return messagesHistoryDao.findAll();
    }


}
