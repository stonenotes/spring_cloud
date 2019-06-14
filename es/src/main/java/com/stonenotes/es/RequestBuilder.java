package com.stonenotes.es;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: javan
 * @Date: 2019/6/14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestBuilder {
    private String indexName;
    private String id;
    private String[] includes;
    private String[] excludes;
}
