package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * xToOne(ManyToOne, OneToOne)에서 성능 최적화를 어떻게 할것인지에 대한것.
 * 주문+배송정보+회원 조회하는 API
 * Order
 * Order -> Member : Order와 Member는 ManyToOne
 * Order -> Delivery : Order 와 Delivery는 OneToOne
 * 이렇게만 연관을 걸리게 만들 예정*/
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;
    /**
     * V1. 엔티티 직접 노출(fetch join 사용X)
     * 양방향 관계 문제 발생 -> @JsonIgnore
     * */
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

    /**
     * V2. 엔티티를 조회해서 DTO로 변환(fetch join 사용X)
     * 단점 : 지연로딩으로 쿼리 N번 호출
     * */
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2(){
        List<Order> orders = orderRepository.findAllByString(new OrderSearch()); //이대로 반환하면 안됨. SimpleOrderDto타입으로 바꿔야함.
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());


        return result;
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address; //배송지정보

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); //LAZY 초기화 : 영속성컨텍스트가 memberid를 가지고 찾고 없으면 db에 쿼리날림.
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); //LAZY 초기화
        }
    }

    /**
     * V3. 엔티티를 조회해서 DTO로 변환(fetch join 사용 O)
     * * - fetch join으로 쿼리 1번 호출
     * V2 와 V3는 비슷하지만 나가는 쿼리수가 엄청 차이남.
     * */
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return result;
    }
     /**
      * V4. JPA에서 DTO로 바로 조회
      * - 쿼리 1번 호출
      * - select 절에서 원하는 데이터만 선택해서 조회
      */
        @GetMapping("/api/v4/simple-orders")
        public List<OrderSimpleQueryDto> ordersV4(){
            return orderSimpleQueryRepository.findOrderDtos();
        }
}
