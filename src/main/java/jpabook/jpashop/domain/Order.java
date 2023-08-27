package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  //직접 set으로 변수 세팅하지 않고 createOreder 로 set을 쓰기 위해서
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id") //DB 컬럼네임을 테이블명_id 로 지정.
    private Long id;

    @ManyToOne (fetch = FetchType.LAZY) // order랑 member는 다대일 관계
    @JoinColumn(name = "member_id")  //메핑을 member_id로 함. 즉 FK(외래키)의 네임이 member_id가 됨. 주문한 회원에 대한 정보 메핑.
    private Member member;

    /*MEMBER_ID(FK)가 Member테이블의 orders랑 Order테이블의 member중 Order에 있는 member가 더 가까우므로 이것을 연관관계의 주인으로 지정.
      그러므로 위에 member를 연관관계의 주인으로 하기. @ManyToOne그냥 써주면 됨.
      Member에 값을 세팅 하면 member_id(외래키) 값이 변경이 됨.
    */

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id") //일대일 관계에서는 외래키를 어느쪽에 둬도 상관 없지만 주로 접근이 많은쪽에 외래키 설정을 함.order테이블에서 delivery로 접근하는 경우가 더 많으므로 외래키 설정을 여기로함.
    private Delivery delivery;

    private LocalDateTime orderDate; //주문시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; //enum타입. 주문상태 [ORDER, CANCEL]


    //==연관관계 메서드==// 연관관계 메서드가 있는 위치는 둘 중에 핵심적으로 컨트롤 하는 쪽이 들고 있는 것이 좋다.
    //Order 테이블은 Member, OrderItem, Delivery 세개의 테이블과 양방향 연관관계이므로 편의를 위해 이를 다 표현 해 주기.
    //양방향 연관관계에서는 아래와 같은 연관관계 편의메소드가 있는 것이 좋다.

    public void setMember(Member member){//member를 세팅할 때,
        this.member = member;               //member값을 받아서
        member.getOrders().add(this);       //Member 엔티티의 orders 리스트 get으로 가져오기
    }

    public void addOrderItem(OrderItem orderItem){  //order랑 Item이랑 양방향 연관관계
        orderItems.add(orderItem);  //Order에 있는 OrderItems에  orderItem을 넣어주고
        orderItem.setOrder(this);   //반대로 orderItem 즉 넘어 온 애에다가 this를 넣어주기. OrderItem 엔티티의 order에다 set으로 넣기
    }

    public void  setDelivery(Delivery delivery){
        this.delivery = delivery;
        delivery.setOrder(this);    //Delivery 엔티티의 order에다 set으로 넣기.
    }

    //==생성 메서드==//
    public static Order createOreder(Member member, Delivery delivery, OrderItem... orderItems){ //Orderzitem...: 여러개를 넘길 수 있음.
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);

        List<OrderItem> temp = new ArrayList<>();

        for(OrderItem orderItem : orderItems){
            order.addOrderItem(orderItem);

        }

        order.setStatus(OrderStatus.ORDER);         //상태세팅
        order.setOrderDate(LocalDateTime.now());    //현재시간으로 주문시간정보세팅
        return order;

    }

    //==비즈니스 로직==//

    /**
     * 주문 취소
     */
    public void cancel(){
        if(delivery.getStatus() == DeliveryStatus.COMP){
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }

        this.setStatus(OrderStatus.CANCEL);
        for(OrderItem orderItem : orderItems){
            orderItem.cancel();  //주문한 상품들 루프를 돌면서 각자 취소된 재고 원복
        }
    }

    //==조회 로직==//
    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice(){
        int totalPrice = 0;
        for(OrderItem orderItem : orderItems){
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

}

