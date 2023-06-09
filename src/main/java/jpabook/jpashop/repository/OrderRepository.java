package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order){
        em.persist(order);
    }

    public Order findOne(Long id){
        return em.find(Order.class, id);
    }

    public List<Order> findAll(OrderSearch orderSearch){   //동적쿼리 jpql
        return em.createQuery("select o from Order o join o.member m" +     // order랑 order와 연관된 member를 조인해
                "where o.status = :status" +
                "and m.name like :name", Order.class)
                .setParameter("status", orderSearch.getOrderStatus())   //파라미터 바인딩
                .setParameter("name", orderSearch.getMemberName())      //파라미터 바인딩
                .setMaxResults(1000)   //최대 1000건
                .getResultList();

    }
}
