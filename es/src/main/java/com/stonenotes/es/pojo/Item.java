package com.stonenotes.es.pojo;

import com.stonenotes.es.annotations.MyDocument;
import com.stonenotes.es.annotations.MyField;
import com.stonenotes.es.annotations.MyFieldType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

/**
 * @Author: javan
 * @Date: 2019/6/13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@MyDocument(indexName = "item", type = "_doc", shards = 1, replicas = 0)
public class Item {
    @Id
    private Long id;
    @MyField(type = MyFieldType.Text, analyzer = "ik_max_word")
    String title; //标题
    @MyField(type = MyFieldType.Keyword)
    String category;// 分类
    @MyField(type = MyFieldType.Keyword)
    String brand; // 品牌
    @MyField(type = MyFieldType.Double)
    Double price; // 价格
    @MyField(index = false, type = MyFieldType.Keyword)
    String images; // 图片地址
}
