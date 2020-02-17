package com.tele2test.dao;

import com.tele2test.entity.AppRole;
import org.springframework.data.repository.CrudRepository;


public interface AppRoleDAO extends CrudRepository<AppRole, Long> {
    AppRole findByRoleName(String roleName);
}
