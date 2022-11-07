package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
    /**
     * Interface는 Interface끼리 상속받을 때는 implements가 아닌 extends
     */
}
