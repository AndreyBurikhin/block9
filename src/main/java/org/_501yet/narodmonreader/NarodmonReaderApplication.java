package org._501yet.narodmonreader;

import java.util.List;

public class NarodmonReaderApplication {

    private static final String API_KEY = "sFMV6arPD0RrP";

    private static final String UUID = "cfcd208495d565ef66e7dff9f98764da";

    public static void main(String[] args) {
        NarodmonClient narodmonClient = new NarodmonClient(API_KEY, UUID);
        // List<NarodmonSensor> sensors = narodmonClient.readSensors("10036");
        // sensors.forEach(sensor -> {
        // System.out.println("Id: " + sensor.getId() + " value: " +
        // sensor.getValue());
        // });

        List<NarodmonDeviceDescription> sensors = narodmonClient.findSensors("Samara");
        sensors.forEach(deviceDescription -> {
            System.out.print("Id: " + deviceDescription.getId() + " name: " + deviceDescription.getName() + " location: "
                    + deviceDescription.getLocation() + " sensors: ");
            deviceDescription.getSensors().forEach(sensor -> System.out.println("{ id: " + sensor.getId() + " name: " + sensor.getName() + " type: " + sensor.getType() +  " }"));
        });
        // ResponseEntity<String> responce =
        // restTemplate.exchange("http://narodmon.ru/api/sensorsNearby?lat=53.180&lng=50.089&radius=10&types=1,2&uuid=cfcd208495d565ef66e7dff9f98764da&api_key=sFMV6arPD0RrP&lang=en",
        // HttpMethod.GET, entity, String.class);
        // "http://narodmon.ru/api/sensorsValues?sensors=10036&uuid=cfcd208495d565ef66e7dff9f98764da&api_key=sFMV6arPD0RrP"

    }

}
