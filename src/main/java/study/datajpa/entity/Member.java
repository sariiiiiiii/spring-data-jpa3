package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;

@Getter @Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = { "id", "username", "age" }) // 연관관계를 ToString 했을시에는 무한루프가 걸릴수 있으니 연관관계는 ToString 제외
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String username;

    private int age;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(String username) {
        this.username = username;
    }

    // 연관관계 편의메소드
    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }

}
