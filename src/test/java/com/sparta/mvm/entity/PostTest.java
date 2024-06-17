package com.sparta.mvm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PostTest {
    private Post post;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User("dlskdud12", "dlskdud12@!", "이나영", "dlskdud@test.com", "테스트", UserStatusEnum.USER_NORMAL);
        post = new Post("포스트 테스트");
        post.setUser(user);
    }

    @DisplayName("Post Entity 생성 테스트")
    @Test
    void createPostEntity() {
        // given
        String contents = "포스트 내용 테스트";

        // when
        Post newPost = new Post(contents);
        newPost.setUser(user);

        // then
        assertThat(newPost).isNotNull();
        assertThat(newPost.getContents()).isEqualTo(contents);
        assertThat(newPost.getUser()).isEqualTo(user);
    }

    @DisplayName("Post Entity 수정 테스트")
    @Test
    void updatePostEntity() {
        // given
        String updatedContents = "수정된 포스트 내용 테스트";

        // when
        post.update(updatedContents);

        // then
        assertThat(post.getContents()).isEqualTo(updatedContents);
    }

    @DisplayName("Post Entity 사용자 설정 테스트")
    @Test
    void setUserForPostEntity() {
        // given
        User newUser = new User("newuser", "newpassword", "테스트", "newuser@test.com", "테스트", UserStatusEnum.USER_NORMAL);

        // when
        post.setUser(newUser);

        // then
        assertThat(post.getUser()).isEqualTo(newUser);
    }


    @DisplayName("Post Entity 사용자 설정 예외 테스트")
    @Test
    void setUserException() {
        // given
        Post newPost = new Post("포스트 테스트");

        // when - then
        assertThatThrownBy(() -> newPost.setUser(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용자 정보는 필수입니다.");
    }

}
