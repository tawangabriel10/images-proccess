package br.com.bix.images.service;

import br.com.bix.images.Fixture;
import br.com.bix.images.api.rest.model.UserResponse;
import br.com.bix.images.data.model.UserDocument;
import br.com.bix.images.data.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final String USER_ID = "ashdguydqwdgv123124";

    @InjectMocks private UserService userService;
    @Mock private UserRepository userRepository;
    @Mock  private ModelMapper modelMapper;

    @Test
    void givenUserId_whenCallFindById_thenReturnUserResponse() {
        final UserResponse userResponse = Fixture.make(UserResponse.builder().build());
        final UserDocument userDocument = Fixture.make(new UserDocument());

        when(userRepository.findById(any())).thenReturn(Optional.of(userDocument));
        when(modelMapper.map(any(), eq(UserResponse.class))).thenReturn(userResponse);

        final UserResponse response = userService.findById(USER_ID);
        assertNotNull(response);
        verify(userRepository).findById(USER_ID);
        verify(modelMapper).map(userDocument, UserResponse.class);
    }
}
