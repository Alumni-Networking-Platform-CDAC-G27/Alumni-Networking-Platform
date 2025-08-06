package com.anp.servicesImpl;

import static com.anp.utils.constants.ResponseMessageConstants.SERVER_ERROR_MESSAGE;
import static com.anp.utils.constants.ResponseMessageConstants.UNAUTHORIZED_SERVER_ERROR_MESSAGE;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.anp.domain.entities.Picture;
import com.anp.domain.entities.User;
import com.anp.domain.entities.UserRole;
import com.anp.domain.models.serviceModels.PictureServiceModel;
import com.anp.repositories.PictureRepository;
import com.anp.repositories.RoleRepository;
import com.anp.repositories.UserRepository;
import com.anp.services.CloudinaryService;
import com.anp.services.PictureService;
import com.anp.utils.responseHandler.exceptions.CustomException;
import com.anp.validations.serviceValidation.services.CloudinaryValidationService;
import com.anp.validations.serviceValidation.services.PictureValidationService;
import com.anp.validations.serviceValidation.services.UserValidationService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class PictureServiceImpl implements PictureService {
    private final PictureRepository pictureRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CloudinaryService cloudinaryService;
    private final ModelMapper modelMapper;
    private final UserValidationService userValidation;
    private final PictureValidationService pictureValidation;
    private final CloudinaryValidationService cloudinaryValidation;

    @Autowired
    public PictureServiceImpl(PictureRepository pictureRepository, UserRepository userRepository,
                              RoleRepository roleRepository, CloudinaryService cloudinaryService, ModelMapper modelMapper, UserValidationService userValidation, PictureValidationService pictureValidation, CloudinaryValidationService cloudinaryValidation) {
        this.pictureRepository = pictureRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.cloudinaryService = cloudinaryService;
        this.modelMapper = modelMapper;
        this.userValidation = userValidation;
        this.pictureValidation = pictureValidation;
        this.cloudinaryValidation = cloudinaryValidation;
    }

    @Override
    public boolean addPicture(String loggedInUserId, MultipartFile file) throws Exception {
        User user = this.userRepository.findById(loggedInUserId).orElse(null);

        if (!userValidation.isValid(user)) {
            throw new Exception(SERVER_ERROR_MESSAGE);
        }

        String cloudinaryPublicId = UUID.randomUUID().toString();
        Map uploadMap = this.cloudinaryService.uploadImage(file, cloudinaryPublicId);

        if (!cloudinaryValidation.isValid(uploadMap)) {
            throw new Exception(SERVER_ERROR_MESSAGE);
        }

        Picture picture = new Picture();
        picture.setImageUrl(uploadMap.get("url").toString());
        picture.setUser(user);
        picture.setTime(LocalDateTime.now());
        picture.setCloudinaryPublicId(uploadMap.get("public_id").toString());

        return this.pictureRepository.save(picture) != null;
    }

    @Override
    public List<PictureServiceModel> getAllPicturesByUserId(String userId) {
        List<Picture> pictureList = this.pictureRepository.findAllByUserId(userId);

        return pictureList
                .stream()
                .map(picture -> this.modelMapper
                        .map(picture, PictureServiceModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean deletePicture(String loggedInUserId, String photoToRemoveId) throws Exception {
        User loggedInUser = this.userRepository.findById(loggedInUserId).orElse(null);
        Picture photoToRemove = this.pictureRepository.findById(photoToRemoveId).orElse(null);

        if (!userValidation.isValid(loggedInUser) || !pictureValidation.isValid(photoToRemove)) {
            throw new Exception(SERVER_ERROR_MESSAGE);
        }


        UserRole rootRole = this.roleRepository.findByAuthority("ROOT");
        boolean hasRootAuthority = loggedInUser.getAuthorities().contains(rootRole);
        boolean pictureOwnershipCheck = photoToRemove.getUser().getId().equals(loggedInUserId);

        if (hasRootAuthority || pictureOwnershipCheck) {
            this.pictureRepository.delete(photoToRemove);

            String cloudinaryPublicId = photoToRemove.getCloudinaryPublicId();

            return this.cloudinaryService.deleteImage(cloudinaryPublicId);
        } else {
            throw new CustomException(UNAUTHORIZED_SERVER_ERROR_MESSAGE);
        }
    }
}
