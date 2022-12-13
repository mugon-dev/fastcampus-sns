package com.fastcampus.sns.controller.response;

import com.fastcampus.sns.model.Comment;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CommentResponse {

    private Integer id;
    private String comment;
    private String userName;
    private Integer postId;
    private Timestamp registeredAt;
    private Timestamp updatedAt;

    public static CommentResponse fromComment(Comment comment) {
        return new CommentResponse(
            comment.getId(),
            comment.getComment(),
            comment.getUserName(),
            comment.getPostId(),
            comment.getRegisteredAt(),
            comment.getUpdatedAt()
        );
    }
}
