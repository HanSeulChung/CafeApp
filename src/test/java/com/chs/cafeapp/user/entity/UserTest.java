package com.chs.cafeapp.user.entity;

import com.chs.cafeapp.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:/application-test.yml")
public class UserTest {

    @Autowired
    UserRepository userRepository;

    @Test
    public void setUserRepositoryTest() {
        User user = User.builder()
                .userId("test")
                .password("test")
                .userName("test")
                .nickName("test")
                .grade("test")
                .build();

        userRepository.save(user);
    }

}