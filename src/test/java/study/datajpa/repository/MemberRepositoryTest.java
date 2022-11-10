package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Arrays;
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

    @Autowired
    TeamRepository teamRepository;

    @PersistenceContext
    EntityManager em;

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

    /**
     * @Query 사용
     */
    @Test
    public void namedQuery() {

        Member member1 = new Member("AAA", 20);
        Member member2 = new Member("BBB", 10);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUsername("AAA");

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(member1);

    }

    /**
     * @Query, 레포지토리 메소드에 쿼리 정의하기
     */
    @Test
    public void testQuery() {

        Member member1 = new Member("AAA", 20);
        Member member2 = new Member("BBB", 10);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findUser("AAA", 20);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(member1);

    }

    /**
     * @Query username 리스트 출력
     */
    @Test
    public void findUsername() {
        
        Member member1 = new Member("AAA", 20);
        Member member2 = new Member("BBB", 10);

        memberRepository.save(member1);
        memberRepository.save(member2);
        
        List<String> username = memberRepository.findUsername();
        for (String s : username) {
            System.out.println("username = " + s);
        }
    }

    /**
     * @Query DTO 조회
     */
    @Test
    public void findMemberDto() {

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("AAA", 20);
        member1.changeTeam(teamA);
        Member member2 = new Member("BBB", 10);
        member2.changeTeam(teamB);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<MemberDto> memberDto = memberRepository.findMemberDto();

        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }

    }

    /**
     * 파라미터 바인딩
     * Sql in절로 조회해야할 시 @Param에 collection으로 in절 조회 가능
     */
    @Test
    public void findByNames() {
        Member member1 = new Member("AAA", 20);
        Member member2 = new Member("BBB", 10);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> members = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));

        for (Member member : members) {
            System.out.println("name = " + member.getUsername());
        }
    }

    /**
     * 반환 타입
     * 주의 !!
     */
    @Test
    public void returnType() {

        Member member1 = new Member("AAA", 20);
        Member member2 = new Member("BBB", 10);

        memberRepository.save(member1);
        memberRepository.save(member2);

        /**
         * 반환타입이 컬렉션인 경우에
         * parameter 및 조회 객체가 없을 때 null을 반환해주는게 아니라 emptyList를 반환해준다
         * 즉, members != null 이런 코드를 짜면 안된다 (안 좋은 코드)
         */
        List<Member> members = memberRepository.findListByUsername("AAA");

        /**
         * 반환타입이 단건인 경우
         * 원래 반환이 null일 때 JPA는 원래 getSingleResult()를 했을 떄 결과가 없으면 noResultException을 터트린다
         * 그러나, spring data JPA는 없으면 왜 exception을 터트리냐 해서 try ~ catch로 감싸서 null로 반환을 해준다
         */
        Member member = memberRepository.findMemberByUsername("AAA");

        /**
         * Optional은 결과가 없을수도 있을 떄 사용하면 좋음
         * 결과가 없을 시 Optional.empty 비어있다고 말해줌
         * opMember.orElseThrow() 등.. 후 처리를 할 수 있음
         */
        Optional<Member> opMember = memberRepository.findOptionalByUsername("AAA");

        /**
         * 반환타입이 단건인데 조회시 결과가 다건이 나오면 이 경우는 예외를 막을 수 없다
         * spring data JPA가 IncorrectResultSizeDataAccessException 발생
         */

    }

    /**
     * 페이징
     * 검색조건 : 나이가 10살
     * 정렬 조건 : 이름으로 내림차순
     * 페이징 조건 : 첫 번째 페이지, 페이지당 보여줄 데이터는 3건
     */
    @Test
    public void paging() {

        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        // spring data JPA는 페이지가 0부터 시작하는게 아니라 0부터 시작 (주의 !)
        // page : 현재 페이지, size : 데이터를 몇개씩 들고 올건지, Sort.by(정렬조건, 정렬할 필드)
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        int age = 10;

        // when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        // long totalCount = memberRepository.totalCount(age); // Page 객체로 반환시 totalCount query 생략... 알아서 다 해줌...

        /**
         * page 객체 DTO 변환
         */
        Page<MemberDto> memberDtos = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
        //for (MemberDto memberDto : memberDtos) { // 왜 이것도 되는것일까....
        for (MemberDto memberDto : memberDtos.getContent()) {
            System.out.println("memberDto = " + memberDto);
        }

        // then
        /**
         * Page.getContent() = page 내부에 있는 데이터 추출
         * Page.getTotalElements() = totalCount 추출
         * Page.getNumber() = 현재 page 번호 계산
         * Page.getTotalPages() = 총 page 갯수 계산
         * Page.isFirst() = 첫 번째 페이지인가 Boolean값
         * Page.hasNext() = 다음 페이지가 있나
         */
        List<Member> members = page.getContent();

        assertThat(members.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();

    }

    /**
     * 슬라이스
     * totalCount는 생략하는 인터페이스 구현체 (모바일에서 스크롤을 맨 아래로 내릴시 +더보기 버튼이 있거나 자동으로 새로운 리스트가 출력될 때 사용)
     * 그럼 slice는 다음 정보가 있는지 없는지 어떻게 알까?
     *   ㄴ query를 날릴때 limit보다 +1 더 날려서 다음 정보가 있는지 없는지 판단
     *   ㄴ query 결과 int limit 3이여도 limit = 4 를 날림
     * 검색조건 : 나이가 10살
     * 정렬 조건 : 이름으로 내림차순
     * 페이징 조건 : 첫 번째 페이지, 페이지당 보여줄 데이터는 3건
     */
    @Test
    public void pageSlice() {

        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        // spring data JPA는 페이지가 0부터 시작하는게 아니라 0부터 시작 (주의 !)
        // page : 현재 페이지, size : 데이터를 몇개씩 들고 올건지, Sort.by(정렬조건, 정렬할 필드)
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        int age = 10;

        // when
        Slice<Member> slice = memberRepository.findSliceByAge(age, pageRequest);

        // then
        /**
         * slice.getContent() = page 내부에 있는 데이터 추출
         * slice.getNumber() = 현재 page 번호 계산
         * slice.isFirst() = 첫 번째 페이지인가 Boolean값
         * slice.hasNext() = 다음 페이지가 있나
         */
        List<Member> members = slice.getContent();

        assertThat(members.size()).isEqualTo(3);
        assertThat(slice.getNumber()).isEqualTo(0);
        assertThat(slice.isFirst()).isTrue();
        assertThat(slice.hasNext()).isTrue();

    }

    /**
     * namedQuery와 쿼리메소드의 우선순위 확인
     * namedQuery가 먼저 (application loading시 parsing이 되서 그런가...)
     */
    @Test
    public void testPageable() {

        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        int age = 10;

        Page<Member> page = memberRepository.findTestByAge(age, pageRequest);
        assertThat(page.getContent().size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();

    }

    /**
     * spring data JPA 벌크성 쿼리
     * 순수 JPA도 그렇고 spring data JPA도 그렇고 벌크성 쿼리 이후 꼭 flush()를 해줘야 한다
     * DB에는 반영이 되어 있지만 영속성 컨텍스트에는 이전 정보가 계속 남아있기 떄문
     */
    @Test
    public void bulkAgePlus() {

        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 25));
        memberRepository.save(new Member("member5", 40));

        /**
         * 중요 !!
         * JPQL은 JPQL이 실행되기 전에 먼저 flush()를 다 하고 JPQL이 실행됨
         * 그래서 위 save도 flush()를 안해도 JPQL이 실행되기 떄문에 먼저 insert query가 나간다음 update 실행
         */

        // when
        int resultCount = memberRepository.bulkAgePlus(20);
//        em.flush(); // 순수 JPA기반
//        em.clear(); // 순수 JPA기반

        List<Member> result = memberRepository.findByUsername("member5");
        Member findMember = result.get(0);
        System.out.println("findMember = " + findMember.getAge()); // getAge = 40

        /**
         * 벌크 연산은 바로 DB에 update를 하기 때문에 영속성 컨텍스트는 update 된지 인지할 수 없다
         * 그래서 벌크연산 이후에는 영속성 컨텍스트를 다 날려야 한다
         */

        // then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() {

        // given
        // member1 => teamA (참조)
        // member2 => teamB (참조)

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // when
        // N + 1 문제 발생
        List<Member> members = memberRepository.findAll(); // Member만 가지고옴 team은 가짜객체로
        for (Member member : members) {
            System.out.println("member = " + member.getUsername()); // Member 이름 (영속성 컨텍스트)
            System.out.println("member.getClass() = " + member.getClass()); // entity
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass()); // Member만 가지고 와서 Member.team은 null로 둘 순 없으니까 proxy라는 텅텅 빈 가짜 객체
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());  // 실제 호출해서 DB에 query (프록시 초기화)
        }
    }

    @Test
    public void findFetchJoinFindMember() {

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        List<Member> members = memberRepository.findMember();
        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.getClass() = " + member.getClass()); // entity
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass()); // entity
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName()); // entity
        }
    }

    /**
     * @EntityGraph를 이용한 조회
     */
    @Test
    public void entityGraph() {

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> members = memberRepository.findAll();
        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("member.getClass() = " + member.getClass());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
        }

        List<Member> memberEntityGraph = memberRepository.findMemberEntityGraph();
        for (Member member : memberEntityGraph) {
            System.out.println("member = " + member);
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }

        List<Member> member11 = memberRepository.findEntityGraphByUsername("member1");
        for (Member member : member11) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void queryHint() {

        // given
        Member member = new Member("member1", 10);
        memberRepository.save(member); // JPA 영속성 컨텍스트에 넣어놓고
        em.flush(); // 강제 DB query날림
        em.clear(); // 영속성 컨텍스트 싹 비우기
        // clear() 이 후 영속성 컨텍스트를 싹 날려버렸기 때문에 JPA에서 조회를 하면 영속성 컨텍스트에서 조회하는것이 아니라 DB에서 다시 조회

        // when
        Member findMember = memberRepository.findById(member.getId()).get();
        findMember.setUsername("member2");

        /**
         * 변경감지에 치명적인 단점은 객체를 2개를 관리해야한다는 것
         *   ㄴ 변경감지를 할려면 원본 객체랑 비교를 해야 뭐가 달라진지 알기 때문에 원본 객체 + 변경한 객체
         */
        em.flush(); // 변경감지(더티체킹) update query

        /**
         * 하이버네이트는 (JPA아님) 단순 조회용으로 하게끔 Hint를 열어놨음
         * @QueryHint로 readOnly된 조회대상은 스냅샷을 안만들어 놓으며 값을 수정할 수 없음
         *   ㄴ update query 자체가 안나감
         */
        Member readOnlyMember = memberRepository.findReadOnlyByusername("member2");
        readOnlyMember.setUsername("memberAAA");

    }

    @Test
    public void lock() {
        // given
        Member member = new Member("member1", 10);
        memberRepository.save(member);
        em.flush();
        em.clear();

        // when
        /**
         * SQL문 뒤에 for update 쿼리가 나감
         */
        List<Member> members = memberRepository.findLockByusername("member1");
    }

    /**
     * 사용자 정의 레포지토리 인터페이스 구현
     */
    @Test
    public void callCustom() {

        memberRepository.save(new Member("memberA", 10));
        memberRepository.save(new Member("memberA", 20));

        em.flush();
        em.clear();

        List<Member> members = memberRepository.findMemberCustom("memberA");
        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
        }
    }

}