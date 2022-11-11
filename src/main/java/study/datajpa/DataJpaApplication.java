package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;
import java.util.UUID;

@SpringBootApplication
@EnableJpaAuditing // auditing -> 스프링부트 설정 클래스에 적용 (spring data JPA)
//@EnableJpaRepositories(basePackages = "study.datajpa.repository") // springboot는 생략 가능
public class DataJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataJpaApplication.class, args);
	}

	private static Optional<String> getCurrentAuditor() {
		return Optional.of(UUID.randomUUID().toString());
	}

	/**
	 * 인터페이스에서 메소드가 하나면 람다식으로 변경가능
	 * 실제 애플리케이션에서는 로그인 ID
	 * @CreatedBy, @LastModifiedBy에서 등록되거나 수정 될때마다 auditorProvider를 호출해서 로그인 정보를 가지고 온다
	 */

	@Bean
	public AuditorAware<String> auditorProvider() {
		return DataJpaApplication::getCurrentAuditor;
	}

	// 람다식
//	@Bean
//	public AuditorAware<String> auditorProvider() {
//		return () -> Optional.of(UUID.randomUUID().toString());
//	}

}
