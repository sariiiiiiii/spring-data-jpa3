package study.datajpa.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item implements Persistable<String> {

    /**
     * save시점에 id값이 셋팅을 안했으니까 현재 id값은 Null이다 (primitive type은 0으로 판단, 기본형은 null이 안되니까)
     * @GeneratedValue의 ID값이 언제 들어가는거면 EntityManager.persist 시점에 들어감
     * 그래서 SimpleJpaRepository.save()에서 persist, merge 분기처리 시점에서 어떤 메소드를 실행할 지 알수 있음
     */

    /**
     * !! 중요
     * 그런데, @GeneratedValue를 안쓰는 domain class라면 ?
     * 예) @Id private String uuid;, new Item("A")
     * 이 때 save()를 호출하면 pk값이 있다고 판단에 persist가 아닌 merge를 호출하게 된다
     * merge()의 특징은 DB에 id값의 데이터를 한번 조회한다 select * from item where id = 'A'
     * 근데 당연히 없으니까 ? insert문 실행 ( 굉장히 비효율적임 )
     * 그럴 때는 Persistable<T> 인터페이스를 상속받아서 처리할 수 있음
     */

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @CreatedDate
    private LocalDateTime createdDate;

    public Item(String name) {
        this.name = name;
    }

    @Override
    public boolean isNew() {
        return createdDate == null; // createdDate는 persist시점에 나가는 거기 때문에 null이면 새로운 객체라는 의미
    }
}
