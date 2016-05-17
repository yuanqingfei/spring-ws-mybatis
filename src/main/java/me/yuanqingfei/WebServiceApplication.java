package me.yuanqingfei;

import me.yuanqingfei.soap.StoreClient;
import org.json.JSONObject;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.tempuri.GetQueryPHDIStoreInfoResponse;

@SpringBootApplication
public class WebServiceApplication {
	
	public static final String OUTPUT_FOLDER = System.getProperty("user.home") + "\\";

	public static void main(String[] args) {
		SpringApplication.run(WebServiceApplication.class);
	}
	
	@Bean
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("org.tempuri");
		return marshaller;
	}

	@Bean
	public StoreClient weatherClient(Jaxb2Marshaller marshaller) {
		StoreClient client = new StoreClient();
		client.setDefaultUri("http://tempuri.org/");
		client.setMarshaller(marshaller);
		client.setUnmarshaller(marshaller);
		return client;
	}

	@Bean
	CommandLineRunner lookup(final StoreClient storeClient) {
		return new CommandLineRunner() {
			@Override
			public void run(String... args) throws Exception {
				GetQueryPHDIStoreInfoResponse response = storeClient.getStoreInfo();
				
				JSONObject json = storeClient.getJsonObjectFromResponse(response);
				Integer storeCount = json.getInt("StoreCount");
				Integer storeTotal = json.getInt("StoreTotal");
				Integer loopNumber = storeTotal / storeCount;
				if(storeTotal % storeCount == 0){
					loopNumber = loopNumber -1;
				}
				
				// first print
				storeClient.printResponseToFile(response, OUTPUT_FOLDER + "stores_1.txt");

				String batchCode = json.getString("BatchCode");
				String retCode = json.getString("RetCode");
				String retDesc = json.getString("RetDesc");
				
				for(int i = 0; i < loopNumber; i ++){
					// just trigger next time
					storeClient.getRecivingResult(batchCode, retCode, retDesc);
					
					response = storeClient.getStoreInfo();
					json = storeClient.getJsonObjectFromResponse(response);
					batchCode = json.getString("BatchCode");
					retCode = json.getString("RetCode");
					retDesc = json.getString("RetDesc");
					
					storeClient.printResponseToFile(response, OUTPUT_FOLDER + "stores_" + (i + 2) + ".txt");
				}
			}
		};
	}

}
