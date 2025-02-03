package com.kh;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 설정에 관련된 클래스라 어노테이션(Annotation)으로 지정
@Configuration
public class AppContext {

	@Bean // 리턴메소드
	public Greeting greeter() {
		Greeting g = new Greeting(1000,"홍길동");
		return g;
	}
	
	@Bean
	public Person person() {
		return new Person("김철수",20);
	}
}
