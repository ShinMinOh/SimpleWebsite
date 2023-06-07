package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional  //위에 readOnly=true 어노테이션을 디폴트로 지정했으므로 item저장이 반영되기 위해서 따로 @Transactional을 써줘야함. 안하면 반영안됨.
    public void saveItem(Item item){
        itemRepository.save(item);
    }

    public List<Item> findItems(){
        return itemRepository.findAll();  //transaction이 없으므로 별도 어노테이션 없이 위의 readOnly적용.
    }

    public Item findOne(Long itemId){
        return itemRepository.findOne(itemId);
    }
}
