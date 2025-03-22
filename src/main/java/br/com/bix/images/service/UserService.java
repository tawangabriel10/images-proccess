package br.com.bix.images.service;

import static br.com.bix.images.util.Constants.PLAN_BASIC;

import br.com.bix.images.api.rest.model.UserRequest;
import br.com.bix.images.api.rest.model.UserResponse;
import br.com.bix.images.data.model.UserDocument;
import br.com.bix.images.data.model.UserDocument.UserAuthority;
import br.com.bix.images.data.repository.UserRepository;
import br.com.bix.images.util.EncryptUtil;
import java.util.Collections;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserResponse findById(@NonNull String userId) {
        return userRepository.findById(userId)
            .map(user -> modelMapper.map(user, UserResponse.class))
            .orElseThrow(() -> new RuntimeException("User not found by ID:" + userId));
    }

    public UserResponse findByEmail(@NonNull String email) {
        return userRepository.findByEmail(email)
            .map(user -> modelMapper.map(user, UserResponse.class))
            .orElseThrow(() -> new RuntimeException("User not found by email:" + email));
    }

    public UserResponse save(@NonNull UserRequest user) {
        final UserAuthority authority = UserAuthority.builder()
            .authority(PLAN_BASIC)
            .build();
        final UserDocument userDocument = modelMapper.map(user, UserDocument.class);
        userDocument.setPass(EncryptUtil.encrypt(user.getPassword()));
        userDocument.setAuthorities(Collections.singletonList(authority));
        final UserDocument userSaved = userRepository.save(userDocument);
        return modelMapper.map(userSaved, UserResponse.class);
    }
}
