package jpabook.jpashop.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j //로그 찍기위한 어노테이션
public class HomeController {

    @RequestMapping("/")
    public String home(){
        log.info("home controller");
        return "home";
    }
}
