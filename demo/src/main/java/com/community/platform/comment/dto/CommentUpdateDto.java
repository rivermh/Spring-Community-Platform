package com.community.platform.comment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentUpdateDto {

	private String content;
	
	// 수정은 content만 필요
}
