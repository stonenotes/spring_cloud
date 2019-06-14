package com.stonenotes.elasticsearch.dao;

import com.stonenotes.elasticsearch.pojo.Item;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author: javan
 * @Date: 2019/6/13
 */
//@Repository
public interface ItemRepository extends ElasticsearchRepository<Item, Long> {
}
