package study.datajpa.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class) // META-INF에 xml파일을 만들어서 글로벌적용 설정을 할 수 있음
@MappedSuperclass
@Getter
public class BaseEntity extends BaseTimeEntity { // time만 따로 BaseTimeEntity 생성

    /**
     * @EntityListeners
     * 엔티티를 DB에 적용시키기 전, 이후에 커스텀 콜백을 요청할 수 있는 어노테이션
     * @EntityListeners의 인자로 커스텀 콜백을 요청할 클래스를 지정해주면 되는데, Auditing을 수행할 때는 JPA에서 제공하는 AuditingEntityListener.class를 인자로 넘겨주면 됨
     * @PrePersist, @PreUpdate 어노테이션으로 JPA의 Auditing 기능을 spring data JPA가 사용하게 되는 것
     */

//    @CreatedDate
//    @Column(updatable = false)
//    private LocalDateTime createdDate; // 등록일
//
//    @LastModifiedDate
//    private LocalDateTime lastModifiedDate; // 수정일

    /**
     * LocalDateTime은 시간이니까 값이 어떻게 들어가는지 알겠는데 등록자와 수정자는 값이 어떻게 들어가는 것일까 ?
     * @CreatedBy, @LastModifiedBy에서 등록되거나 수정 될때마다 main application의 auditorProvider를 호출해서 로그인 정보를 가지고 온다
     */

    @CreatedBy
    @Column(updatable = false)
    private String createdBy; // 등록자

    @LastModifiedBy
    private String lastModifiedBy; // 수정자

}
