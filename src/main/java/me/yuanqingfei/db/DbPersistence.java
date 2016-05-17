package me.yuanqingfei.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import me.yuanqingfei.db.pojo.City;
import org.json.JSONArray;
import org.json.JSONObject;

import me.yuanqingfei.WebServiceApplication;

//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;

public class DbPersistence {
	
//	SqlSessionFactoryBean 
	
	private Set<City> citySet = new HashSet<City>();

	public DbPersistence(){
		setupBeans();
	}
	
	private void setupBeans(){
		String jsonString = getJsonString();
		
		if(jsonString == null){
			System.out.println("JSON String is empty. Stop. ");
			return;
		}
		
//		JSONObject jsonObj = JSON.parseObject(jsonString);
//		String storeListString = (String)jsonObj.get("StoreList");
//		JSONArray storeList = JSON.parseArray(storeListString);
		
		JSONObject jsonObj = new JSONObject(jsonString);
		String storeListString = (String)jsonObj.get("StoreList");
		JSONArray storeList = new JSONArray(storeListString);
		
		for(int i = 0; i < storeList.length(); i ++){
			JSONObject store = (JSONObject)storeList.get(i);

			// market
			Integer marketId = store.getInt("marketId");
			String marketName = store.getString("marketName");
			
			// city
			Integer cityId = store.getInt("cityId");
			String cityName = store.getString("cityName");
			
			// store
			String storeId = store.getString("storeId");
			String storeName = store.getString("storeName");
			String addressDetail = store.getString("addressDetail");
			String storePhone = store.getString("storePhone");

			
			City city = new City();
			city.setCityId(String.valueOf(cityId));
			city.setCityName(cityName);
			citySet.add(city);
			

		}
//		System.out.println("city size: " + citySet.size());
//		System.out.println("shop size: " + shopSet.size());
//		System.out.println("market size: " + marketSet.size());
//		System.out.println("city market size: " + cmSet.size());
//		System.out.println("city shop size: " + csSet.size());
	}

	public Set<City> getCitySet() {
		return citySet;
	}


	private String getJsonString() {
		File file = new File(WebServiceApplication.OUTPUT_FOLDER + "stores_1.txt");
		InputStreamReader isr = null;
		String jsonString = null;
		try {
			isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
			StringBuffer sbread = new StringBuffer();
			while (isr.ready()) {
				sbread.append((char) isr.read());
			}
			jsonString = sbread.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (isr != null) {
				try {
					isr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return jsonString;
	}

}
