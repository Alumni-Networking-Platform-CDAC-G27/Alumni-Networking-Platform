package com.anp.servicesImpl;

import static com.anp.utils.constants.ResponseMessageConstants.SERVER_ERROR_MESSAGE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anp.domain.entities.UserRole;
import com.anp.repositories.RoleRepository;
import com.anp.services.RoleService;
import com.anp.validations.serviceValidation.services.RoleValidationService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleValidationService roleValidation;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository, RoleValidationService roleValidation) {
        this.roleRepository = roleRepository;
        this.roleValidation = roleValidation;
    }

    @Override
    public boolean persist(UserRole role) throws Exception {
        if(!roleValidation.isValid(role)){
            throw new Exception(SERVER_ERROR_MESSAGE);
        }

        return this.roleRepository.save(role) != null;
    }

    @Override
    public UserRole getByName(String name) {
        return this.roleRepository.findByAuthority(name);
    }
}
