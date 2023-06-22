package jpabook.jpashop.service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count){

        //엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        //배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        //주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        //주문 생성
        Order order = Order.createOreder(member, delivery, orderItem);

        //주문 저장
        orderRepository.save(order);  //그래서 이렇게 order하나만 persist해도 orderItem이랑 delivery가 persist됨.
         /*원래는 delivery와 orderItem 등 save로 jpa에 넣어준 다음에 위와 같이 세팅을 해야하는데,
        Order 클래스의 Cascade옵션(Order 클래스를 persist하면 컬렉션에 들어와있는 OrderItem의 List들도 강제로 persist를 날려줌.)
        delivery도 마찬가지로 cascade가 걸려있음. Order가 persist될때, delivery 엔티티도 persist 됨.

        OrderItem 클래스나 delivery 클래스 : 두 클래스가 다른걸 참조할 순 있으나 두 클래스 모두 Order만 참조해서 씀.
        또한 다른 클래스가 참조할 수 없는 private한 클래스의 오너일경우 cascade를 써서 도움을 받을 수 있다.
        하지만 다른 클래스에서 앞에 두 클래스를 참조하거나 갔다 쓴다면 함부로 cascade를 써서는 안된다.!
        */


        return order.getId();   //order의 식별자값인 id만 반환
    }

    /**
     * 주문  취소
     */
    @Transactional
    public void cancelOrder(Long orderId){
        //주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);
        //주문 취소
        order.cancel();
    }

    /**
     * 검색
     */
    public List<Order> findOrders(OrderSearch orderSearch){
       return orderRepository.findAllByString(orderSearch);
    }
}
