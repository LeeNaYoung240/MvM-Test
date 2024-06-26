package com.sparta.mvm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.mvm.config.SecurityConfig;
import com.sparta.mvm.dto.PostRequestDto;
import com.sparta.mvm.dto.PostResponseDto;
import com.sparta.mvm.entity.User;
import com.sparta.mvm.exception.CustomException;
import com.sparta.mvm.exception.ErrorEnum;
import com.sparta.mvm.mvc.MockSpringSecurityFilter;
import com.sparta.mvm.security.UserDetailsImpl;
import com.sparta.mvm.service.PostService;
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
        controllers = PostController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfig.class
                )
        }
)
@WithMockUser
@MockBean(JpaMetamodelMappingContext.class)
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private Principal mockPrincipal;

    @MockBean
    private PostService postService;

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
    @DisplayName("게시글 등록 테스트")
    void createPost() throws Exception {
        // given
        mockUserSetup();

        PostRequestDto requestDto = new PostRequestDto("내용 테스트");
        PostResponseDto responseDto = PostResponseDto.builder()
                .msg("게시글 등록 성공 🎉")
                .statusCode(200)
                .id(1L)
                .username("user1")
                .contents("내용 테스트")
                .build();

        given(postService.save(ArgumentMatchers.any(PostRequestDto.class)))
                .willReturn(responseDto);

        // when
        MvcResult result = mockMvc.perform(post("/posts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.msg", is("게시글 등록 성공 🎉")))
                .andExpect(jsonPath("$.statusCode", is(200)))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("user1")))
                .andExpect(jsonPath("$.contents", is("내용 테스트")))
                .andReturn();

        // then
        String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("Response: " + content);
    }

    @Test
    @DisplayName("게시글 전체 조회 테스트")
    void getAllPosts() throws Exception {
        // Given
        mockUserSetup();
        PostResponseDto responseDto = PostResponseDto.builder()
                .id(1L)
                .contents("내용 테스트")
                .username("user1")
                .build();
        List<PostResponseDto> responseDtoList = Collections.singletonList(responseDto);
        given(postService.getAll()).willReturn(responseDtoList);

        // When
        MvcResult result = mockMvc.perform(get("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.newsFeed[0].id", is(1)))
                .andExpect(jsonPath("$.newsFeed[0].contents", is("내용 테스트")))
                .andExpect(jsonPath("$.newsFeed[0].username", is("user1")))
                .andReturn();

        // Then
        String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("Response: " + content);
    }


    @Test
    @DisplayName("게시글 부분 조회 테스트")
    void getPostById() throws Exception {
        // Given
        mockUserSetup();
        long postId = 1L;
        PostRequestDto requestDto = new PostRequestDto("내용 테스트");
        PostResponseDto responseDto = PostResponseDto.builder()
                .id(postId)
                .contents("내용 테스트")
                .username("user1")
                .msg("게시글 조회 성공 🎉")
                .statusCode(200)
                .build();

        given(postService.findById(postId)).willReturn(responseDto);

        // When
        MvcResult result = mockMvc.perform(get("/posts/{postId}", postId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.contents", is("내용 테스트")))
                .andExpect(jsonPath("$.username", is("user1")))
                .andExpect(jsonPath("$.msg", is("게시글 조회 성공 🎉")))
                .andReturn();

        // Then
        String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("Response: " + content);
    }

    @Test
    @DisplayName("게시글 수정 테스트")
    void updatePost() throws Exception {
        // Given
        mockUserSetup();
        long postId = 1L;
        PostRequestDto requestDto = new PostRequestDto("내용 테스트");
        PostResponseDto responseDto = PostResponseDto.builder()
                .id(postId)
                .contents("내용 테스트")
                .username("user1")
                .msg("게시글 수정 성공 🎉")
                .statusCode(200)
                .build();

        given(postService.update(eq(postId), any(PostRequestDto.class))).willReturn(responseDto);

        // When
        MvcResult result = mockMvc.perform(put("/posts/{postId}", postId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.contents", is("내용 테스트")))
                .andExpect(jsonPath("$.username", is("user1")))
                .andExpect(jsonPath("$.msg", is("게시글 수정 성공 🎉")))
                .andReturn();

        // Then
        String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("Response: " + content);
    }

    @Test
    @DisplayName("게시글 삭제 테스트")
    void deletePost() throws Exception {
        // Given
        mockUserSetup();
        long postId = 1L;

        // When
        MvcResult result =  mockMvc.perform(delete("/posts/{postId}", postId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode", is(200)))
                .andExpect(jsonPath("$.msg", containsString("게시글 삭제 성공 🎉")))
                .andReturn();

        // Then
        verify(postService, times(1)).delete(postId);
        String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("Response: " + content);

    }

    @Test
    @DisplayName("게시글 조회 실패 테스트")
    void getPostByIdFail() throws Exception {
        // Given
        mockUserSetup();
        given(postService.findById(1L)).willThrow(new CustomException(ErrorEnum.BAD_POSTID));

        // When
        MvcResult result = mockMvc.perform(get("/posts/{postId}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(ErrorEnum.BAD_POSTID.getStatusCode()))
                .andExpect(jsonPath("$.msg").value(ErrorEnum.BAD_POSTID.getMsg()))
                .andReturn();

        // Then
        String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("Response: " + content);
    }

    @Test
    @DisplayName("게시글 수정 실패 테스트")
    void updatePostFail() throws Exception {
        // Given
        mockUserSetup();
        PostRequestDto requestDto = new PostRequestDto("내용 테스트");
        given(postService.update(eq(1L), any(PostRequestDto.class))).willThrow(new CustomException(ErrorEnum.BAD_AUTH_PUT));

        // When
        MvcResult result = mockMvc.perform(put("/posts/{postId}", 1L)
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
    @DisplayName("게시글 삭제 실패 테스트")
    void deletePostFail() throws Exception {
        // Given
        mockUserSetup();
        doThrow(new CustomException(ErrorEnum.BAD_AUTH_DELETE)).when(postService).delete(1L);

        // When
        MvcResult result = mockMvc.perform(delete("/posts/{postId}", 1L)
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