package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@Rollback(value = false)
@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void testRepository() {
        /**
         * memberRepository를 보면 구현체밖에 없는데 이건 뭘까?
         * 스프링이 인터페이스를 보고 스프링 데이터 JPA가 구현클래스를 만들어서 꽂음
         * MemberRepository는 개발자가 만든게 아니라 spring data JPA가 만들어서 memberRepository injection
         */
        System.out.println("memberRepository = " + memberRepository.getClass());
    }

    @Test
    public void testMember() {

        Member member = new Member("memberB");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);

    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        // 삭제 카운터 검증
        long deleteCount = memberRepository.count();
        assertThat(deleteCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {

        /**
         *  JPQL도 아니고 memberRepositoryTest에서 쓰는데 도대체 이게 어떻게 되는걸까..?
         *    ㄴ 메소드 이름을 Spring data JPA가 읽어서 SQL 쿼리문을 만든다
         *    findBy = select
         *    username = equals
         *    And = and
         *    AgeGreaterThan = age > parameter (파라미터 조건보다 age가 크면)
         */

        Member member1 = new Member("AAA", 20);
        Member member2 = new Member("BBB", 10);
        memberRepository.save(member1);
        memberRepository.save(member2);
        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findHelloBy() {
        List<Member> findAll = memberRepository.findHelloBy();
    }

    @Test
    public void findTop3HelloBy() {
        List<Member> findMembers = memberRepository.findTop3HelloBy();
        for (Member findMember : findMembers) {
            System.out.println(findMember);
        }
    }

}