package study.datajpa.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass // 속성만 상속받는 클래스
public class JpaBaseEntity {

    /**
     * @Column(insertable = false) = SQL INSERT문에 해당 컬럼을 포함할지 여부
     * @Column(updatable = false) = SQL UPDATE문에 해당 컬럼을 포함할지 여부
     */

    @Column(updatable = false) // createdDate에 값을 실수로 바꿔도 DB에 값이 변경되지 않음 (update 막기)
    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    @PrePersist
    public void prePersist() {

        /**
         * @PrePersist JPA가 제공하는 이벤트
         * persist가 발생하기 전에 이벤트 발생하는 메소드
         */

        LocalDateTime now = LocalDateTime.now();
        this.createdDate = now;
        this.updatedDate = now;

    }

    @PreUpdate
    public void preUpdate() {

        /**
         * update가 발생하기 전에 이벤트 발생하는 메소드
         */

        this.updatedDate = LocalDateTime.now();

    }

}
