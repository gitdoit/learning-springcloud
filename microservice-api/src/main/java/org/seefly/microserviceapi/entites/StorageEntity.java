package org.seefly.microserviceapi.entites;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liujianxin
 * @date 2021/4/25 10:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StorageEntity {
    
    private Long id;
    /*产品id*/
    private Long productId;
    /*总量*/
    private Integer total;
    /*使用量*/
    private Integer used;
    /*剩余量*/
    private Integer residue;
}
