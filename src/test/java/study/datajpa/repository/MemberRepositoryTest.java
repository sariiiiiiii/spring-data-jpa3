package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

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

}