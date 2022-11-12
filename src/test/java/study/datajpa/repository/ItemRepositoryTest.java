package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.datajpa.entity.Item;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRepositoryTest {

    /**
     * 새로운 엔티티 구별하는 방법
     */

    @Autowired
    ItemRepository itemRepository;

    @Test
    public void save() {

        /**
         * save시점에 id값이 셋팅을 안했으니까 현재 id값은 Null이다 (primitive type은 0으로 판단, 기본형은 null이 안되니까)
         * @GeneratedValue의 ID값이 언제 들어가는거면 EntityManager.persist 시점에 들어감
         * 그래서 SimpleJpaRepository.save()에서 persist, merge 분기처리 시점에서 어떤 메소드를 실행할 지 알수 있음
         */
        itemRepository.save(new Item("itemA"));

    }

}