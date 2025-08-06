package com.anp.validations.serviceValidation.services;

import com.anp.domain.entities.Post;
import com.anp.domain.models.bindingModels.post.PostCreateBindingModel;

public interface PostValidationService {
    boolean isValid(Post post);

    boolean isValid(PostCreateBindingModel postCreateBindingModel);
}
