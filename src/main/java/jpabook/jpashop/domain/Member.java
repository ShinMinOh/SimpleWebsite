package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long Id;

    private String name;

    @Embedded  //내장타입을 포함했다는 어노테이션. Address 클래스의 @Embeddable을 지워도 상관x.둘중 하나만 쓰면 됨. 그냥 2개다 쓰기
    private Address address;

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

    /*하나의 Member가 여러개의 상품을 주문하기 때문에 일대다 관계. 나는 주인이 아닌 연관관계의 거울이라는 뜻으로 mappedBy 붙이기.
      order테이블에 있는 member에 의해서 메핑이 됬다는 뜻. 메핑을 하는 애가 아니고 메핑 된 거울임.
      그러므로 여기에 값을 넣는다고 해서 FK(외래키)의 값이 변경되지 않음.
     */


}
