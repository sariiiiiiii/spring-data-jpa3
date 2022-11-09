package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Getter @Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = { "id", "username", "age" }) // 연관관계를 ToString 했을시에는 무한루프가 걸릴수 있으니 연관관계는 ToString 제외
@NamedQuery(
        name = "Member.findByUsername",
        query = "select m from Member m where m.username = :username"
)
/**
 * 실무에서 namedQuery를 잘 안쓰지만 namedQuery에 가장 큰 장점이 있음
 * 보통 JPQL 작성시 qlString을 잘못입력해도 문자열이기 때문에 애플리케이션 로딩시나 다른 api 실행시에는 오류가 뜨지 않는데
 * namedQuery의 query는 애플리케이션 로딩시점에 한번 parsing을 해서 오류가 있으면 오류를 알려줌 (강력한 기능)
 */
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String username;

    private int age;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(String username) {
        this.username = username;
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if (team != null) { // team이 null일시 무시
            changeTeam(team);
        }
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    // 연관관계 편의메소드
    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }

}
