package com.kh;

public class RegisterDTO {
	private String memberId;
	private String name;
	private String passwd;
	private int age;
	
	public RegisterDTO() {	}

	public RegisterDTO(String memberId, String name, String passwd, int age) {
		super();
		this.memberId = memberId;
		this.name = name;
		this.passwd = passwd;
		this.age = age;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return "RegisterDTO [memberId=" + memberId + ", name=" + name + ", passwd=" + passwd + ", age=" + age + "]";
	}
	
	
	
}
