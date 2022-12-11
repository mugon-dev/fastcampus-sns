package com.fastcampus.sns.controller.response;

import com.fastcampus.sns.model.Post;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PostResponse {

    private Integer id;
    private String title;
    private String body;
    private UserResponse user;
    private Timestamp registeredAt;
    private Timestamp updatedAt;

    public static PostResponse fromPost(Post post) {
        return new PostResponse(
            post.getId(),
            post.getTitle(),
            post.getBody(),
            UserResponse.fromUser(post.getUser()),
            post.getRegisteredAt(),
            post.getUpdatedAt()
        );
    }
}
