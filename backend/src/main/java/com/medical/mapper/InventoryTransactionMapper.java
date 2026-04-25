package com.medical.mapper;

import com.medical.entity.InventoryTransaction;
import com.medical.entity.Params;
import com.medical.entity.SearchForm;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface InventoryTransactionMapper {

    void out(Params params);

    void in(Params params);

    void transfer(Params params);

    void Edit(Params params);

    @Select("select * from inventorytransactions")
    List<InventoryTransaction> selectAll();

    List<InventoryTransaction> search(SearchForm searchForm);

    @Delete("delete from inventorytransactions where TransactionID = #{transactionId}")
    void deleteById(Integer transactionId);
}
