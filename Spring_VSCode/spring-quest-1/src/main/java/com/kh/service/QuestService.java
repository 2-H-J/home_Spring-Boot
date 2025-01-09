package com.kh.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kh.dto.CarDTO;
import com.kh.mapper.QuestMapper;

@Service
public class QuestService {
	private QuestMapper mapper;

	public QuestService(QuestMapper mapper) {
		this.mapper = mapper;
	}

	public List<CarDTO> selectAllCar() {
		return mapper.selectAllCar();
	}

  public List<CarDTO> searchCar(String carName) {
		return mapper.searchCar(carName);
  }

  public void deleteCar(String carId) {
		mapper.deleteCar(carId);
	}
	
	
}
