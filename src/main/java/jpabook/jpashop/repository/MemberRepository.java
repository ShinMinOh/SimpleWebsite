package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    public void save(Member member){
        em.persist(member);//jpa가 member저장
    }

    public Member findOne(Long id){     //조회
        return em.find(Member.class, id);  //jpa가 제공하는 find메소드. id값을 넘기면 member찾아서 반환해줌. 1가지조회.
    }

    public List<Member> findAll(){     //전부조회 (회원목록)
        //sql은 테이블대상으로 쿼리, jpql은 엔티티 객체를 대상으로 쿼리. createQuery(JPQL, 반환타입)형태
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name){  //회원이름으로 조회.
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();

        /*setParameter("naem",name)까지 써줘야 위에 쿼리문의 name과 바인딩됨.*/
    }


}
