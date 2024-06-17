package com.sparta.mvm.entity;

import com.sparta.mvm.dto.ProfileRequestDto;
import com.sparta.mvm.dto.SignupRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UserTest {
    private User user;

    @BeforeEach
    void setUp() {
        SignupRequestDto signupRequestDto = new SignupRequestDto();
        signupRequestDto.setUsername("dlskdud123");
        signupRequestDto.setPassword("dlskdud12!");
        signupRequestDto.setName("이나영");
        signupRequestDto.setEmail("dlskdud@test.com");

        user = new User(signupRequestDto);
    }

    @DisplayName("User Entity 생성 테스트")
    @Test
    void createUserEntity() {
        // given
        SignupRequestDto signupRequestDto = new SignupRequestDto();
        signupRequestDto.setUsername("dlskdud123");
        signupRequestDto.setPassword("dlskdud12!");
        signupRequestDto.setName("이나영");
        signupRequestDto.setEmail("dlskdud@test.com");


        // when
        User newUser = new User(signupRequestDto);

        // then
        assertThat(newUser).isNotNull();
        assertThat(newUser.getUsername()).isEqualTo("dlskdud123");
        assertThat(newUser.getPassword()).isEqualTo("dlskdud12!");
        assertThat(newUser.getName()).isEqualTo("이나영");
        assertThat(newUser.getEmail()).isEqualTo("dlskdud@test.com");
        assertThat(newUser.getUserStatus()).isEqualTo(UserStatusEnum.USER_NORMAL);
    }

    @DisplayName("User Entity 비밀번호 변경 테스트")
    @Test
    void updateUserPassword() {
        // given
        ProfileRequestDto profileRequestDto = new ProfileRequestDto();
        profileRequestDto.setChangedPassword("NewPassword123!");

        // when
        user.update(profileRequestDto);

        // then
        assertThat(user.getPassword()).isEqualTo("NewPassword123!");
    }

    @DisplayName("User Entity 상태 변경 테스트")
    @Test
    void resignUserStatus() {
        // given

        // when
        user.resignStatus();

        // then
        assertThat(user.getUserStatus()).isEqualTo(UserStatusEnum.USER_RESIGN);
    }

    @DisplayName("User Entity Refresh Token 설정 테스트")
    @Test
    void setUserRefreshToken() {
        // given
        String refreshToken = "abc123def456";

        // when
        user.setRefreshToken(refreshToken);

        // then
        assertThat(user.getRefreshToken()).isEqualTo(refreshToken);
    }

    @Nested
    @DisplayName("User Entity 생성 실패 테스트")
    class CreateInvalidUserEntity {

        @DisplayName("이름 공백 예외 발생 테스트")
        @Test
        void InvalidBlankName() {
            // given
            SignupRequestDto signupRequestDto = new SignupRequestDto();
            signupRequestDto.setUsername("dlskdud123");
            signupRequestDto.setPassword("dlskdud12!");
            signupRequestDto.setName(" ");

            // when - then
            assertThatThrownBy(() -> new User(signupRequestDto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("이름은 공백일 수 없습니다.");
        }

        @DisplayName("이메일 형식 예외 발생 테스트")
        @Test
        void InvalidEmailFormat() {
            // given
            SignupRequestDto signupRequestDto = new SignupRequestDto();
            signupRequestDto.setUsername("dlskdud123");
            signupRequestDto.setPassword("dlskdud12!");
            signupRequestDto.setName("이나영");
            signupRequestDto.setEmail("invalid-email-format");

            // when - then
            assertThatThrownBy(() -> new User(signupRequestDto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("올바른 형식의 이메일 주소여야 합니다");
        }
    }

}
