package com.sparta.mvm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.mvm.config.SecurityConfig;
import com.sparta.mvm.dto.CommentRequestDto;
import com.sparta.mvm.dto.CommentResponseDto;
import com.sparta.mvm.entity.User;
import com.sparta.mvm.exception.CustomException;
import com.sparta.mvm.exception.ErrorEnum;
import com.sparta.mvm.mvc.MockSpringSecurityFilter;
import com.sparta.mvm.security.UserDetailsImpl;
import com.sparta.mvm.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = CommentController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfig.class
                )
        }
)
@WithMockUser
@MockBean(JpaMetamodelMappingContext.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private Principal mockPrincipal;

    @MockBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .build();
    }

    private void mockUserSetup() {
        User user = User.builder()
                .username("testUser")
                .password("testPassword")
                .name("Test")
                .lineIntro("TestUser")
                .email("test@example.com")
                .build();
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        mockPrincipal = new UsernamePasswordAuthenticationToken(userDetails, user.getPassword(), userDetails.getAuthorities());
    }

    @Test
    @DisplayName("댓글 등록 테스트")
    void createComment() throws Exception {
        // given
        mockUserSetup();
        long postId = 1L;

        CommentRequestDto requestDto = new CommentRequestDto("댓글 테스트");
        CommentResponseDto responseDto = CommentResponseDto.builder()
                .msg("댓글 등록 성공 💌")
                .statusCode(200)
                .id(1L)
                .comments("댓글 테스트")
                .build();

        given(commentService.save(ArgumentMatchers.eq(postId), ArgumentMatchers.any(CommentRequestDto.class)))
                .willReturn(responseDto);

        // when
        MvcResult result = mockMvc.perform(post("/posts/{postId}/comments", postId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.msg", is("댓글 등록 성공 💌")))
                .andExpect(jsonPath("$.statusCode", is(200)))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.comments", is("댓글 테스트")))
                .andReturn();

        // then
        String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("Response: " + content);
    }

    @Test
    @DisplayName("댓글 조회 테스트")
    void getAllComments() throws Exception {
        // Given
        mockUserSetup();
        CommentResponseDto responseDto = CommentResponseDto.builder()
                .msg("댓글 조회 성공 🎉")
                .statusCode(200)
                .id(1L)
                .comments("댓글 테스트")
                .build();
        List<CommentResponseDto> responseDtoList = Collections.singletonList(responseDto);
        given(commentService.getAll()).willReturn(responseDtoList);

        // When
        MvcResult result = mockMvc.perform(get("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.newFeed_Comment[0].id", is(1)))
                .andExpect(jsonPath("$.newFeed_Comment[0].comments", is("댓글 테스트")))
                .andExpect(jsonPath("$.newFeed_Comment[0].statusCode", is(200)))
                .andExpect(jsonPath("$.newFeed_Comment[0].msg", is("댓글 조회 성공 🎉")))
                .andReturn();

        // Then
        String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("Response: " + content);
    }


    @Test
    @DisplayName("댓글 수정 테스트")
    void updateComment() throws Exception {
        // Given
        mockUserSetup();
        long commentId = 1L;
        CommentRequestDto requestDto = new CommentRequestDto("댓글 테스트");
        CommentResponseDto responseDto = CommentResponseDto.builder()
                .msg("댓글 수정 성공 🎉")
                .statusCode(200)
                .id(1L)
                .comments("댓글 테스트")
                .build();

        given(commentService.update(ArgumentMatchers.eq(commentId), ArgumentMatchers.any(CommentRequestDto.class))).willReturn(responseDto);

        // When
        MvcResult result = mockMvc.perform(put("/comments/{commentId}", commentId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.comments", is("댓글 테스트")))
                .andExpect(jsonPath("$.msg", is("댓글 수정 성공 🎉")))
                .andExpect(jsonPath("$.statusCode", is(200)))
                .andReturn();

        // Then
        String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("Response: " + content);
    }

    @Test
    @DisplayName("댓글 삭제 테스트")
    void deleteComment() throws Exception {
        // Given
        mockUserSetup();
        long commentId = 1L;

        // When
        MvcResult result = mockMvc.perform(delete("/comments/{commentId}", commentId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode", is(200)))
                .andExpect(jsonPath("$.msg", containsString("댓글 삭제 성공 🎉")))
                .andReturn();

        // Then
        verify(commentService, times(1)).delete(commentId);
        String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("Response: " + content);
    }


    @Test
    @DisplayName("댓글 조회 실패 테스트")
    void getAllCommentsFail() throws Exception {
        // Given
        mockUserSetup();
        given(commentService.getAll()).willReturn(Collections.emptyList());

        // When
        MvcResult result = mockMvc.perform(get("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode", is(200)))
                .andExpect(jsonPath("$.msg", is("먼저 댓글을 작성해 보세요 📝")))
                .andReturn();

        // Then
        String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("Response: " + content);
    }


    @Test
    @DisplayName("댓글 수정 실패 테스트")
    void updateCommentFail() throws Exception {
        // Given
        mockUserSetup();
        long commentId = 1L;
        CommentRequestDto requestDto = new CommentRequestDto("내용 테스트");
        given(commentService.update(eq(commentId), any(CommentRequestDto.class))).willThrow(new CustomException(ErrorEnum.BAD_AUTH_PUT));

        // When
        MvcResult result = mockMvc.perform(put("/comments/{commentId}", commentId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.statusCode", is(ErrorEnum.BAD_AUTH_PUT.getStatusCode())))
                .andExpect(jsonPath("$.msg", is(ErrorEnum.BAD_AUTH_PUT.getMsg())))
                .andReturn();

        // Then
        String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("Response: " + content);
    }

    @Test
    @DisplayName("댓글 삭제 실패 테스트")
    void deleteCommentFail() throws Exception {
        // Given
        mockUserSetup();
        long commentId = 1L;
        doThrow(new CustomException(ErrorEnum.BAD_AUTH_DELETE)).when(commentService).delete(commentId);

        // When
        MvcResult result = mockMvc.perform(delete("/comments/{commentId}", commentId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.statusCode", is(ErrorEnum.BAD_AUTH_DELETE.getStatusCode())))
                .andExpect(jsonPath("$.msg", is(ErrorEnum.BAD_AUTH_DELETE.getMsg())))
                .andReturn();

        // Then
        String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("Response: " + content);
    }


}