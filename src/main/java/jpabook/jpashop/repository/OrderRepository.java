package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
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

    public List<Order> findAllByString(OrderSearch orderSearch){   //동적쿼리 jpql
       /* return em.createQuery("select o from Order o join o.member m" +     // order랑 order와 연관된 member를 조인해
                "where o.status = :status" +
                "and m.name like :name", Order.class)
                .setParameter("status", orderSearch.getOrderStatus())   //파라미터 바인딩
                .setParameter("name", orderSearch.getMemberName())      //파라미터 바인딩
                .setMaxResults(1000)   //최대 1000건
                .getResultList();

        */

            //language=JPQL
            String jpql = "select o From Order o join o.member m";
            boolean isFirstCondition = true;

//주문 상태 검색
            if (orderSearch.getOrderStatus() != null) {
                if (isFirstCondition) {
                    jpql += " where";
                    isFirstCondition = false;
                } else {
                    jpql += " and";
                }
                jpql += " o.status = :status";
            }

//회원 이름 검색
            if (StringUtils.hasText(orderSearch.getMemberName())) {
                if (isFirstCondition) {
                    jpql += " where";
                    isFirstCondition = false;
                } else {
                    jpql += " and";
                }
                jpql += " m.name like :name";
            }
            TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                    .setMaxResults(1000); //최대 1000건
            if (orderSearch.getOrderStatus() != null) {
                query = query.setParameter("status", orderSearch.getOrderStatus());
            }
            if (StringUtils.hasText(orderSearch.getMemberName())) {
                query = query.setParameter("name", orderSearch.getMemberName());
            }
            return query.getResultList();
        }

    }

