package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    /**
     * Web 확장 - 도메인 클래스 컨버터
     * parameter로 들어온 @PathVariable은 pk값이 들어온다
     * 이 때는 도메인 클래스 컨버터란 기능을 쓸 수 있음
     */
    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    /**
     * 클래스 컨버터 적용
     * 스프링이 중간에서 id(PK)값으로 member를 찾아내는 컨버팅 과정을 끝내고 찾은 엔티티(Member)를 파라미터로 injection 해줌
     * spring data JPA가 기본으로 해주는 기능
     * 원래 몇가지 등록을 해줘야 하는데 springboot는 기본으로 해줌
     * 도메인 클래스 컨버터도 memberRepository를 이용해서 엔티티를 찾음
     * 얘는 단순히 조회용으로만 사용해야 됨 transaction 범위가 없는 상황에서 조회를 했기 때문에 영속성 컨텍스트에 대한 개념이 애매함
     */
    @GetMapping("/members2/{id}")
    public String findMemberClassConverter(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    /**
     * @param pageable interface
     * Page는 결과값 interface
     * url: /members pageable를 인자로 받는 함수는 default 20개씩 출력
     * url: /members?page=? page 파라미터만 넘겨주게 되면 default 20개씩 데이터 리스트를 반환
     * url: /members?page=?&size=? size파라미터는 출력할 리스트의 size
     * url: /members?page=?&size=?&sort=id,desc id값 역정렬
     * url: /members?page=?&size=?&sort=id,desc&sort=username,desc
     */
    @GetMapping("/members")
    public Page<Member> list(@PageableDefault(size = 5, sort = "id") Pageable pageable) { // 단일 설정 @PageableDefault

        /**
         * 글로벌 설정 application.yml 세팅
         * @PageableDefault 단일 설정
         * @Qualifier에 접두사명 추가, 페이징 정보가 둘 이상이면 접두사로 구분
         */

        // Pageable를 파라미터로 받는 PagingAndSortingRepository interface의 함수
        // 내가 직접 구현한 함수의 마지막 파라미터로 pageable을 넘겨줘도 됨
        
        Page<Member> members = memberRepository.findAll(pageable); 
        return members;
    }

    /**
     * page Member entity => MemberDto 변환
     * page 객체는 page 시작을 0부터 시작한다 1부터 시작하고 싶으면 ? (spring data JPA PDF 참고)
     */
    @GetMapping("/members2")
    public Page<MemberDto> memberDtoList(@PageableDefault(size = 5) Pageable pageable) {
        // member를 인자로 받는 MemberDto 생성자가 있으면 람다 레퍼런스 가능
        // 또, page 객체를 반환하기 때문에 page 객체에 대한 정보(totalcount, totalElements 등등 사용하면 됨)
        Page<Member> list = memberRepository.findAll(pageable);
        return list.map(MemberDto::new);
    }


    // application 실행시 실행 메소드
    @PostConstruct
    public void init() {
        for (int i = 0; i < 100; i++) {
            memberRepository.save(new Member("user" + i, i));
        }
    }

}
