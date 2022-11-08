package study.datajpa.repository;

import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberJpaRepository {

    @PersistenceContext
    private EntityManager em;

    // 저장
    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    // 삭제
    public void delete(Member member) {
        em.remove(member);
    }

    // 단건 조회
    public Member find(Long id) {
        return em.find(Member.class, id);
    }

    // Optional 이용한 조회
    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member); // Optional.ofNullable = null일수도 있고 아닐수도 있다
    }

    // count는 long으로 반환해줌
    public long count() {
        return em.createQuery(
                "select count(m) from Member m", Long.class)
                .getSingleResult();
    }

    // 전체 조회
    public List<Member> findAll() {
        return em.createQuery(
                        "select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByUsernameAndAgeGreaterThen(String username, int age) {
        return em.createQuery(
                        "select m from Member m where m.username = :username and m.age > :age", Member.class)
                .setParameter("username", username)
                .setParameter("age", age)
                .getResultList();
    }

    public List<Member> findByUsername(String username) {
        return em.createNamedQuery("Member.findByUsername", Member.class)
                .setParameter("username", username)
                .getResultList();
    }


}

