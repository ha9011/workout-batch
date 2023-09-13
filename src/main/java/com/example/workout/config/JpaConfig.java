package com.example.workout.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing  // auditing을 감시하다는 뜻으로, 시간, 아이디 등 설정하면 자동으로 데이터 값이 격납
public class JpaConfig {
}
