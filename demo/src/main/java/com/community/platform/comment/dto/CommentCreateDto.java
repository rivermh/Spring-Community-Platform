package com.community.platform.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentCreateDto {

	@NotBlank
	private String content;
}
