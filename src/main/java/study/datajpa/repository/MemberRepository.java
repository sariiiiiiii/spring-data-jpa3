package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
    /**
     * Interface는 Interface끼리 상속받을 때는 implements가 아닌 extends
     */
    /**
     * memberRepository를 보면 구현체밖에 없는데 이건 뭘까?
     * 스프링이 인터페이스(MemberRepository)를 보고 스프링 데이터 JPA가 구현클래스를 만들어서 꽂음
     * MemberRepository는 개발자가 만든게 아니라 spring data JPA가 만들어서 memberRepository injection
     */
    /**
     * 스프링 데이터 JPA가 자바의 기본적인 프록시 기술로 가짜 클래스를 만든다음에 주입
     * @Repository가 없는데 어떻게 스캔대상이 되지? (생략 가능)
     */
}
