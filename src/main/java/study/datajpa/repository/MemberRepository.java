package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

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

    /**
     * namedQuery spring data JPA 으로 사용
     * @Query(name = "domain class에서 설정한 namedQuery name")
     *   ㄴ namedQuery name과 JpaRepository<Member> <- 타입과 해당 메소드명이 일치하면 @Query를 생략시킬수 있음
     *   ㄴ 맞는게 없네? 하면 query 메소드로 규칙을 찾음 (순서 namedQuery -> 쿼리 메소드)
     * @Param namedQuery에 들어가는 parameter === setParameter
     */
    @Query(name = "Member.findByUsername") // 생략가능 (조건 필요)
    List<Member> findByUsername(@Param("username") String username);

    /**
     * @Query, 레포지토리 메소드에 쿼리 정의하기
     * @Query에 정의하는 query문은 이름이 없는 namedQuery라고 생각하면 된다 namedQuery의 오류기능을 그대로 가져감
     * 장점 : @Query qlString은 문자열인데 오타가 났을 시 애플리케이션 로딩시점에 parsing해서 sql문을 다 만들어놓기 떄문에 틀린 문법이면 오류를 잡아줌 !! (강력한 기능)
     */
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    /**
     * @Query, 값, DTO 조회
     */
    @Query("select m.username from Member m") // 회원 이름 조회
    List<String> findUsername();

    /**
     * MemberDto 객체를 생성해주는 것은 JPA가 해줌
     */
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    /**
     * 파라미터 바인딩
     * Sql in절로 조회해야할 시 @Param에 collection으로 in절 조회 가능
     */
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    /**
     * 반환 타입
     */
    List<Member> findListByUsername(String username); // 컬렉션
    Member findMemberByUsername(String username); // 단건
    Optional<Member> findOptionalByUsername(String username); // 단건 Optional

    /**
     * spring data JPA Paging
     * @param age sql where parameter
     * @param pageable spring data JPA paging을 위한 pageable 현 test에서는 구현체로 PageRequest 사용
        * pageable은 query문에 날릴 offset, limit, sort를 결정해줄 뿐이지 반환타입에 따라 totalCount를 날릴지 결정하는 것
          * 즉, List<Member> findByAge(int age, Pageable pageable) 이런것도 다 가능하다 이거임
     * @return
     */
    Page<Member> findByAge(int age, Pageable pageable);

    /**
     * 슬라이스
        * totalCount는 생략하는 인터페이스 구현체 (모바일에서 스크롤을 맨 아래로 내릴시 +더보기 버튼이 있거나 자동으로 새로운 리스트가 출력될 때 사용)
        * 그럼 slice는 다음 정보가 있는지 없는지 어떻게 알까?
        *   ㄴ query를 날릴때 limit보다 +1 더 날려서 다음 정보가 있는지 없는지 판단
        *      ㄴ query 결과 int limit 3이여도 limit = 4 를 날림
     */
    Slice<Member> findSliceByAge(int age, Pageable pageable);

    /**
     * countQuery 최적화
     * 보통 join이 많이 들어갔을 시 totalCount query는 join문까지 다 할 필요는 없다 (성능이 저하됨)
     * 그럴때 @Query문에 queryValue로 totalCount를 최적화 시킬 수 있음
     */
    @Query(value = "select m from Member m left join m.team t", countQuery = "select count(m) from Member m")
    Page<Member> findCountQueryByAge(int age, Pageable pageable);

    @Query("select m from Member m") // @query를 주니까 findTestByAge 쿼리메소드는 안먹힘 namedQuery가 우선순위라는것을 알 수 있음
    Page<Member> findTestByAge(int age, Pageable pageable);

    /**
     * 벌크성 쿼리 spring data JPA
     * @Modifying annotaion을 추가해줘야지 벌크 update 쿼리가 나간다
     * @Modifying을 안적어주면 JPA에서 getResultList(), getSingleResult()가 나가게 됨
     * clearAutomatically = 벌크연산 후 영속성 컨텍스트를 날리지 말지 결정
     */
    @Modifying(clearAutomatically = true) // 쿼리가 나간다음이 clear 과정을 자동으로 해결
    @Query(value = "update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    // fetch join
    @Query("select m from Member m join fetch m.team t")
    List<Member> findMember();

    /**
     * spring data JPA에서는 맨날 @Query fetch join을 할 순 없으니까 @EntityGraph라는걸 지원한다
     * JpaRepository에 있는 findAll()를 @Override해서 재정의 해서 사용
     */
    @Override
    @EntityGraph(attributePaths = {"team"}) // member를 조회할 떄 team까지 같이 조회 (내부적으로 fetch join)
    List<Member> findAll();

    /**
     * JPQL로 query를 짜고 싶은데 fetch join만 살짝 곁들이고 싶으면 @Query, @EntityGraph 동시 사용
     */
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    /**
     * 쿼리 메소드와 @EntityGraph 같이 사용
     */
    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    /**
     * JPA Hint
     * 조회 최적화
     */
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByusername(String username);

    /**
     * JPA LOCK
     * 조회할 때도 다른사람이 조회하지 못하도록 LOCK (select for update)
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByusername(String username);

}
