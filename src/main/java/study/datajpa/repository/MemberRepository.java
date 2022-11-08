package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * Interface는 Interface끼리 상속받을 때는 implements가 아닌 extends
     * JpaRespository<Entity, PK type>
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

    /**
     * 스프링 데이터 JPA가 제공하는 쿼리 메소드 기능
     * 조회 : find...by, read...by, query...by, get...by
     *   ㄴ 예) findHelloBy 처럼 ...에 식별하기 위한 내용(설명)이 들어가도 된다
     * COUNT : count...By 반환타입 long
     * EXISTS : exists..By 반환타입 Boolean
     * 삭제 : delete...By, remove...By 반환타입 long
     * DISTINCT : findDistinct, findMemberDistinctBy (조회 ... 에다가 distinct를 넣으면 됨)
     * LIMIT : findFirst3, findFirst, findTop, findTop3
     */

    /**
     *  JPQL도 아니고 memberRepositoryTest에서 쓰는데 도대체 이게 어떻게 되는걸까..?
     *    ㄴ 메소드 이름을 Spring data JPA가 읽어서 SQL 쿼리문을 만든다
     *    findBy = select
     *    username = equals
     *    And = and
     *    AgeGreaterThan = age > parameter (파라미터 조건보다 age가 크면)
     */
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findHelloBy();

    List<Member> findTop3HelloBy();
}
