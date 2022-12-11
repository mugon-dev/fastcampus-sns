package com.fastcampus.sns.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fastcampus.sns.exception.ErrorCode;
import com.fastcampus.sns.exception.SnsApplicationException;
import com.fastcampus.sns.fixture.PostEntityFixture;
import com.fastcampus.sns.fixture.UserEntityFixture;
import com.fastcampus.sns.model.entity.PostEntity;
import com.fastcampus.sns.model.entity.UserEntity;
import com.fastcampus.sns.repository.PostEntityRepository;
import com.fastcampus.sns.repository.UserEntityRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@SpringBootTest
public class PostServiceTest {

    @Autowired
    private PostService postService;

    @MockBean
    private PostEntityRepository postEntityRepository;

    @MockBean
    private UserEntityRepository userEntityRepository;

    @Test
    void 포스트작성이_성공한경우() {
        String title = "title";
        String body = "body";
        String userName = "userName";

        when(userEntityRepository.findByUserName(userName)).thenReturn(
            Optional.of(mock(UserEntity.class)));
        when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

        assertDoesNotThrow(() -> postService.create(title, body, userName));

    }

    @Test
    void 포스트작성시_요청한유자가_존재하지않는경우() {
        String title = "title";
        String body = "body";
        String userName = "userName";

        when(userEntityRepository.findByUserName(userName)).thenReturn(
            Optional.empty());
        when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

        SnsApplicationException e = assertThrows(
            SnsApplicationException.class, () -> postService.create(title, body, userName));
        assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void 포스트수정이_성공한_경우() {
        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;
        PostEntity entity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = entity.getUser();
        when(userEntityRepository.findByUserName(userName)).thenReturn(
            Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(
            Optional.of(entity));
        when(postEntityRepository.saveAndFlush(any())).thenReturn(entity);

        assertDoesNotThrow(() -> postService.modify(title, body, userName, postId));
    }

    @Test
    void 포스트수정시_포스트가_존재하지_않는경우() {
        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;
        PostEntity entity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = entity.getUser();
        when(userEntityRepository.findByUserName(userName)).thenReturn(
            Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(
            Optional.empty());

        SnsApplicationException e = assertThrows(
            SnsApplicationException.class, () -> postService.modify(title, body, userName, postId));
        assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void 포스트수정시_권한이_없는_경우() {
        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;
        PostEntity entity = PostEntityFixture.get(userName, postId, 1);
        UserEntity writer = UserEntityFixture.get("userName1", "password", 2);
        when(userEntityRepository.findByUserName(userName)).thenReturn(
            Optional.of(writer));
        when(postEntityRepository.findById(postId)).thenReturn(
            Optional.of(entity));

        SnsApplicationException e = assertThrows(
            SnsApplicationException.class, () -> postService.modify(title, body, userName, postId));
        assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());
    }

    @Test
    void 포스트삭제가_성공한_경우() {
        String userName = "userName";
        Integer postId = 1;
        PostEntity entity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = entity.getUser();
        when(userEntityRepository.findByUserName(userName)).thenReturn(
            Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(
            Optional.of(entity));

        assertDoesNotThrow(() -> postService.delete(userName, postId));
    }

    @Test
    void 포스트삭제시_포스트가_존재하지_않는경우() {
        String userName = "userName";
        Integer postId = 1;
        PostEntity entity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = entity.getUser();
        when(userEntityRepository.findByUserName(userName)).thenReturn(
            Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(
            Optional.empty());

        SnsApplicationException e = assertThrows(
            SnsApplicationException.class, () -> postService.delete(userName, postId));
        assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void 포스트삭제시_권한이_없는_경우() {
        String userName = "userName";
        Integer postId = 1;
        PostEntity entity = PostEntityFixture.get(userName, postId, 1);
        UserEntity writer = UserEntityFixture.get("userName1", "password", 2);
        when(userEntityRepository.findByUserName(userName)).thenReturn(
            Optional.of(writer));
        when(postEntityRepository.findById(postId)).thenReturn(
            Optional.of(entity));

        SnsApplicationException e = assertThrows(
            SnsApplicationException.class, () -> postService.delete(userName, postId));
        assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());
    }

    @Test
    void 피드목록요청이_성공한_경우() {
        Pageable pageable = mock(Pageable.class);
        when(postEntityRepository.findAll(pageable)).thenReturn(Page.empty());
        assertDoesNotThrow(() -> postService.list(pageable));
    }

    @Test
    void 내피드목록요청이_성공한_경우() {
        Pageable pageable = mock(Pageable.class);
        UserEntity userEntity = mock(UserEntity.class);
        when(userEntityRepository.findByUserName(any())).thenReturn(
            Optional.of(userEntity));
        when(postEntityRepository.findAllByUser(userEntity, pageable)).thenReturn(Page.empty());
        assertDoesNotThrow(() -> postService.my("", pageable));
    }

}
