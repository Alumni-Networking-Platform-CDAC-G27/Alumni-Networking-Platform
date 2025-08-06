package com.anp.validations.serviceValidation.servicesImpl;

import org.springframework.stereotype.Component;

import com.anp.domain.entities.Comment;
import com.anp.domain.models.bindingModels.comment.CommentCreateBindingModel;
import com.anp.validations.serviceValidation.services.CommentValidationService;

@Component
public class CommentValidationServiceImpl implements CommentValidationService {
    @Override
    public boolean isValid(Comment comment) {
        return comment != null;
    }

    @Override
    public boolean isValid(CommentCreateBindingModel commentCreateBindingModel) {
        return commentCreateBindingModel != null;
    }
}
