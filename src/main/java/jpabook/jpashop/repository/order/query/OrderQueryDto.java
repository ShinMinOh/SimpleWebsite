package jpabook.jpashop.repository.order.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * OrderQueryDto를 따로 만든 이유는
 * OrderApiController 안에 있는 OrderDto를 참조하게 되면 OrderQueryRepository가 Controller을 참조하는 순환관계가 되어버림
 * */
@Data
public class OrderQueryDto {

    @JsonIgnore
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;
    private List<OrderItemQueryDto> orderItems;

    public OrderQueryDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address) {
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
        //orderItems는 1대다관계이므로 데이터가 늘어나 넣을 수 없음.
    }
}
