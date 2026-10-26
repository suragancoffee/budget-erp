package com.example.budget.mapper;

import com.example.budget.dto.BudgetStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 메서드 이름 = BudgetMapper.xml 안의 <select id="...">
@Mapper
public interface BudgetMapper {
    List<BudgetStatus> selectBudgetStatus(@Param("year") int year);
}
