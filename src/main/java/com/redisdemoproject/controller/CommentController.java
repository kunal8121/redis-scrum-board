package com.redisdemoproject.controller;

import com.redisdemoproject.mapper.CommentMapper;
import com.redisdemoproject.model.response.RestComment;
import com.redisdemoproject.model.spec.CommentSpec;
import com.redisdemoproject.service.CommentService;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Controller("/api/{version}/comment")
@RequiredArgsConstructor
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
public class CommentController {

    private final static String VERSION = "1";
    private final CommentService commentService;

    @Post
    public RestComment create(@Body @Valid CommentSpec commentSpec, Authentication authentication) {
        return CommentMapper.INSTANCE.toRest(commentService.create(commentSpec, authentication));
    }

    @Put("/{id}")
    public RestComment update(@PathVariable UUID id, @Body @Valid CommentSpec commentSpec) {
        return CommentMapper.INSTANCE.toRest(commentService.update(id, commentSpec));
    }
    //add api to delete comment
     @Delete("/{id}")
    public void delete(@PathVariable UUID id) {
         commentService.delete(id);
     }
}
