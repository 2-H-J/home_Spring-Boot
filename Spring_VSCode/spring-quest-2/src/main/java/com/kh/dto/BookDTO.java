package com.kh.dto;

import org.apache.ibatis.type.Alias;

@Alias("book")
public class BookDTO {
  private String isbn;
  private String title;
  private String author;
  private String pubdate;
  private String publisher;
  private int price;

  public String getIsbn() {
    return isbn;
  }
  public void setIsbn(String isbn) {
    this.isbn = isbn;
  }
  public String getTitle() {
    return title;
  }
  public void setTitle(String title) {
    this.title = title;
  }
  public String getAuthor() {
    return author;
  }
  public void setAuthor(String author) {
    this.author = author;
  }
  public String getPubdate() {
    return pubdate;
  }
  public void setPubdate(String pubdate) {
    this.pubdate = pubdate;
  }
  public String getPublisher() {
    return publisher;
  }
  public void setPublisher(String publisher) {
    this.publisher = publisher;
  }
  public int getPrice() {
    return price;
  }
  public void setPrice(int price) {
    this.price = price;
  }

  
}
