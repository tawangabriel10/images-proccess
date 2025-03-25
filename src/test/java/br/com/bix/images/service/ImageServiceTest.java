package br.com.bix.images.service;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.bix.images.Fixture;
import br.com.bix.images.api.rest.model.ImageResponse;
import br.com.bix.images.api.rest.model.UserResponse;
import br.com.bix.images.api.stream.model.ProcessImageEvent;
import br.com.bix.images.api.stream.model.SaveImageEvent;
import br.com.bix.images.data.model.ImageDocument;
import br.com.bix.images.data.model.UserDocument;
import br.com.bix.images.data.repository.ImageRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.mongodb.core.MongoTemplate;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    private static final String FILENAME = "teste.jpeg";
    private static final String USER_ID = "asdqdasde2ee21d2d";
    private static final int PAGE_NUMBER = 1;
    private static final int PAGE_SIZE = 10;
    private static final String IMAGE_ID = "asdqdasde2ee21d2d";

    @InjectMocks private ImageService imageService;
    @Mock private ImageRepository imageRepository;
    @Mock private UserService userService;
    @Mock private ModelMapper modelMapper;
    @Mock private MongoTemplate mongoTemplate;
    @Mock private ProcessImageService processImageService;

    @Test
    void givenProcessImageEvent_whenCallSave_thenSaveImage() {
        final SaveImageEvent saveImageEvent = Fixture.make(SaveImageEvent.builder().build());
        final UserResponse userResponse = Fixture.make(UserResponse.builder().build());
        userResponse.setId("67e1c9292f2d3f2642af87a2");
        final ImageDocument imageDocument = Fixture.make(new ImageDocument());

        when(userService.findById(any())).thenReturn(userResponse);
        when(processImageService.save(any(), any())).thenReturn(FILENAME);
        when(modelMapper.map(any(), eq(ImageDocument.class))).thenReturn(imageDocument);
        when(imageRepository.save(any())).thenReturn(imageDocument);

        imageService.save(saveImageEvent);

        verify(userService).findById(saveImageEvent.getUserId());
        verify(processImageService).save(saveImageEvent, userResponse.getId());
        verify(modelMapper).map(saveImageEvent, ImageDocument.class);
        verify(imageRepository).save(imageDocument);
    }

    @Test
    void givenUserIdAndPageNumberAndPageSize_whenCallFindAll_thenReturnListImageResponse() {
        final ImageDocument imageDocument = Fixture.make(new ImageDocument());
        final ImageResponse imageResponse = Fixture.make(ImageResponse.builder().build());

        when(mongoTemplate.find(any(), eq(ImageDocument.class))).thenReturn(singletonList(imageDocument));
        when(modelMapper.map(any(), eq(ImageResponse.class))).thenReturn(imageResponse);

        final List<ImageResponse> responses = imageService.findAll(USER_ID, PAGE_NUMBER, PAGE_SIZE);
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(imageResponse.getId(), responses.get(0).getId());
        assertEquals(imageResponse.getName(), responses.get(0).getName());
        assertEquals(imageResponse.getPath(), responses.get(0).getPath());

        verify(mongoTemplate).find(any(), eq(ImageDocument.class));
        verify(modelMapper).map(imageDocument, ImageResponse.class);
    }

    @Test
    void givenImageId_whenCallFindById_thenReturnImageResponse() {
        final ImageResponse imageResponse = Fixture.make(ImageResponse.builder().build());
        final ImageDocument imageDocument = Fixture.make(new ImageDocument());

        when(imageRepository.findById(any())).thenReturn(Optional.of(imageDocument));
        when(modelMapper.map(any(), eq(ImageResponse.class))).thenReturn(imageResponse);

        final ImageResponse response = imageService.findById(IMAGE_ID);
        assertNotNull(response);

        verify(imageRepository).findById(IMAGE_ID);
        verify(modelMapper).map(imageDocument, ImageResponse.class);
    }

    @Test
    void givenImageId_whenCallFindById_thenNotFoundImageAndThrows() {
        final ImageResponse imageResponse = Fixture.make(ImageResponse.builder().build());
        final ImageDocument imageDocument = Fixture.make(new ImageDocument());

        when(imageRepository.findById(any())).thenReturn(Optional.of(imageDocument));
        when(modelMapper.map(any(), eq(ImageResponse.class))).thenReturn(imageResponse);

        final ImageResponse response = imageService.findById(IMAGE_ID);
        assertNotNull(response);

        verify(imageRepository).findById(IMAGE_ID);
        verify(modelMapper).map(imageDocument, ImageResponse.class);
    }

    @Test
    void givenProcessImageEvent_whenCallProcess_thenProcessImage() {
        final ProcessImageEvent processImageEvent = Fixture.make(ProcessImageEvent.builder().build());
        final ImageResponse imageResponse = Fixture.make(ImageResponse.builder().build());
        final ImageDocument imageDocument = Fixture.make(new ImageDocument());

        when(imageRepository.findById(any())).thenReturn(Optional.of(imageDocument));
        when(modelMapper.map(any(), eq(ImageResponse.class))).thenReturn(imageResponse);
        doNothing().when(processImageService).process(any());

        imageService.process(processImageEvent);
        verify(imageRepository).findById(processImageEvent.getImageId());
        verify(modelMapper).map(imageDocument, ImageResponse.class);
        verify(processImageService).process(processImageEvent);
    }
}
