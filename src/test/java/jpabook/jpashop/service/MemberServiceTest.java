package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)   //JUnit 실행할때 spring이랑 같이 엮어서 실행할래.
@SpringBootTest             //위 2개의 어노테이션이 있어야 스프링이랑 완전히 통합해서 스프링부트를 올려서 테스트할수있음.스프링부트를 띄운상태에서 태스트할때.이거없으면 Autowired다 실패함.
@Transactional              //테스트에서만 롤백을함. 서비스나 다른곳에서는 롤백X. 테스트 코드가 끝난 후에 바로 DB를 rollback을 해줘서 변화된 데이터가 보이지 않음.
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;

    @Test
    //@Rollback(false)
    public void 회원가입() throws Exception {
        //given
        Member member = new Member();
        member.setName("kim");

        //when  ~join할때
        Long savedId = memberService.join(member);

        //then
        //em.flush(); // DB에 강제로 쿼리가 나가서 insert되서 데이터가 반영됨. EntityManger 선언해줘야함.
        assertEquals(member, memberRepository.findOne(savedId));

    }

    @Test(expected = IllegalStateException.class)   //expected=IllegalSatateException-->try&catch문을 써줌
    public void 중복_회원_예외() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        //when
        memberService.join(member1);
        memberService.join(member2);

        /*try{
            memberService.join(member2);
        } catch (IllegalStateException e){
            return;
        }*/

        //then 예외처리 안터지고 여기코드까지 도달하면 안됨.
        fail("예외가 발생해야 한다.");
    }



}