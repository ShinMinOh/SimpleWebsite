package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
/**
 * 회원 조회 API worse & best version
 * 회원 등록 API worse & best version
 * 회원 수정 API worse & best version
 * */
@RestController //@Controller + @ResponseBody( 데이터 자체를 바로 json이나 xml보내고자 할때) : REST API스타일로 만드는것
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    //회원 조회 worse ver
    @GetMapping("/api/v1/members")
    public List<Member> membersV1(){
        return memberService.findMembers();
    }

    //회원 조회 best ver
    @GetMapping("/api/v2/members")
    public Result memberV2(){   //엔티티->DTO로 변환
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()  //List Member 를 List MemberDto로 변환
                .map(m -> new MemberDto(m.getName(),m.getAddress()))
                .collect(Collectors.toList());

        return new Result(collect.size(),collect);  //return 값이 아래 Result 클래스임.
    }

    @Data
    @AllArgsConstructor
    static class Result<T>{ //object타입으로 반환하는것이기 때문에 Result가 List 껍데기 씌어져서 나갈것임
                    //List나 Collection타입을 바로 반환하면, JSON 배열타입으로 넘어가기 때문에 유연성이 확떨어짐.
        private int count;
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto{  //필요한것만 노출시킬 수 있음.dto의 장점.api 스펙 수정 용이.
        private String name;
        private Address address;
    }

    //회원 등록 API worse ver
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){ //@RequestBody: Json 데이터를 Member로 바꿔줌
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);

    }
    //회원 등록 API best ver
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){
        // CreateMemberRequest와 같은 별도의 DTO를 만드는것이 api 만드는 정석. 위 V1처럼 Member 엔티티를 직접 가져오지 않아 엔티티와 api스펙을 명확하게 분리.엔티티가 변해도 api스펙이 변하지 않음.
        //절대 엔티티를 외부에 노출시키거나 파라미터로 그대로 받는것 금지.
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    //회원 수정 best ver.
    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request){

        memberService.update(id, request.getName()); //query
        Member findMember = memberService.findOne(id);  //command 위의 쿼리형식과 커맨드 형식 분리.유지보수 용이해짐.
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data //DTO
    static class UpdateMemberRequest{
        private String name;
    }

    @Data //DTO
    @AllArgsConstructor
    static class UpdateMemberResponse{
        private Long id;
        private String name;
    }

    @Data //DTO
     static class CreateMemberRequest{
        private String name;

    }

    @Data //DTO
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }


}
