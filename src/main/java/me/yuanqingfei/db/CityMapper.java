package me.yuanqingfei.db;

import me.yuanqingfei.db.pojo.City;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CityMapper {
	
	@Insert("insert into c_city (city_id, city_name) values(#{cityId}, #{cityName})")
	int insertCity(City city);

//	@Select("SELECT * FROM orders WHERE seqNo = #{seqNo}")
//	BackendOrder getOrder(@Param("seqNo") String seqNo);
}
