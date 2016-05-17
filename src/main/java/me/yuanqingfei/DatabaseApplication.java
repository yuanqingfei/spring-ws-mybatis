package me.yuanqingfei;

import com.alibaba.druid.pool.DruidDataSource;
import me.yuanqingfei.db.CityMapper;
import me.yuanqingfei.db.DbPersistence;
import me.yuanqingfei.db.pojo.City;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import java.util.Set;

@SpringBootApplication
@PropertySource({"classpath:jdbc.properties"})
public class DatabaseApplication{
	
	private static Logger logger = Logger.getLogger(DatabaseApplication.class);
	
    @Value("${jdbc.url}")
    public String jdbcUrl;

    @Value("${jdbc.username}")
    public String jdbcUserName;

    @Value("${jdbc.password}")
    public String jdbcPassword;
    
    @Bean
    protected DbPersistence dbPer(){
    	DbPersistence dbPer = new DbPersistence();
    	return dbPer;
    }
    
    @Bean
    public DruidDataSource druidDataSource() {
        DruidDataSource ds = new DruidDataSource();
        ds.setUrl(jdbcUrl);
        ds.setPassword(jdbcPassword);
        ds.setUsername(jdbcUserName);
        return ds;
    }
    
	@Bean
	CommandLineRunner lookup(final DbPersistence dbPersistence, final CityMapper cityMapper) {
		
		return new CommandLineRunner() {
			@Override
			public void run(String... args) throws Exception {
				Set<City> citySet = dbPersistence.getCitySet();
				for(City city : citySet){
					logger.info(city);
					cityMapper.insertCity(city);
				}
				
//				Set<Market> marketSet = dbPersistence.getMarketSet();
//				for(Market market : marketSet){
//					logger.info(market);
//					marketMapper.insertMarket(market);
//				}
				
//				Set<Shop> shopSet = dbPersistence.getShopSet();
//				for(Shop shop: shopSet){
//					logger.info(shop);
//					shopMapper.insertShop(shop);
//				}
				
//				Set<CityMarket> cmSet = dbPersistence.getCmSet();
//				for(CityMarket cm : cmSet){
//					logger.info(cm);;
//					cmMapper.insertCm(cm);
//				}
				
//				Set<CityShop> csSet = dbPersistence.getCsSet();
//				for(CityShop cs : csSet){
//					logger.info(cs);
//					csMapper.insertCs(cs);
//				}
			
			}
		};
	}

    
	public static void main(String[] args) {
		SpringApplication.run(DatabaseApplication.class, args);
	}
}
