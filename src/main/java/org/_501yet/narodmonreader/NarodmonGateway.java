package org._501yet.narodmonreader;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

@MessagingGateway(defaultRequestChannel = "jmqttOutboundChannel")
public interface NarodmonGateway {

	void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, @Payload TemperatureMessage message);
	
}
