package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    /**
     * custom repository는 spring data JPA가 제공하는 기술중 하나
     * MemberRepositoryCustom을 구현받은 클래스는 꼭 MemberRepository <- JpaRepository를 상속받은 인터페이스 네임으로 하며 뒤에 Impl을 붙여준다
     * 예) TeamRepositoryImpl, MemberRepositoryImpl
     */

    /**
     * 항상 사용자 정의 레포지토리가 필요한 것은 아니다
     * 그냥 임의의 레포지토리를 만들어도 된다.
     * 예를들어 MemberQueryRepository를 인터페이스가 아닌 클래스로 만들고 스프링 빈으로 등록해서 그냥 직접 사용해도 된다
     * 물론 이 경우 스프링 데이터 JPA와는 아무런 관계 없이 별도로 동작한다
     * (핵심 비즈니스 로직과 화면에 관련된 로직을 나누자 !!)
     */

    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom(String username) {
        return em.createQuery(
                "select m from Member m", Member.class)
                .getResultList();
    }
}
