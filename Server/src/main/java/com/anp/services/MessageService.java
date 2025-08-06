package com.anp.services;

import java.util.List;

import com.anp.domain.models.bindingModels.message.MessageCreateBindingModel;
import com.anp.domain.models.serviceModels.MessageServiceModel;
import com.anp.domain.models.viewModels.message.MessageFriendsViewModel;

public interface MessageService {

    MessageServiceModel createMessage(MessageCreateBindingModel messageCreateBindingModel, String loggedInUsername) throws Exception;

    List<MessageServiceModel> getAllMessages(String loggedInUsername, String chatUserId);

    List<MessageFriendsViewModel> getAllFriendMessages(String loggedInUsername);
}
