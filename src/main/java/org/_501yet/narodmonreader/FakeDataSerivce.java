package org._501yet.narodmonreader;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class FakeDataSerivce {

	private static final String[] sensorIds = { "28923", "343443", "324324" };
	
	private static final int TEMP_BASE_VALUE = 19;
	
	public List<NarodmonSensor> readSensors() {
		return generateSensorsData();
	}
	
	List<NarodmonSensor> generateSensorsData() {
		List<NarodmonSensor> sensors = new ArrayList<>();
		for (String sensorId : sensorIds) {
			NarodmonSensor sensor = new NarodmonSensor();
			sensor.setId(sensorId);
			sensor.setValue(generateTempValue());
			sensor.setTime(LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond());
			sensors.add(sensor);
		}
		return sensors;
	}
	
	String generateTempValue() {
		double tempValue = TEMP_BASE_VALUE + Math.random();
		return String.valueOf(tempValue);
	}
	
}
