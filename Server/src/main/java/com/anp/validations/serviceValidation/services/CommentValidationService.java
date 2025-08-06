package com.anp.validations.serviceValidation.services;

import com.anp.domain.entities.Comment;
import com.anp.domain.models.bindingModels.comment.CommentCreateBindingModel;

public interface CommentValidationService {
    boolean isValid(Comment comment);

    boolean isValid(CommentCreateBindingModel commentCreateBindingModel);
}
