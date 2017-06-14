package org._501yet.narodmonreader;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class NarodmonClient {

    private static final String ROOT_API_URL = "http://narodmon.ru/api/";

    private final String apiKey;

    private final String uuid;

    private final RestTemplate restTemplate;

    private final HttpHeaders headers;

    public NarodmonClient(String apiKey, String uuid) {
        this.apiKey = apiKey;
        this.uuid = uuid;
        this.restTemplate = new RestTemplate();
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.TEXT_HTML));
        restTemplate.getMessageConverters().add(mappingJackson2HttpMessageConverter);
        headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.TEXT_HTML));
        headers.add("User-Agent", "NarodmonReader");
    }

    public List<NarodmonSensor> readSensors(String... ids) {
        MultiValueMap<String, String> sensorsIds = new LinkedMultiValueMap<>();
        for (String id : ids) {
            sensorsIds.add("sensors", id);
        }
        String requestURL = buildUrl("sensorsValues", sensorsIds);
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        ResponseEntity<NarodmonSensors> responce = restTemplate.exchange(requestURL, HttpMethod.GET, entity, NarodmonSensors.class);
        if (responce.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("Unexpected status code " + responce.getStatusCodeValue());
        }
        NarodmonSensors sensorsResponce = responce.getBody();
        return sensorsResponce.getSensors();
    }
    
    public List<NarodmonDeviceDescription> findSensors(String address) {
        return findSensors(address, 0);
    }
    
    public List<NarodmonDeviceDescription> findSensors(String address, int radius) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("addr", address);
        if (radius > 0) {
            params.add("radius", String.valueOf(radius));
        }
        params.add("lang", "en");
        String requestURL = buildUrl("sensorsNearby", params);
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        
//        ResponseEntity<String> responce = restTemplate.exchange(requestURL, HttpMethod.GET, entity,
//              String.class);
//        System.out.println("Status: " + responce.getStatusCode() + " headers: " + responce.getHeaders() + " body: " + responce.getBody());
        ResponseEntity<NarodmonDeviceDescriptions> responce = restTemplate.exchange(requestURL, HttpMethod.GET, entity,
                NarodmonDeviceDescriptions.class);
        if (responce.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("Unexpected status code " + responce.getStatusCodeValue());
        }
        List<NarodmonDeviceDescription> deviceDescriptions = responce.getBody().getDevices();
        return deviceDescriptions == null ? Collections.emptyList() : deviceDescriptions;
//        return Collections.emptyList();
    }

    String buildUrl(String actionController, MultiValueMap<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(ROOT_API_URL + actionController);
        builder.queryParam("api_key", apiKey);
        builder.queryParam("uuid", uuid);
        builder.queryParams(params);
        return builder.toUriString();
    }

}
