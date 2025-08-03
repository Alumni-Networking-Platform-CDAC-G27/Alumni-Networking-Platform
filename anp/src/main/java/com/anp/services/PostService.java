package com.anp.services;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.anp.domain.models.bindingModels.post.PostCreateBindingModel;
import com.anp.domain.models.serviceModels.PostServiceModel;

public interface PostService {
    boolean createPost(PostCreateBindingModel postCreateBindingModel) throws Exception;

    List<PostServiceModel> getAllPosts(String timelineUserId);

    CompletableFuture<Boolean> deletePost(String loggedInUserId, String postToRemoveId) throws Exception;
}
