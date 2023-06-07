package jpabook.jpashop.domain.item;


import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@DiscriminatorColumn(name = "dtype")  //구분코드
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
/*상속관계는 전략을 지정해야함. 전략은 부모클래스에 잡아줘야 함. SINGLE_TABLE전략을 사용.
  JOINED: 가장 정규화된 스타일.
  SINGLE_TABLE: 한테이블에 다 때려박는 스타일.
  TABLE_PER_CLASS: 예를 들면 BOOK,MOVIE,ALBUM 3가지의 테이블만 나오는 전략
*/
public abstract class Item { //추상클래스로 만듬. 구현체를 가지고 할거기 때문에. 또한 상속관계 매핑을 해야함.

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;


    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    //==비즈니스 로직==// 데이터를 가지고 있는쪽에 비즈니스 메소드가 있는것이 가장 좋다.(stock 증가/감소는 stockQuantity가 있는 Item 클래스에 생성 )
    
    /**
     *  stock 증가
     *  */
    public void addStock(int quantity){
        this.stockQuantity+=quantity;
    }
    
    /**
     *  stock 감소
     *  */
    public void removeStock(int quantity){
        int restStock = this.stockQuantity-quantity;
        if(restStock < 0){
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity=restStock;
    }

}
