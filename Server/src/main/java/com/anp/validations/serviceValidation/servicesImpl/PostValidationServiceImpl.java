package com.anp.validations.serviceValidation.servicesImpl;

import org.springframework.stereotype.Component;

import com.anp.domain.entities.Post;
import com.anp.domain.models.bindingModels.post.PostCreateBindingModel;
import com.anp.validations.serviceValidation.services.PostValidationService;

@Component
public class PostValidationServiceImpl implements PostValidationService {
    @Override
    public boolean isValid(Post post) {
        return post != null;
    }

    @Override
    public boolean isValid(PostCreateBindingModel postCreateBindingModel) {
        return postCreateBindingModel != null;
    }
}
