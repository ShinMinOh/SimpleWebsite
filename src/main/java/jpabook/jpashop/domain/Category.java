package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Category {

    @Id @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(name = "category_item",                                            //중간테이블메핑
             joinColumns = @JoinColumn(name = "category_id"),
             inverseJoinColumns = @JoinColumn(name = "item_id"))  //객체는 다대다 관계가 가능하지만 관계형 db의 경우 다대다 관계의 경우 일대다 다대일로 풀어내는 중간테이블이 있어야함.
    private List<Item> items = new ArrayList<>();


    //Category의 계층구조를 만들어주기 위한 부모 자식간 관계. 셀프로 양방향 연관관계를 만든것.
    /*
        @ManyToMany 는 편리한 것 같지만, 중간 테이블( CATEGORY_ITEM )에 컬럼을 추가할 수 없고, 세밀하게
        쿼리를 실행하기 어렵기 때문에 실무에서 사용하기에는 한계가 있다. 중간 엔티티( CategoryItem 를
        만들고 @ManyToOne , @OneToMany 로 매핑해서 사용하자. 정리하면 대다대 매핑을 일대다, 다대일
        매핑으로 풀어내서 사용하자.

    */
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();//


    //==연관관계 메서드==//
    public void addChildCategory(Category child){  //
        this.child.add(child);  //부모 컬렉션에서도 자식이 들어가야되고
        child.setParent(this);  //반대로 자식에서도 부모가 누군지 this로 넣어줘야 한다.
    }
}
