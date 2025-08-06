package com.anp.servicesImpl;

import static com.anp.utils.constants.ResponseMessageConstants.SERVER_ERROR_MESSAGE;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.anp.domain.entities.Comment;
import com.anp.domain.entities.Post;
import com.anp.domain.entities.User;
import com.anp.domain.entities.UserRole;
import com.anp.domain.models.bindingModels.post.PostCreateBindingModel;
import com.anp.domain.models.serviceModels.PostServiceModel;
import com.anp.repositories.LikeRepository;
import com.anp.repositories.PostRepository;
import com.anp.repositories.RoleRepository;
import com.anp.repositories.UserRepository;
import com.anp.services.PostService;
import com.anp.validations.serviceValidation.services.PostValidationService;
import com.anp.validations.serviceValidation.services.UserValidationService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final PostValidationService postValidationService;
    private final UserValidationService userValidationService;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository, LikeRepository likeRepository, RoleRepository roleRepository, ModelMapper modelMapper, PostValidationService postValidationService, UserValidationService userValidationService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
        this.postValidationService = postValidationService;
        this.userValidationService = userValidationService;
    }

    @Override
    public boolean createPost(PostCreateBindingModel postCreateBindingModel) throws Exception {
        if (!postValidationService.isValid(postCreateBindingModel)) {
            throw new Exception(SERVER_ERROR_MESSAGE);
        }

        User loggedInUser = this.userRepository
                .findById(postCreateBindingModel.getLoggedInUserId())
                .filter(userValidationService::isValid)
                .orElseThrow(Exception::new);

        User timelineUser = this.userRepository
                .findById(postCreateBindingModel.getTimelineUserId())
                .filter(userValidationService::isValid)
                .orElseThrow(Exception::new);

        PostServiceModel postServiceModel = new PostServiceModel();
        postServiceModel.setLoggedInUser(loggedInUser);
        postServiceModel.setTimelineUser(timelineUser);
        postServiceModel.setContent(postCreateBindingModel.getContent());
        postServiceModel.setImageUrl(postCreateBindingModel.getImageUrl());
        postServiceModel.setTime(LocalDateTime.now());
        postServiceModel.setLike(new ArrayList<>());
        postServiceModel.setCommentList(new ArrayList<>());

        Post post = this.modelMapper.map(postServiceModel, Post.class);

        if (postValidationService.isValid(post)) {
            return this.postRepository.save(post) != null;
        }
        return false;
    }

    @Override
    public List<PostServiceModel> getAllPosts(String timelineUserId) {
        List<Post> postList = this.postRepository.findAllByTimelineUserIdOrderByTimeDesc(timelineUserId);

        return postList
                .stream()
                .map(post -> this.modelMapper
                        .map(post, PostServiceModel.class))
                .peek(postServiceModel -> {
                    List<Comment> commentList = postServiceModel.getCommentList()
                            .stream()
                            .sorted((comment1, comment2) -> {
                                if (comment1.getTime().isAfter(comment2.getTime())) {
                                    return 1;
                                } else if (comment1.getTime().isBefore(comment2.getTime())) {
                                    return -1;
                                }
                                return 0;
                            }).collect(Collectors.toList());

                    postServiceModel.setCommentList(commentList);
                })
                .collect(Collectors.toList());
    }

    @Async
    @Override
    public CompletableFuture<Boolean> deletePost(String loggedInUserId, String postToRemoveId) throws Exception {
        User loggedInUser = this.userRepository.findById(loggedInUserId).orElse(null);
        Post postToRemove = this.postRepository.findById(postToRemoveId).orElse(null);

        if (!userValidationService.isValid(loggedInUser) || !postValidationService.isValid(postToRemove)) {
            throw new Exception(SERVER_ERROR_MESSAGE);
        }

        UserRole rootRole = this.roleRepository.findByAuthority("ROOT");
        boolean hasRootAuthority = loggedInUser.getAuthorities().contains(rootRole);
        boolean isPostCreator = postToRemove.getLoggedInUser().getId().equals(loggedInUserId);
        boolean isTimeLineUser = postToRemove.getTimelineUser().getId().equals(loggedInUserId);

        if (hasRootAuthority || isPostCreator || isTimeLineUser) {
            try {
                this.postRepository.delete(postToRemove);
                return CompletableFuture.completedFuture(true);
            } catch (Exception e) {
                throw new Exception(SERVER_ERROR_MESSAGE);
            }
        } else {
            throw new Exception(SERVER_ERROR_MESSAGE);
        }
    }
}
