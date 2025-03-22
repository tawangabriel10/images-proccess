package br.com.bix.images.service;

import static br.com.bix.images.util.Constants.PATH_TO_SAVE_IMAGE;

import br.com.bix.images.api.stream.model.ProcessImageEvent;
import br.com.bix.images.api.stream.model.SaveImageEvent;
import br.com.bix.images.util.ProcessFilterImage;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
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

    public String save(@NonNull SaveImageEvent event, String userId) {
        try {
            final String filename = PATH_TO_SAVE_IMAGE + userId + "_" + event.getName();
            ClassPathResource res = new ClassPathResource(filename);
            File file = new File(res.getPath());
            FileUtils.writeByteArrayToFile(file, Base64.getDecoder().decode(event.getImageData().getData()));
            return filename;
        } catch(IOException ex) {
            return null;
        }
    }

    public void process(@NonNull ProcessImageEvent event) {
        File file = new File(event.getPath());
        BufferedImage image = null;
        try {
            image = ImageIO.read(file);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }

        BufferedImage newImage = null;
        switch (event.getAction()) {
            case ORIGINAL:
                break;
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
                throw new RuntimeException();
        }

        try {
            ImageIO.write(newImage, "jpeg", file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
