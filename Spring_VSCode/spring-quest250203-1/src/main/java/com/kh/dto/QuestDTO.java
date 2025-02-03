package com.kh.dto;

import org.apache.ibatis.type.Alias;

@Alias("quest")
public class QuestDTO {
	private String searchText;
	private String searchDate;
    
	public QuestDTO() {	}

	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	public String getSearchDate() {
		return searchDate;
	}

	public void setSearchDate(String searchDate) {
		this.searchDate = searchDate;
	}

	@Override
	public String toString() {
		return "QuestDTO [searchText=" + searchText + ", searchDate=" + searchDate + "]";
	}

	
}
