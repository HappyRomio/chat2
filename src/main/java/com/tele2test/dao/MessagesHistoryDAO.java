package com.tele2test.dao;

import com.tele2test.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface MessagesHistoryDAO extends CrudRepository<Message, Long> {
    List<Message> findAll(Pageable page);
}



