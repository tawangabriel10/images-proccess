package br.com.bix.images.service;

import static br.com.bix.images.util.Constants.MESSAGE_PROCESS_IMAGE;
import static br.com.bix.images.util.Constants.PATH_TO_SAVE_IMAGE;
import static br.com.bix.images.util.Constants.SUBJECT_PROCESS_IMAGE;

import br.com.bix.images.api.rest.model.UserResponse;
import br.com.bix.images.api.stream.model.ProcessImageEvent;
import br.com.bix.images.api.stream.model.SaveImageEvent;
import br.com.bix.images.service.model.BusinessException;
import br.com.bix.images.util.EmailUtil;
import br.com.bix.images.util.ProcessFilterImage;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import javax.imageio.ImageIO;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessImageService {

    private final EmailUtil emailUtil;
    private final UserService userService;

    public String save(@NonNull SaveImageEvent event, String userId) {
        try {
            final String filename = PATH_TO_SAVE_IMAGE + userId + "_" + event.getName();
            ClassPathResource res = new ClassPathResource(filename);
            File file = new File(res.getPath());
            FileUtils.writeByteArrayToFile(file, Base64.getDecoder().decode(event.getImageData().getData()));
            return filename;
        } catch(IOException ex) {
            throw new BusinessException("Failed to upload image: " + ex.getMessage());
        }
    }

    public void process(@NonNull ProcessImageEvent event) {
        try {
            File file = new File(event.getPath());
            BufferedImage image = ImageIO.read(file);

            BufferedImage newImage = applyUpdateImage(event, image);
            ImageIO.write(newImage, "jpeg", file);

            final UserResponse userResponse = userService.findById(event.getUserId());
            emailUtil.sendSimpleMessage(userResponse.getEmail(), SUBJECT_PROCESS_IMAGE, MESSAGE_PROCESS_IMAGE);
        } catch (IOException ex) {
            throw new BusinessException("Failed to update image: %s, message: %s" + ex.getMessage());
        }
    }

    private BufferedImage applyUpdateImage(ProcessImageEvent event, BufferedImage image) {
        BufferedImage newImage = null;
        switch (event.getAction()) {
            case RESIZE:
                newImage = ProcessFilterImage.resize(image, 400, 400);
                break;
            case NEGATIVE:
                newImage = ProcessFilterImage.negative(image);
                break;
            case GRAY:
                newImage = ProcessFilterImage.gray(image);
                break;
            default:
                throw new BusinessException("Action not found: " + event.getAction());
        }
        return newImage;
    }
}
