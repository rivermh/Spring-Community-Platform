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
@org.springframework.context.annotation.Profile("dev") // 'dev' 모드일 때만 작동!
public class DataInit implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @org.springframework.transaction.annotation.Transactional // 트랜잭션 보장
    public void run(String... args) {
        if (userRepository.count() > 10) return;

        String encodedPassword = passwordEncoder.encode("password");
        List<User> users = new ArrayList<>();

        // 1. 유저 생성 (150명)
        for (int i = 1; i <= 150; i++) {
            users.add(User.builder()
                .username("user" + i)
                .password(encodedPassword)
                .email("user" + i + "@test.com")
                .birth(LocalDate.of(1990, 1, 1).plusDays(i % 365)) // 날짜 범위를 안전하게
                .role(Role.USER)
                .status(UserStatus.ACTIVE) // import 확인 필요!
                .build());
        }
        userRepository.saveAll(users);

        // 2. 게시글 생성 (500개)
        List<Post> posts = new ArrayList<>();
        for (int i = 1; i <= 500; i++) {
            // 실제 생성된 users 리스트 크기에 맞춰서 분배
            User author = users.get(i % users.size()); 
            posts.add(Post.create(author, "테스트 제목 " + i, "테스트 내용입니다. " + i));
        }
        postRepository.saveAll(posts);
        
        System.out.println("✅ 더미 데이터 생성 완료! (User: " + users.size() + "건, Post: " + posts.size() + "건)");
    }
}