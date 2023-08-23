package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    @Autowired  //spring이 springBean에 등록되어 있는 repository패키지의 MemberRepository를  injection 해줌.
    private final MemberRepository memberRepository;



    /**
    *  회원가입
    */
    @Transactional
    public Long join(Member member){

        validateDuplicateMember(member);  //중복 회원 검증
        memberRepository.save(member);    //중복 회원이 아닐시 save
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findByMembers = memberRepository.findByName(member.getName());
        if(!findByMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    /**
     *회원 전체 조회
     */
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    /**
     * 회원 Id로 단건(1개)조회
     */
    public Member findOne(Long memberId){
        return memberRepository.findOne(memberId);
    }

    /**
     * 회원 이름 변경
     * */
    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepository.findOne(id);
        member.setName(name);
    }
}
