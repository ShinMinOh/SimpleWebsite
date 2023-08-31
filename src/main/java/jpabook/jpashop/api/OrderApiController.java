package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
/**
 * 주문내역에서 추가로 주문한 상품 정보를 추가로 조회
 * Order 기준으로 컬렉션인 OrderItem 와 Item 이 필요
 * 컬렉션인 일대다 관계 (OneToMany)를 조회하고, 최적화
 * */
@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for(Order order : all){
            order.getMember().getName();  //Lazy 강제 초기화
            order.getDelivery().getAddress();  //Lazy 강제 초기화

            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());  //Lazy 강제 초기화. 아이템 명을 갖고와야 해서 Item도 초기화(getItem()).
                                                                    // Order 엔티티에는 상품명이 없으므로 List인 orderItems를 참조해서 그 안에서 가져와야 한다.
        }
        return all;
    }
}
