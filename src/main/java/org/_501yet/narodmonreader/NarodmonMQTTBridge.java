package org._501yet.narodmonreader;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.json.ObjectToJsonTransformer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;

@SpringBootApplication
@IntegrationComponentScan
@EnableScheduling
public class NarodmonMQTTBridge {
	
	private static final String TOPIC_NAME = "sdcps/weather/russia/samara/";

	@Value("${mqtt.client.name:Weather-gateway}")
	private String clientName;

	@Value("${mqtt.client.username:}")
	private String clientUsername;

	@Value("${mqtt.client.password:}")
	private String clientPassword;

	@Value("${mqtt.client.server.URIs:tcp://127.0.0.1:1883}")
	private String[] serverURIs = {};
	
	@Autowired
	private NarodmonGateway narodmonGateway;
	
	@Autowired
	private NarodmonService narodmonService;
	
	@Autowired
	private FakeDataSerivce fakeDataSerivce;

	public static void main(String[] args) throws IOException {
		try (ConfigurableApplicationContext context = new SpringApplicationBuilder(NarodmonMQTTBridge.class).web(false)
		        .run(args)) {
			context.registerShutdownHook();
			while (true) {
				System.in.read();
			}
		}
	}

	@Scheduled(fixedRateString = "${gateway.update.interval:60000}")
	public void scheduleFixedRateTask() {
//		List<NarodmonSensor> sensors = narodmonService.readSensors();
//		sensors.forEach(sensor -> {
//			TemperatureMessage message = new TemperatureMessage();
//			message.setValue(Double.valueOf(sensor.getValue()));
//			narodmonGateway.sendToMqtt(TOPIC_NAME + sensor.getId(), message);
//		});
		List<NarodmonSensor> sensors = fakeDataSerivce.readSensors();
		sensors.forEach(sensor -> {
			TemperatureMessage message = new TemperatureMessage();
			message.setValue(Double.valueOf(sensor.getValue()));
			message.setTime(sensor.getTime());
			narodmonGateway.sendToMqtt(TOPIC_NAME + sensor.getId(), message);
		});
	}
	
	@Bean
	public NarodmonService narodmonService(NarodmonClient narodmonClient) {
		return new NarodmonService(narodmonClient);
	}
	
	@Bean
	public NarodmonClient narodmonClient(@Value("${narodmon.client.api.key:}") String apiKey, @Value("${narodmon.client.uuid:}") String uuid) {
		return new NarodmonClient(apiKey, uuid);
	}
	
	
	@Bean
	public FakeDataSerivce fakeDataSerivce() {
		return new FakeDataSerivce();
	}

	@Bean
	public MqttPahoClientFactory mqttClientFactory() {
		DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
		factory.setServerURIs(serverURIs);
		if (!StringUtils.isEmpty(clientUsername)) {
			factory.setUserName(clientUsername);
			factory.setPassword(clientPassword);
		}
		return factory;
	}

	@Bean
	@ServiceActivator(inputChannel = "mqttOutboundChannel")
	public MessageHandler mqttOutbound() {
		MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(clientName, mqttClientFactory());
		messageHandler.setAsync(true);
		messageHandler.setDefaultTopic("testTopic");
		return messageHandler;
	}

	@Bean
	public MessageChannel mqttOutboundChannel() {
		return new DirectChannel();
	}
	
	@Bean
	@Transformer(inputChannel = "jmqttOutboundChannel", outputChannel = "mqttOutboundChannel")
	public org.springframework.integration.transformer.Transformer dfd() {
		return new ObjectToJsonTransformer();
	}

}
