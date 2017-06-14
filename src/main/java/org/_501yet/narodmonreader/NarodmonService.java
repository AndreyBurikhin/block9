package org._501yet.narodmonreader;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

public class NarodmonService {

	private final NarodmonClient narodmonClient;
	
	private String[] sensorIds;
	
	NarodmonService(NarodmonClient narodmonClient) {
		this.narodmonClient = narodmonClient;
	}
	
	@PostConstruct
	public void initService() {
		List<NarodmonDeviceDescription> devices = narodmonClient.findSensors("Samara");
		List<String> sensorIdsArray = new ArrayList<>();
		devices.forEach(device -> {
			device.getSensors().forEach(sensor -> {
				if (sensor.getType() == 1) {
					sensorIdsArray.add(sensor.getId());
				}
			});
		});
		sensorIds = sensorIdsArray.toArray(new String[sensorIdsArray.size()]);
	}
	
	public List<NarodmonSensor> readSensors() {
		return narodmonClient.readSensors(sensorIds);
	}
	
}
