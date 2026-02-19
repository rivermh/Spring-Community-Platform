package com.community.platform.common;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.community.platform.post.entity.Post;
import com.community.platform.post.repository.PostRepository;
import com.community.platform.user.entity.Role;
import com.community.platform.user.entity.User;
import com.community.platform.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInit implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 이미 데이터가 충분히 있다면 중복 생성을 방지합니다.
        if (userRepository.count() > 10) return;

        String encodedPassword = passwordEncoder.encode("password");
        List<User> users = new ArrayList<>();

        // 1. 유저 100명 생성
        for (int i = 1; i <= 100; i++) {
            users.add(User.builder()
                .username("user" + i)
                .password(encodedPassword)
                .email("user" + i + "@test.com")
                .birth(LocalDate.of(1990, 1, 1).plusDays(i)) // birth 필드 추가
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .build());
        }
        userRepository.saveAll(users);

        // 2. 게시글 500개 생성 (변수 중복 선언 제거 완료!)
        List<Post> posts = new ArrayList<>();
        for (int i = 1; i <= 500; i++) {
            // 위에서 저장된 100명의 유저를 번갈아가며 작성자로 지정
            posts.add(Post.create(users.get(i % 100), "테스트 제목 " + i, "테스트 내용입니다. " + i));
        }
        postRepository.saveAll(posts);
        
        System.out.println("✅ 더미 데이터 생성 완료! (User: 100건, Post: 500건)");
    }
}