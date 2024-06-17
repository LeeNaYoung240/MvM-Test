package com.sparta.mvm.service;

import com.sparta.mvm.dto.SignupRequestDto;
import com.sparta.mvm.dto.SignupResponseDto;
import com.sparta.mvm.entity.User;
import com.sparta.mvm.entity.UserStatusEnum;
import com.sparta.mvm.exception.CustomException;
import com.sparta.mvm.repository.UserRepository;
import com.sparta.mvm.jwt.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.sparta.mvm.exception.ErrorEnum.BAD_DUPLICATE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        when(passwordEncoder.encode(any())).thenAnswer(invocation -> "encodedPassword");
    }

    @Test
    @DisplayName("회원 가입 - 정상적인 경우")
    void SignupSuccess() {
        // Given
        SignupRequestDto requestDto = new SignupRequestDto();
        requestDto.setUsername("이나영");
        requestDto.setPassword("dlskdud12@");
        requestDto.setName("이나영");

        User user = new User(requestDto.getUsername(), "encodedPassword", requestDto.getName(), requestDto.getEmail(), requestDto.getLineIntro(), UserStatusEnum.USER_NORMAL);
        when(userRepository.findByUsername(requestDto.getUsername())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        SignupResponseDto responseDto = userService.signup(requestDto);

        // Then
        assertNotNull(responseDto);
        assertEquals(user.getUsername(), responseDto.getUsername());
        assertEquals(user.getName(), responseDto.getName());
    }

    @Test
    @DisplayName("회원 가입 - 이미 존재하는 사용자일 경우")
    void SignupFail() {
        // Given
        SignupRequestDto requestDto = new SignupRequestDto();
        requestDto.setUsername("이나영");
        requestDto.setPassword("dlskdud12@");
        requestDto.setName("이나영");

        User existingUser = new User(requestDto.getUsername(), "encodedPassword", requestDto.getName(), requestDto.getEmail(), requestDto.getLineIntro(), UserStatusEnum.USER_NORMAL);
        when(userRepository.findByUsername(requestDto.getUsername())).thenReturn(Optional.of(existingUser));

        // When, Then
        CustomException exception = assertThrows(CustomException.class, () -> userService.signup(requestDto));
        assertEquals(BAD_DUPLICATE.getMsg(), exception.getMessage());
    }
}
