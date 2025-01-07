package com.kh.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.kh.dto.BoardMemberDTO;
import com.kh.service.BoardMemberService;



@RequestMapping("/member") // - /member 에 해당하는것을 여기 컨트롤러로 정의
@Controller
public class MemberController {
    private BoardMemberService boardMemberService;

    // 생성자
    public MemberController(BoardMemberService boardMemberService) {
        this.boardMemberService = boardMemberService;
    }

    
    @GetMapping("/main")
    public ModelAndView memberMain(ModelAndView view) {
        List<BoardMemberDTO> memberList = boardMemberService.selectAllMember();

        view.addObject("memberList", memberList);
        view.setViewName("admin_member_main");
    
        return view;
    }

    @ResponseBody // 보내야 하기 원하는 데이터
    @DeleteMapping("/delete")
    public Map<String, Object> deleteMember(@RequestBody Map<String, Object> param) {

        HashMap<String, Object> map = new HashMap<String, Object>();
        
        String id = param.get("id").toString();
        int count = boardMemberService.deleteMember(id);

        if (count > 0) {
            map.put("msg", id+ "- 해당 아이디 삭제 완료");
        }else{
            map.put("msg", id+ "삭제할 아이디를 찾을 수 없습니다.");
        }

        List<BoardMemberDTO> memberList = boardMemberService.selectAllMember();
        map.put("memberList", memberList);

        return map;
    }



}
