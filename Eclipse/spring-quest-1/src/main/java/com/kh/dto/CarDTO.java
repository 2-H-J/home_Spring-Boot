package com.kh.dto;

import org.apache.ibatis.type.Alias;

@Alias("car")
public class CarDTO {
	private String carId;
	private String carName;
	private String carMaker;
	private int carMakeYear;
	private int carPrice;

	public CarDTO() {
	}

	public String getCarId() {
		return carId;
	}

	public void setCarId(String carId) {
		this.carId = carId;
	}

	public String getCarName() {
		return carName;
	}

	public void setCarName(String carName) {
		this.carName = carName;
	}

	public String getCarMaker() {
		return carMaker;
	}

	public void setCarMaker(String carMaker) {
		this.carMaker = carMaker;
	}

	public int getCarMakeYear() {
		return carMakeYear;
	}

	public void setCarMakeYear(int carMakeYear) {
		this.carMakeYear = carMakeYear;
	}

	public int getCarPrice() {
		return carPrice;
	}

	public void setCarPrice(int carPrice) {
		this.carPrice = carPrice;
	}

	@Override
	public String toString() {
		return "CarDTO [carId=" + carId + ", carName=" + carName + ", carMaker=" + carMaker + ", carMakeYear="
				+ carMakeYear + ", carPrice=" + carPrice + "]";
	}

}
