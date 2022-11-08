package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    public void testMember() {

        // given
        Member member = new Member("memberA");

        // when
        Member savedMember = memberJpaRepository.save(member);
        Member findMember = memberJpaRepository.find(member.getId());

        // then
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);

    }

    @Test
    public void findByUsernameAndAgeGreaterThen() {
        Member member1 = new Member("AAA", 20);
        Member member2 = new Member("BBB", 10);
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);
        List<Member> result = memberJpaRepository.findByUsernameAndAgeGreaterThen("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    /**
     * namedQuery 사용
     */
    @Test
    public void testNamedQuery() {

        Member member1 = new Member("AAA", 20);
        Member member2 = new Member("BBB", 10);
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        List<Member> result = memberJpaRepository.findByUsername("AAA");

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);

    }

}