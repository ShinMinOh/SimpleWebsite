package jpabook.jpashop.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * OrderRepository : Order 엔티티를 조회하는 용도. 핵심 비즈니스를 위해 엔티티를 찾을 때 사용.
 * OrderQueryRepository :쿼리쪽은 화면이나 API에 의존 관계가 있는것을 때어내기 위함. 엔티티가 아닌 특정 화면들에 핏한 쿼리들은 여기로
 * 장점 : 화면과 관련되 쿼리들은 여기서 파악이 가능하고 핵심 비즈니스 로직들은 OrderRepository 참조 등 분리해서 파악가능.
 * */

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    //OrderQueryDto 안에 생성자에서 orderItems값을 채워주지 못해서 여기서 넣어줌. Loop 돌면서 가져 온후 set으로 채워넣음.
    public List<OrderQueryDto> findOrderQueryDtos(){

        List<OrderQueryDto> result = findOrders();
        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderid());
            o.setOrderItems(orderItems);
        });
        return result;
    }

    private List<OrderQueryDto> findOrders() {
        return em.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                                " from Order o" +
                                " join o.member m" +
                                " join o.delivery d", OrderQueryDto.class)
                .getResultList();
        //orderItems는 1대다관계이므로 데이터가 늘어나 넣을 수 없음.
    }


    // 1대다인 부분은 따로 쿼리를 짜야함.
    private List<OrderItemQueryDto> findOrderItems(Long orderId) { //OrderItem안에 Order에 name이 존재
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id =  :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();

    }




}
