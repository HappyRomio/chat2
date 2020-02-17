package com.tele2test.dao;

import com.tele2test.entity.AppUser;
import org.springframework.data.repository.CrudRepository;

public interface AppUserDAO extends CrudRepository<AppUser, Long> {
    AppUser findByUserName(String userName);

    AppUser findByEmail(String email);
}
