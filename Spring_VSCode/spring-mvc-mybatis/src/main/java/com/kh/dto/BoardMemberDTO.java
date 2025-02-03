package com.kh.dto;

import org.apache.ibatis.type.Alias;

@Alias("member")
public class BoardMemberDTO {
	private String id;
	private String password;
	private String userName;
	private String nickName;

	public BoardMemberDTO() {	}

	public BoardMemberDTO(String id, String password, String userName, String nickName) {
		this.id = id;
		this.password = password;
		this.userName = userName;
		this.nickName = nickName;
	}



	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	@Override
	public String toString() {
		return "BoardMemberDTO [id=" + id + ", password=" + password + ", userName=" + userName + ", nickName="
				+ nickName + "]";
	}
	
	
	
	
}
