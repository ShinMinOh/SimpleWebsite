package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderItemQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.*;


/**
 * 주문내역에서 추가로 주문한 상품 정보를 추가로 조회
 * Order 기준으로 컬렉션인 OrderItem 와 Item 이 필요
 * 컬렉션인 일대다 관계 (OneToMany)를 조회하고, 최적화
 * */
@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;


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

     @GetMapping("/api/v2/orders")
     public List<OrderDto> ordersV2(){
         List<Order> orders = orderRepository.findAllByString(new OrderSearch());
         List<OrderDto> result = orders.stream()  //orders -> OrderDto 로 변환
                 .map(o -> new OrderDto(o))
                 .collect(toList());

         return result;
     }

    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithItem();

        for(Order order : orders) {
            System.out.println("orders ref = " + order + "orders id = " + order.getId());
        }
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());

        return result;
    }

     @GetMapping("/api/v3.1/orders")
     public List<OrderDto> ordersV3_page(
             @RequestParam(value = "offset", defaultValue = "0") int offset, //몇번째부터 시작할건지
             @RequestParam(value = "limit", defaultValue = "100") int limit)  //개수제한 몇개로할건지
     {
         List<Order> orders = orderRepository.findAllWithMemberDelivery(offset,limit);

         List<OrderDto> result = orders.stream()
                 .map(o -> new OrderDto(o))
                 .collect(toList());

         return result;
     }


    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4(){
        return orderQueryRepository.findOrderQueryDtos();

    }

    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5(){
        return orderQueryRepository.findAllByDto_optimization();
    }

    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> ordersV6() {
        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();
        return flats.stream()
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(),
                                o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(),
                                o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(),
                        e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(),
                        e.getKey().getAddress(), e.getValue()))
                .collect(toList());
    }

     @Getter
     static class OrderDto{
            // Dto에는 아래 6가지 정보를 노출시킬 예정.
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;                //Address 같은 value object는 엔티티 노출시켜도 상관X.바뀔 일 없고 쭉 써야되는 값
        //private List<OrderItem> orderItems;  //Dto 안에 엔티티(OrderItem)이 있으면 안됌.노출위험성,API 스펙 변경 가능성. 이 조차도 Dto로 다 변환해줘야함.
        private List<OrderItemDto> orderItems;  //외부에 OrderDto 안에 OrderItemDto로 랩핑해서 나가게 됨.


         public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(toList());

            /* order.getOrderItems().stream().forEach(o -> o.getItem().getName()); //orderItems는 엔티티이므로 프록시 강제 초기화가 필요. 없을경우 null로 나옴.
            orderItems = order.getOrderItems();
            */
         }
     }

     @Getter
     static class OrderItemDto{
            //API 스펙상 상품명,주문한가격,카운트 3개만 필요하다.
        private String itemName;  //상품 명
        private int orderPrice;  //주문 가격
        private int count;  //주문 수량

        public OrderItemDto(OrderItem orderItem){
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();

        }
     }

}
