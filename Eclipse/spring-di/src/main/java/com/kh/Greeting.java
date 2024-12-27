package com.kh;

public class Greeting {
	private long id;
	private String content;
	
	public Greeting(long id, String content) {
		super();
		this.id = id;
		this.content = content;
		
		System.out.println("Greeting 생성");
	}

	@Override
	public String toString() {
		return "Greeting [id=" + id + ", content=" + content + "]";
	}
	
}
