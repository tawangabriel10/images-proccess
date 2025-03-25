package br.com.bix.images.service;

import static io.micrometer.common.util.StringUtils.isNotBlank;

import br.com.bix.images.api.rest.model.ImageResponse;
import br.com.bix.images.api.rest.model.UserResponse;
import br.com.bix.images.api.stream.model.ProcessImageEvent;
import br.com.bix.images.api.stream.model.SaveImageEvent;
import br.com.bix.images.data.model.ImageDocument;
import br.com.bix.images.data.repository.ImageRepository;
import br.com.bix.images.service.model.BusinessException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final MongoTemplate mongoTemplate;
    private final ProcessImageService processImageService;

    public void save(@NonNull SaveImageEvent event) {
        final UserResponse user = userService.findById(event.getUserId());

        final String filename = processImageService.save(event, user.getId());

        final ImageDocument image = modelMapper.map(event, ImageDocument.class);
        image.setCreateAt(LocalDateTime.now());
        image.setUserId(new ObjectId(user.getId()));
        image.setPath(filename);

        imageRepository.save(image);
    }

    public List<ImageResponse> findAll(String name, Integer pageNumber, Integer pageSize) {
        log.info("Finding all images by params");

        final List<Criteria> criterias = new ArrayList<>();
        if (isNotBlank(name)) {
            criterias.add(Criteria.where("name").regex(name, "i"));
        }
        final Criteria criteria = new Criteria();
        if (!criterias.isEmpty()) {
            criteria.andOperator(criterias);
        }

        final Pageable page = PageRequest.of(pageNumber, pageSize);

        Query query = new Query()
            .addCriteria(criteria)
            .with(page);

        return mongoTemplate.find(query, ImageDocument.class)
            .stream()
            .map(order -> modelMapper.map(order, ImageResponse.class))
            .toList();
    }

    public ImageResponse findById(@NonNull String imageId) {
        return imageRepository.findById(imageId)
            .map(image -> modelMapper.map(image, ImageResponse.class))
            .orElseThrow(() -> new BusinessException("Image not found"));
    }

    public void process(@NonNull ProcessImageEvent event) {
        final ImageResponse imageResponse = findById(event.getImageId());
        event.setPath(imageResponse.getPath());
        processImageService.process(event);
    }
}
