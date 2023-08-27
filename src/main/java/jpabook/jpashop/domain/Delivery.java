package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Delivery {

    @Id @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @JsonIgnore
    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;  //READY, COMP

    /* enumtype에는 ORDINAL과 STRING이 있는데 ORDINAL일 경우 숫자로 메핑되서 들어감.
    그래서 중간에 READY, XXX, COMP와 같이 중간에 하나의 상태가 추가될경우 메핑된 숫자가 밀려서 결과에 큰 지장을 줌.
    따라서 STRING타입으로 사용하는 것이 안전. (디폴트 - ORDINAL)
     */


}
