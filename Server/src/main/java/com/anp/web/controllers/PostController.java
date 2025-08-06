package com.anp.web.controllers;

import static com.anp.utils.constants.ResponseMessageConstants.SERVER_ERROR_MESSAGE;
import static com.anp.utils.constants.ResponseMessageConstants.SUCCESSFUL_CREATE_POST_MESSAGE;
import static com.anp.utils.constants.ResponseMessageConstants.SUCCESSFUL_POST_DELETE_MESSAGE;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anp.domain.models.bindingModels.post.PostCreateBindingModel;
import com.anp.domain.models.serviceModels.PostServiceModel;
import com.anp.domain.models.viewModels.post.PostAllViewModel;
import com.anp.services.PostService;
import com.anp.utils.responseHandler.exceptions.CustomException;
import com.anp.utils.responseHandler.successResponse.SuccessResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;

@RestController()
@RequestMapping(value = "/post")
public class PostController {

    private final PostService postService;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    @Autowired
    public PostController(PostService postService, ModelMapper modelMapper, ObjectMapper objectMapper) {
        this.postService = postService;
        this.modelMapper = modelMapper;
        this.objectMapper = objectMapper;
    }

    @PostMapping (value = "/create")
    public ResponseEntity<Object> createPost(@RequestBody @Valid PostCreateBindingModel postCreateBindingModel, Authentication principal) throws Exception {
        boolean post = this.postService.createPost(postCreateBindingModel);
        if (post) {
            SuccessResponse successResponse = new SuccessResponse(LocalDateTime.now(), SUCCESSFUL_CREATE_POST_MESSAGE, " ", true);
            return new ResponseEntity<>(this.objectMapper.writeValueAsString(successResponse), HttpStatus.OK);
        }

        throw new CustomException(SERVER_ERROR_MESSAGE);
    }

    @GetMapping(value = "/all/{id}")
    public List<PostAllViewModel> getAllPosts(@PathVariable(value = "id") String timelineUserId) {
        try {
            List<PostServiceModel> postServiceAllPosts = this.postService.getAllPosts(timelineUserId);

            return postServiceAllPosts.stream().map(postServiceModel -> {
                PostAllViewModel postAllViewModel = this.modelMapper.map(postServiceModel, PostAllViewModel.class);
                postAllViewModel.setLikeCount(postServiceModel.getLike().size());
                postAllViewModel.setPostId(postServiceModel.getId());
                return postAllViewModel;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            throw new CustomException(SERVER_ERROR_MESSAGE);
        }
    }

    @PostMapping(value = "/remove")
    public ResponseEntity removePost(@RequestBody Map<String, Object> body) throws Exception {
        String loggedInUserId = (String) body.get("loggedInUserId");
        String postToRemoveId = (String) body.get("postToRemoveId");

        CompletableFuture<Boolean> result = this.postService.deletePost(loggedInUserId, postToRemoveId);

        if (result.get()) {
            SuccessResponse successResponse = new SuccessResponse(LocalDateTime.now(), SUCCESSFUL_POST_DELETE_MESSAGE, "", true);
            return new ResponseEntity<>(this.objectMapper.writeValueAsString(successResponse), HttpStatus.OK);
        }

        throw new CustomException(SERVER_ERROR_MESSAGE);
    }

}
