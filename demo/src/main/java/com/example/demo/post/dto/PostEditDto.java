package com.example.demo.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostEditDto {

	// 이 요소들은 최소단위기 때문에 분리
	private Long id;
	private String title;
	private String content;
}
