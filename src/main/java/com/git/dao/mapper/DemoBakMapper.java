package com.git.dao.mapper;

import com.git.dao.pojo.DemoBak;
import com.git.dao.pojo.DemoBakExample;
import java.util.List;

import com.git.datasource.DataSource;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

@DataSource("datasourceBakKey")
public interface DemoBakMapper {
    int countByExample(DemoBakExample example);

    int deleteByExample(DemoBakExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(DemoBak record);

    int insertSelective(DemoBak record);

    List<DemoBak> selectByExampleWithRowbounds(DemoBakExample example, RowBounds rowBounds);

    List<DemoBak> selectByExample(DemoBakExample example);

    DemoBak selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") DemoBak record, @Param("example") DemoBakExample example);

    int updateByExample(@Param("record") DemoBak record, @Param("example") DemoBakExample example);

    int updateByPrimaryKeySelective(DemoBak record);

    int updateByPrimaryKey(DemoBak record);
}