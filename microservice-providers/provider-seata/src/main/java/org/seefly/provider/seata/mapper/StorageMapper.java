package org.seefly.provider.seata.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * 库存表
 */
@Repository
@Mapper
public interface StorageMapper {

    @Update("update t_storage  set used = used + #{count} , residue = residue -  #{count} where product_id = #{productId}")
    void decrease(@Param("productId")Long productId,@Param("count")Integer count);
}
