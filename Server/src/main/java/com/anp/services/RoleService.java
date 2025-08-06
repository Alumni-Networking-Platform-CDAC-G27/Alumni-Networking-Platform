package com.anp.services;

import com.anp.domain.entities.UserRole;

public interface RoleService {
    boolean persist(UserRole role) throws Exception;

    UserRole getByName(String name);
}
