package com.kh.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.kh.dto.CarDTO;

@Mapper
public interface QuestMapper {

	List<CarDTO> selectAllCar();
  List<CarDTO> searchCar(String carName);
  int deleteCar(String carId);

}
