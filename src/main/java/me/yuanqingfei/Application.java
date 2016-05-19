package me.yuanqingfei;

import com.alibaba.druid.pool.DruidDataSource;
import me.yuanqingfei.db.CityMapper;
import me.yuanqingfei.db.DataPreparation;
import me.yuanqingfei.db.pojo.City;
import me.yuanqingfei.soap.StoreClient;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.tempuri.GetQueryPHDIStoreInfoResponse;

import java.util.Set;

@SpringBootApplication
@PropertySource({"classpath:jdbc.properties"})
public class Application {
	private static Logger logger = Logger.getLogger(Application.class);

	@Value("${jdbc.url}")
	public String jdbcUrl;

	@Value("${jdbc.username}")
	public String jdbcUserName;

	@Value("${jdbc.password}")
	public String jdbcPassword;

	@Bean
	protected DataPreparation dbPer(){
		return new DataPreparation();
	}

	@Bean
	public DruidDataSource druidDataSource() {
		DruidDataSource ds = new DruidDataSource();
		ds.setUrl(jdbcUrl);
		ds.setPassword(jdbcPassword);
		ds.setUsername(jdbcUserName);
		return ds;
	}
	
	public static final String OUTPUT_FOLDER = System.getProperty("user.home") + "\\";



	@Bean
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("org.tempuri");
		return marshaller;
	}

	@Bean
	public StoreClient storeClient(Jaxb2Marshaller marshaller) {
		StoreClient client = new StoreClient();
		client.setDefaultUri("http://tempuri.org/");
		client.setMarshaller(marshaller);
		client.setUnmarshaller(marshaller);
		return client;
	}

	@Bean
	CommandLineRunner lookup(final StoreClient storeClient, final DataPreparation dataPreparation, final CityMapper cityMapper) {
		return new CommandLineRunner() {
			@Override
			public void run(String... args) throws Exception {
				GetQueryPHDIStoreInfoResponse response = storeClient.getStoreInfo();
				
				JSONObject json = new JSONObject(response.getGetQueryPHDIStoreInfoResult());
				Integer storeCount = json.getInt("StoreCount");
				Integer storeTotal = json.getInt("StoreTotal");
				Integer loopNumber = storeTotal / storeCount;
				if(storeTotal % storeCount == 0){
					loopNumber = loopNumber -1;
				}
				
				// first print
				dataPreparation.setupBeans(response.getGetQueryPHDIStoreInfoResult());
//				storeClient.printResponseToFile(response, OUTPUT_FOLDER + "stores_1.txt");

				String batchCode = json.getString("BatchCode");
				String retCode = json.getString("RetCode");
				String retDesc = json.getString("RetDesc");
				
				for(int i = 0; i < loopNumber; i ++){
					// just trigger next time
					storeClient.getRecivingResult(batchCode, retCode, retDesc);
					
					response = storeClient.getStoreInfo();
					json = new JSONObject(response.getGetQueryPHDIStoreInfoResult());
					batchCode = json.getString("BatchCode");
					retCode = json.getString("RetCode");
					retDesc = json.getString("RetDesc");

					dataPreparation.setupBeans(response.getGetQueryPHDIStoreInfoResult());
//					storeClient.printResponseToFile(response, OUTPUT_FOLDER + "stores_" + (i + 2) + ".txt");
				}

				// -------------------------- save to db ---------------

				Set<City> citySet = dataPreparation.getCitySet();
				for(City city : citySet){
					logger.info(city);
					cityMapper.insertCity(city);
				}
			}
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class);
	}
}
