package com.sparta.mvm.dtotest;

import com.sparta.mvm.dto.SignupRequestDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class SignupRequestDtoTest {

    private Validator validator;

    public SignupRequestDtoTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @DisplayName("SignupRequestDto 생성")
    @Nested
    class CreateSignupRequestDto {

        @DisplayName("SignupRequestDto 생성 성공")
        @Test
        void createSignupRequestDtoSuccess() {
            // given
            SignupRequestDto signupRequestDto = new SignupRequestDto();
            signupRequestDto.setUsername("dlskdud123");
            signupRequestDto.setPassword("dlskdud12!");
            signupRequestDto.setName("이나영");
            signupRequestDto.setEmail("dlskdud@test.com");

            // when
            Set<ConstraintViolation<SignupRequestDto>> violations = validator.validate(signupRequestDto);

            // then
            assertThat(violations).isEmpty();
        }

        @DisplayName("SignupRequestDto 생성 실패 : username 오류")
        @Test
        void createSignupRequestDtoFailUsername() {
            // given
            SignupRequestDto signupRequestDto = new SignupRequestDto();
            signupRequestDto.setUsername("");
            signupRequestDto.setPassword("dlskdud12!");
            signupRequestDto.setName("이나영");
            signupRequestDto.setEmail("dlskdud@test.com");

            // when
            Set<ConstraintViolation<SignupRequestDto>> violations = validator.validate(signupRequestDto);

            // then
            assertThat(violations).hasSize(3);
            assertThat(violations)
                    .extracting("message")
                    .contains("ID는 공백일 수 없습니다.")
                    .contains("아이디는 최소 10자 이상, 20자 이하로 작성해주세요.")
                    .contains("아이디는 대소문자 포함 영문 + 숫자만을 허용합니다.");

        }

        @DisplayName("SignupRequestDto 생성 실패 : password 오류")
        @Test
        void createSignupRequestDtoFailPassword() {
            // given
            SignupRequestDto signupRequestDto = new SignupRequestDto();
            signupRequestDto.setUsername("dlskdud123");
            signupRequestDto.setPassword("");
            signupRequestDto.setName("이나영");
            signupRequestDto.setEmail("dlskdud@test.com");

            // when
            Set<ConstraintViolation<SignupRequestDto>> violations = validator.validate(signupRequestDto);

            // then
            assertThat(violations).hasSize(3);
            assertThat(violations)
                    .extracting("message")
                    .contains("비밀번호는 공백일 수 없습니다.")
                    .contains("대소문자 포함 영문 + 숫자 + 특수문자를 최소 1글자씩 포함해야합니다. ")
                    .contains("비밀번호는 최소 10자 이상 작성해주세요.");
        }

        @DisplayName("SignupRequestDto 생성 실패 : 이메일 오류")
        @Test
        void createSignupRequestDtoFailEmail() {
            // given
            SignupRequestDto signupRequestDto = new SignupRequestDto();
            signupRequestDto.setUsername("dlskdud123");
            signupRequestDto.setPassword("dlskdud12!");
            signupRequestDto.setName("이나영");
            signupRequestDto.setEmail("dlskdud-email"); // 잘못된 이메일

            // when
            Set<ConstraintViolation<SignupRequestDto>> violations = validator.validate(signupRequestDto);

            // then
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting("message").contains("올바른 형식의 이메일 주소여야 합니다");

        }

        @DisplayName("SignupRequestDto 생성 실패 : name 오류")
        @Test
        void createSignupRequestDtoFailName() {
            // given
            SignupRequestDto signupRequestDto = new SignupRequestDto();
            signupRequestDto.setUsername("dlskdud123");
            signupRequestDto.setPassword("dlskdud12!");
            signupRequestDto.setName(" ");
            signupRequestDto.setEmail("dlskdud@test.com");

            // when
            Set<ConstraintViolation<SignupRequestDto>> violations = validator.validate(signupRequestDto);

            // then
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting("message").contains("이름은 공백일 수 없습니다.");
        }
    }
}
