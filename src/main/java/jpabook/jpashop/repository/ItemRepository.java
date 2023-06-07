package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item){   //업데이트라도 생각하면 됨.
        /*item은 jpa 저장하기 전까지는 id값이 없다. id값이 없댜는 것은 완전히 새로 생성한 객체라는 뜻. 그래서 em.persist로 신규 item을 등록.*/
        if(item.getId() == null){
            em.persist(item);
        }
        else{
            em.merge(item);   //id가 있다는 뜻은 jpa db에 한번 들어간것이라는 뜻. merge를 통해서 강제 업데이트.
        }
    }

    /**
     * item 하나조회.
     * */
    public Item findOne(Long id){
        return em.find(Item.class, id);
    }

    /**
     * 여러 item 조회.
     * */
    public List<Item> findAll(){      //한개 조회면 find함수로 가능하지만 여러개 찾는것은 JPQL작성 해야함.
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }

}
