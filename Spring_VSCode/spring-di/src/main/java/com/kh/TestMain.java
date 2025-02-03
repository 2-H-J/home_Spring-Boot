package com.kh;

import java.util.Objects;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TestMain {

	public static void main(String[] args) {
//		Greeting g1 = new Greeting(1, "greentign1");
//		Greeting g2 = new Greeting(2, "greentign2");
//		Greeting g3 = new Greeting(3, "greentign3");
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(AppContext.class);
		
		Greeting g1 = ctx.getBean("greeter",Greeting.class);
		System.out.println(g1.toString());
		
		Greeting g2 = ctx.getBean("greeter",Greeting.class);
		System.out.println(g2.toString());

		// g1과 g2가 정말 같은 객체인지 한번 증명 : 몇개를 만들어도 단 하나
		System.out.println(g1 == g2);
		if(g1 == g2) {
			System.out.println("같음");
		}
		
		System.out.println(g1.equals(g2));
		
		System.out.println(Objects.equals(g1,g2));
		
		Person p1 = ctx.getBean("person",Person.class);
		System.out.println(p1);
		
		Person p2 = (Person) ctx.getBean("person");
		System.out.println(p2);
		
		System.out.println(p1 == p2);
		
		// 다를 경우 에러
//		Person p3 = ctx.getBean("greeter",Person.class);
//		System.out.println(p3);
	}
}
