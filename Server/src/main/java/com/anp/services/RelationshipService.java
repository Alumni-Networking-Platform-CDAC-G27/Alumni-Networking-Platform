package com.anp.services;

import java.util.List;

import com.anp.domain.models.serviceModels.RelationshipServiceModel;
import com.anp.domain.models.viewModels.relationship.ConnectionCandidatesViewModel;

public interface RelationshipService {

    List<RelationshipServiceModel> findAllUserRelationshipsWithStatus(String userId) throws Exception;

    List<ConnectionCandidatesViewModel> findAllFriendCandidates(String loggedInUserId);

    boolean createRequestForAddingFriend(String loggedInUserId, String friendCandidateId) throws Exception;

    boolean removeFriend(String loggedInUserId, String friendToRemoveId) throws Exception;

    boolean acceptFriend(String loggedInUserId, String friendToAcceptId) throws Exception;

    boolean cancelFriendshipRequest(String loggedInUserId, String friendToRejectId) throws Exception;

    List<ConnectionCandidatesViewModel> searchUsers(String loggedInUserId, String search);
}
