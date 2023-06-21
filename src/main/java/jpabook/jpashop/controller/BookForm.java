package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BookForm {
    private Long id; //상품 수정이 있어서 id값이 있어야함.

    private String name;
    private int price;
    private int stockQuantity;

    private String author;
    private String isbn;
}
