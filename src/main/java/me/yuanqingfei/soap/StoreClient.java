package me.yuanqingfei.soap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import javax.xml.datatype.XMLGregorianCalendar;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import org.tempuri.GetQueryPHDIStoreInfo;
import org.tempuri.GetQueryPHDIStoreInfoResponse;
import org.tempuri.GetReceivingResults;
import org.tempuri.GetReceivingResultsResponse;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

public class StoreClient extends WebServiceGatewaySupport {

	private static final Logger log = LoggerFactory.getLogger(StoreClient.class);

	private static final String USER_NAME = "xxx";

	private static final String PASSWORD = "xxx";

	public GetQueryPHDIStoreInfoResponse getStoreInfo() {
		// log.info("begin call");
		GetQueryPHDIStoreInfo request = new GetQueryPHDIStoreInfo();
		request.setPhdiUser(USER_NAME);
		GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
		XMLGregorianCalendar dateNow = new XMLGregorianCalendarImpl(calendar);
		request.setPhdiCurrentDate(dateNow);
		String signMsg = createMsg(USER_NAME, PASSWORD, dateNow);
		request.setSignMsg(signMsg);

		GetQueryPHDIStoreInfoResponse response = (GetQueryPHDIStoreInfoResponse) getWebServiceTemplate()
				.marshalSendAndReceive("http://devftp.xxx.com/bohservice/PHDIWebService.asmx",
						request,
						new SoapActionCallback("http://tempuri.org/GetQueryPHDIStoreInfo"));
		return response;
	}

	public GetReceivingResultsResponse getRecivingResult(String batchCode, String retCode, String retDesc) {
		GetReceivingResults request = new GetReceivingResults();
		JSONObject jsonObj = new JSONObject();
		jsonObj.append("MethodName", "GetQueryPHDIStoreInfo");
		jsonObj.append("BatchCode", batchCode);
		jsonObj.append("RetCode", retCode);
		jsonObj.append("RetDesc", retDesc);

		request.setJson(jsonObj.toString());
		GetReceivingResultsResponse response = (GetReceivingResultsResponse) getWebServiceTemplate()
				.marshalSendAndReceive("http://devftp.xxx.com/bohservice/PHDIWebService.asmx",
						request,
						new SoapActionCallback("http://tempuri.org/GetReceivingResults"));
		return response;
	}

	private static String createMsg(String userName, String password, XMLGregorianCalendar dateNow) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String orginal = userName + password + format.format(dateNow.toGregorianCalendar().getTime());
		return DigestUtils.md5DigestAsHex(orginal.getBytes());
	}

	public void printResponseToFile(GetQueryPHDIStoreInfoResponse resp, String fileName) {
		String result = resp.getGetQueryPHDIStoreInfoResult();
		BufferedWriter writer = null;
		try {
			if (result != null) {
				File storesFile = new File(fileName);
				writer = new BufferedWriter(new FileWriter(storesFile));
				writer.append(result);
				writer.flush();
			} else {
				log.warn("No response received");
			}
		} catch (IOException e) {
			e.printStackTrace();
			log.error("io error", e);
		} finally {
			if (writer != null)
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

}
