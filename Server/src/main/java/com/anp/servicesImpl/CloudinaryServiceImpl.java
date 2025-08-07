package com.anp.servicesImpl;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.anp.services.CloudinaryService;
import static com.anp.utils.constants.ResponseMessageConstants.SERVER_ERROR_MESSAGE;
import com.anp.validations.serviceValidation.services.CloudinaryValidationService;
import com.cloudinary.Cloudinary;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {
    private final Cloudinary cloudinary;
    private final CloudinaryValidationService cloudinaryValidation;

    @Autowired
    public CloudinaryServiceImpl(Cloudinary cloudinary, CloudinaryValidationService cloudinaryValidation) {
        this.cloudinary = cloudinary;
        this.cloudinaryValidation = cloudinaryValidation;
    }

    @Override
    public Map uploadImage(MultipartFile multipartFile, String uuid) throws Exception {
        if (!cloudinaryValidation.isValid(multipartFile, uuid)) {
            throw new Exception(SERVER_ERROR_MESSAGE);
        }

        Map params = new HashMap<String, Object>() {{
            put("public_id", uuid);
            put("overwrite", true);
        }};

        File fileToUpload = File.createTempFile("temp-file", multipartFile.getOriginalFilename());
        multipartFile.transferTo(fileToUpload);

        Map upload = this.cloudinary
                .uploader()
                .upload(fileToUpload, params);

        return upload;
    }

    @Override
    public boolean deleteImage(String cloudinaryPublicId) throws Exception {
        if (!cloudinaryValidation.isValid(cloudinaryPublicId)) {
            throw new Exception(SERVER_ERROR_MESSAGE);
        }

        Map result = this.cloudinary.api().deleteResources(
                Collections.singletonList(cloudinaryPublicId),
                new HashMap<>()
        );

        Map deleted = (Map) result.get("deleted");
        String deletingResult = deleted.get(cloudinaryPublicId).toString();

        return "deleted".equals(deletingResult);
    }

}
