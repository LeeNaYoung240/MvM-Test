package com.sparta.mvm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CommentTest {

    private Comment comment;
    private Post post;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User("dlskdud12", "dlskdud12@!", "이나영", "dlskdud@test.com", "테스트", UserStatusEnum.USER_NORMAL);
        post = new Post("포스트 테스트");
        post.setUser(user);
        comment = new Comment("댓글 테스트", post);
    }

    @DisplayName("Comment Entity 생성 테스트")
    @Test
    void createCommentEntity() {
        // given
        String comments = "댓글 내용 테스트";

        // when
        Comment newComment = new Comment(comments, post);
        newComment.setUser(user);

        // then
        assertThat(newComment).isNotNull();
        assertThat(newComment.getComments()).isEqualTo(comments);
        assertThat(newComment.getPost()).isEqualTo(post);
        assertThat(newComment.getUser()).isEqualTo(user);
    }

    @DisplayName("Comment Entity 수정 테스트")
    @Test
    void updateCommentEntity() {
        // given
        String updatedComments = "수정된 댓글 내용 테스트";

        // when
        comment.update(updatedComments);

        // then
        assertThat(comment.getComments()).isEqualTo(updatedComments);
    }

    @DisplayName("Comment Entity 사용자 설정 테스트")
    @Test
    void setUserForCommentEntity() {
        // given
        User newUser = new User("newuser", "newpassword", "테스트", "newuser@test.com", "테스트", UserStatusEnum.USER_NORMAL);

        // when
        comment.setUser(newUser);

        // then
        assertThat(comment.getUser()).isEqualTo(newUser);
    }

    @DisplayName("Comment Entity 포스트 설정 테스트")
    @Test
    void setPostForCommentEntity() {
        // given
        Post newPost = new Post("새로운 포스트");

        // when
        comment.setPost(newPost);

        // then
        assertThat(comment.getPost()).usingRecursiveComparison().isEqualTo(newPost);
    }


    @Nested
    @DisplayName("Comment Entity 설정 예외 테스트")
    class CreateInvalidCommentEntity{
        @DisplayName("Comment Entity 사용자 설정 예외 테스트")
        @Test
        void setUserException() {
            // given
            Comment newComment = new Comment("댓글 테스트", post);

            // when - then
            assertThatThrownBy(() -> newComment.setUser(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("사용자 정보는 필수입니다.");
        }

        @DisplayName("Comment Entity 포스트 설정 예외 테스트")
        @Test
        void setPostException() {
            // given
            Comment newComment = new Comment("댓글 테스트", post);

            // when - then
            assertThatThrownBy(() -> newComment.setPost(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("포스트 정보는 필수입니다.");
        }
    }
}
