package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable  //jpa의 내장타입이기 때문에. 어딘가에 내장이 될 수 있다는 뜻.
@Getter      //외부에서 필드값만 읽을 수만 있고, 변경하지 못하도록하기 위해(읽기전용) Setter 선언안함.
public class Address {

    private String city;
    private String street;
    private String zipcode;

    protected Address() {

    }//기본생성자가 있어야함. JPA스펙상 그냥 써주는 코드.

    public Address(String city, String street, String zipcode) {  //생성자로 처음 생성될때만 값을 입력할수 있게 하고 그 이후로는 수정불가.
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
