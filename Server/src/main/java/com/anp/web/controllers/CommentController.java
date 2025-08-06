package com.anp.web.controllers;

import static com.anp.utils.constants.ResponseMessageConstants.SERVER_ERROR_MESSAGE;
import static com.anp.utils.constants.ResponseMessageConstants.SUCCESSFUL_CREATE_COMMENT_MESSAGE;
import static com.anp.utils.constants.ResponseMessageConstants.SUCCESSFUL_DELETE_COMMENT_MESSAGE;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anp.domain.models.bindingModels.comment.CommentCreateBindingModel;
import com.anp.services.CommentService;
import com.anp.utils.constants.ResponseMessageConstants;
import com.anp.utils.responseHandler.exceptions.CustomException;
import com.anp.utils.responseHandler.successResponse.SuccessResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;

@RestController()
@RequestMapping(value = "/comment")
public class CommentController {

    private final CommentService commentService;
    private final ObjectMapper objectMapper;

    @Autowired
    public CommentController( CommentService commentService, ObjectMapper objectMapper) {
        this.commentService = commentService;
        this.objectMapper = objectMapper;
    }

    @PostMapping(value = "/create")
    public ResponseEntity<Object> createComment(@RequestBody @Valid CommentCreateBindingModel commentCreateBindingModel) throws Exception {
        boolean comment = this.commentService.createComment(commentCreateBindingModel);
        if (comment) {
            SuccessResponse successResponse = new SuccessResponse(LocalDateTime.now(), SUCCESSFUL_CREATE_COMMENT_MESSAGE, "", true);
            return new ResponseEntity<>(this.objectMapper.writeValueAsString(successResponse), HttpStatus.OK);
        }
        throw new CustomException(SERVER_ERROR_MESSAGE);
    }

    @PostMapping(value = "/remove")
    public ResponseEntity removeComment(@RequestBody Map<String, Object> body) throws Exception {
        String loggedInUserId = (String) body.get("loggedInUserId");
        String commentToRemoveId = (String) body.get("commentToRemoveId");

        CompletableFuture<Boolean> result = this.commentService.deleteComment(loggedInUserId, commentToRemoveId);
        if (result.get()) {
            SuccessResponse successResponse = new SuccessResponse(LocalDateTime.now(), SUCCESSFUL_DELETE_COMMENT_MESSAGE, "", true);
            return new ResponseEntity<>(this.objectMapper.writeValueAsString(successResponse), HttpStatus.OK);
        }
        throw new CustomException(ResponseMessageConstants.SERVER_ERROR_MESSAGE);
    }
}
