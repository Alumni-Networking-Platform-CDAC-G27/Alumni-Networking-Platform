package com.anp.web.controllers;

import static com.anp.utils.constants.ResponseMessageConstants.PASSWORDS_MISMATCH_ERROR_MESSAGE;
import static com.anp.utils.constants.ResponseMessageConstants.SUCCESSFUL_REGISTER_MESSAGE;
import static com.anp.utils.constants.ResponseMessageConstants.SUCCESSFUL_USER_DELETE_MESSAGE;
import static com.anp.utils.constants.ResponseMessageConstants.SUCCESSFUL_USER_DEMOTED_MESSAGE;
import static com.anp.utils.constants.ResponseMessageConstants.SUCCESSFUL_USER_PROFILE_EDIT_MESSAGE;
import static com.anp.utils.constants.ResponseMessageConstants.SUCCESSFUL_USER_PROMOTED_MESSAGE;
import static com.anp.utils.constants.ResponseMessageConstants.USER_FAILURE_DEMOTING_MESSAGE;
import static com.anp.utils.constants.ResponseMessageConstants.USER_FAILURE_PROMOTING_MESSAGE;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anp.domain.models.bindingModels.user.UserRegisterBindingModel;
import com.anp.domain.models.bindingModels.user.UserUpdateBindingModel;
import com.anp.domain.models.serviceModels.UserServiceModel;
import com.anp.domain.models.viewModels.user.UserAllViewModel;
import com.anp.domain.models.viewModels.user.UserCreateViewModel;
import com.anp.domain.models.viewModels.user.UserDetailsViewModel;
import com.anp.services.UserService;
import com.anp.utils.responseHandler.exceptions.BadRequestException;
import com.anp.utils.responseHandler.exceptions.CustomException;
import com.anp.utils.responseHandler.successResponse.SuccessResponse;
import com.anp.validations.serviceValidation.services.UserValidationService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/users")
public class UserController {
    private static final String SERVER_ERROR_MESSAGE = "Server Error";

    private final UserService userService;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final UserValidationService userValidationService;

    @Autowired
    public UserController(UserService userService, ModelMapper modelMapper, ObjectMapper objectMapper, UserValidationService userValidationService) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.objectMapper = objectMapper;
        this.userValidationService = userValidationService;
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> registerUser(@RequestBody @Valid UserRegisterBindingModel userRegisterBindingModel) throws Exception {

        if (!userValidationService.isValid(userRegisterBindingModel.getPassword(), userRegisterBindingModel.getConfirmPassword())) {
            throw new BadRequestException(PASSWORDS_MISMATCH_ERROR_MESSAGE);
        }

        if (!userValidationService.isValid(userRegisterBindingModel)) {
            throw new Exception(SERVER_ERROR_MESSAGE);
        }

        UserServiceModel user = modelMapper.map(userRegisterBindingModel, UserServiceModel.class);
        UserCreateViewModel savedUser = this.userService.createUser(user);

        SuccessResponse successResponse = successResponseBuilder(LocalDateTime.now(), SUCCESSFUL_REGISTER_MESSAGE, savedUser, true);

        return new ResponseEntity<>(this.objectMapper.writeValueAsString(successResponse), HttpStatus.OK);
    }

    @GetMapping(value = "/all/{id}")
    public List<UserAllViewModel> getAllUsers(@PathVariable(value = "id") String userId) throws Exception {
        List<UserServiceModel> allUsers = this.userService.getAllUsers(userId);

        return allUsers.stream()
                .map(x -> {
                    UserAllViewModel userAllViewModel = this.modelMapper.map(x, UserAllViewModel.class);
                    userAllViewModel.setRole(x.extractAuthority());
                    return userAllViewModel;
                })
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/details/{id}")
    public ResponseEntity getDetails(@PathVariable String id) throws Exception {
        UserDetailsViewModel user = this.userService.getById(id);

        return new ResponseEntity<>(this.objectMapper.writeValueAsString(user), HttpStatus.OK);
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity updateUser(@RequestBody @Valid UserUpdateBindingModel userUpdateBindingModel,
                                     @PathVariable(value = "id") String loggedInUserId) throws Exception {

        if(!userValidationService.isValid(userUpdateBindingModel)){
            throw new Exception(SERVER_ERROR_MESSAGE);
        }

        UserServiceModel userServiceModel = this.modelMapper.map(userUpdateBindingModel, UserServiceModel.class);
        boolean result = this.userService.updateUser(userServiceModel, loggedInUserId);

        if (result) {
            SuccessResponse successResponse = successResponseBuilder(LocalDateTime.now(), SUCCESSFUL_USER_PROFILE_EDIT_MESSAGE, "", true);
            return new ResponseEntity<>(this.objectMapper.writeValueAsString(successResponse), HttpStatus.OK);
        }

        throw new CustomException(SERVER_ERROR_MESSAGE);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable String id) throws Exception {
            boolean result = this.userService.deleteUserById(id);

            if(result){
                SuccessResponse successResponse = successResponseBuilder(LocalDateTime.now(), SUCCESSFUL_USER_DELETE_MESSAGE, "", true);
                return new ResponseEntity<>(this.objectMapper.writeValueAsString(successResponse), HttpStatus.OK);
            }

            throw new CustomException(SERVER_ERROR_MESSAGE);
    }


    @PostMapping(value = "/promote")
    public ResponseEntity promoteUser(@RequestParam(name = "id") String id) throws Exception {
        boolean resultOfPromoting = this.userService.promoteUser(id);

        if (resultOfPromoting) {
            SuccessResponse successResponse = successResponseBuilder(LocalDateTime.now(), SUCCESSFUL_USER_PROMOTED_MESSAGE, "", true);
            return new ResponseEntity<>(this.objectMapper.writeValueAsString(successResponse), HttpStatus.OK);
        }

        throw new CustomException(USER_FAILURE_PROMOTING_MESSAGE);
    }

    @PostMapping(value = "/demote")
    public ResponseEntity demoteUser(@RequestParam(name = "id") String id) throws Exception {
        boolean resultOfDemoting = this.userService.demoteUser(id);

        if (resultOfDemoting) {

            SuccessResponse successResponse = successResponseBuilder(LocalDateTime.now(), SUCCESSFUL_USER_DEMOTED_MESSAGE, "", true);
            return new ResponseEntity<>(this.objectMapper.writeValueAsString(successResponse), HttpStatus.OK);
        }
        throw new CustomException(USER_FAILURE_DEMOTING_MESSAGE);
    }

    private SuccessResponse successResponseBuilder(LocalDateTime timestamp, String message, Object payload, boolean success) {
        return new SuccessResponse(timestamp, message, payload, success);
    }
}
