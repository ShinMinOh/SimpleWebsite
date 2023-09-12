package jpabook.jpashop.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * OrderRepository : Order 엔티티를 조회하는 용도. 핵심 비즈니스를 위해 엔티티를 찾을 때 사용.
 * OrderQueryRepository :쿼리쪽은 화면이나 API에 의존 관계가 있는것을 때어내기 위함. 엔티티가 아닌 특정 화면들에 핏한 쿼리들은 여기로
 * 장점 : 화면과 관련되 쿼리들은 여기서 파악이 가능하고 핵심 비즈니스 로직들은 OrderRepository 참조 등 분리해서 파악가능.
 * */

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

   /**
    * OrderQueryDto 안에 생성자에서 orderItems값을 채워주지 못해서 여기서 넣어줌. Loop 돌면서 가져 온후 set으로 채워넣음.
    */
    public List<OrderQueryDto> findOrderQueryDtos(){

        List<OrderQueryDto> result = findOrders();  // query 1번 -> N개 (orders 개수)
        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());    //Query N번 (orders 안에 있는 OrderItems 개수)
            o.setOrderItems(orderItems);
        });
        return result;
    }

    /**
     * 1:N 관계(컬렉션)를 제외한 나머지를 한번에 조회
     */
    private List<OrderQueryDto> findOrders() {
        return em.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                                " from Order o" +
                                " join o.member m" +
                                " join o.delivery d", OrderQueryDto.class)
                .getResultList();
        //orderItems는 1대다관계이므로 데이터가 늘어나 넣을 수 없음.
    }


    /**
     * 1대다(1:N)인 부분은 따로 쿼리를 짜야함. OrderItem과 Item 조인
     */
     private List<OrderItemQueryDto> findOrderItems(Long orderId) { //OrderItem안에 Order에 name이 존재
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id =  :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();

    }


    public List<OrderQueryDto> findAllByDto_optimization() {
        List<OrderQueryDto> result = findOrders();  // ToOne 관계들 먼저 조회

        List<Long> orderIds = result.stream()
                .map(o -> o.getOrderId())           //map을 써서 o(OrderQueryDto) 를 orderId로 바꾸고
                .collect(Collectors.toList());      // 그 값들을 리스트로 반환


        //orderIds(4번,11번) 안에 있는 orderItems 4개가 뽑혀 나옴. ToMany 관계인 OrderItem 한꺼번에 조회
        List<OrderItemQueryDto> orderItems = em.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                                " from OrderItem oi" +
                                " join oi.item i" +
                                " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        // orderItems를 코드도 작성하기 쉽고 성능 최적화를 위해 Map으로 바꿔줌.
        //Long 타입인 key는 orderItemQueryDto.getOrderId() 즉 orderId 이고 값은 List<OrderItemQueryDto>
        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
                .collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));

        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));
        //forEach로 돌리면서 orderItems 넣어주면 됨.orderItemMap안에서 찾는데 키값은 orderId이므로 .get(키값)으로 가져오기
        //앞에 version 들은 루프를 돌리자마자 쿼리를 날렸는데 이방법은 em.createQuery에서 1번 날리고 메모리에서  Map으로 다 가져온 후
        //메모리에서 매칭 시켜서 값들을 가져옴. 그래서 Query는 총 2번(findOrders() 1번 + em.createQuery 1번) 나감.
        return result;
    }

    public List<OrderFlatDto> findAllByDto_flat() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderFlatDto(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d" +
                        " join o.orderItems oi" +
                        " join oi.item i", OrderFlatDto.class)
                .getResultList();
    }

}
