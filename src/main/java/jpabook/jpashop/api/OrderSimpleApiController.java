package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * xToOne(ManyToOne, OneToOne)에서 성능 최적화를 어떻게 할것인지에 대한것.
 * Order
 * Order -> Member : Order와 Member는 ManyToOne
 * Order -> Delivery : Order 와 Delivery는 OneToOne
 * 이렇게만 연관을 걸리게 만들 예정*/
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for(Order order : all){//order.getMember()까지는 Proxy 객체라 DB에 접근하지 않지만(DB에 쿼리 안날라감)
                                // .getName()까지 하면 실제 name을 끌고 와야되기 때문에 Lazy 강제 초기호됨.(Member에 쿼리 날려서 DB에 접근)
            order.getMember().getName(); //Lazy 강제 초기화
            order.getDelivery().getAddress(); //Lazy 강제 초기화.

            //이렇게 하면 결론적으로 member, delivery만 출력되고 orderItems는 null로 출력시키지 않을 수 있음.
        }

        return all;
    }
}
