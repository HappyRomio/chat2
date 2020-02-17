package com.tele2test.dao;

import com.tele2test.entity.UserConnection;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface UserConnectionDAO extends CrudRepository<UserConnection, String> {
    List<UserConnection> findByProviderUserId(String providerUserId);
}
