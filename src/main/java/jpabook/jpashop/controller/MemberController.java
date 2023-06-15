package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")  //form화면 열어보는 기능. 회원가입버튼 눌렀을때 해당 url
    public String createForm(Model model){
        model.addAttribute("memberForm", new MemberForm()); //controller에서 view로 넘어갈때, 이름이 memberForm인 객체가 MemberForm인 빈 껍데기 객체
        return "members/createMemberForm";
    }

    @PostMapping("/members/new") // 데이터를 실제로 등록
    public String create(@Valid MemberForm form, BindingResult result){         //MemberForm클래스의 @NotEmpty기능 validation하기 위해서 @Valid 써줘야함.
                                                                                /* BindingResult를 사용할 경우 valid 해서 오류가 생겼을때, 원래라면 오류가 나오고 튕겨나가지만
                                                                                 그 오류가 result에 담겨서 아래 코드가 실행됨. */
        if(result.hasErrors()){
            return "/members/createMemberForm";  //이름을 입력하지않아 error발생할경우 다시 이곳으로 돌아가도록 만듬.
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/";  //home으로 이동
    }
}
