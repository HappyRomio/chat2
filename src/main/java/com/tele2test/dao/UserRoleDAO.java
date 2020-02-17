package com.tele2test.dao;

import com.tele2test.entity.AppUser;
import com.tele2test.entity.UserRole;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface UserRoleDAO extends CrudRepository<UserRole, Long> {
    List<UserRole> findByAppUser(AppUser appUser);
}
