package com.baoguan.biz.penghy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Function: <br/>
 * REASON: <br/>
 * VERSION: 4.0
 *
 * @Auther: zhangyang
 * @Date: 2019/3/5.
 */

import com.baoguan.tools.HttpTransfer;

/**
 * File Name:PENGhyBiz  
 * @Description 鹏海运
 * Date:2019/3/5 13:52  
 * @author zhangyang
 * @Version 4.0
 * Copyright (c) 2019,  All Rights Reserved.  
 */
public class PENGhyBiz {


    public static final String enco = "UTF-8";
    Map<String,String> header = new HashMap<>();
    /***
     * @Author zhangyang
     * @Description //HEADER信息 临时写入
     * @Date 9:49 2019/3/6
     * @param
     * @return java.util.Map<java.lang.String,java.lang.String>
     **/
    private Map<String,String> getHeader(){
//        header.put("Cookie","__guid=32303777.2616703916869367000.1551765057711.3918; isAnnouncementZD=17570; JSESSIONID=96E09D57D7C604BF5C80A1B249F38799; monitor_count=5");
        header.put("Cookie","JSESSIONID=CDBCBF6DFE46B45799C5F149B61EDF7A; isAnnouncementZD=17650");
        header.put("accept","text/html, application/xhtml+xml, image/jxr, */*");
        header.put("","");
        header.put("","");
        header.put("","");
        return header;
    }

    public void setLoginCookie(String cookie){
        if(header.size()==0){
            this.getHeader();
        }
        header.put("Cookie",cookie);
    }


    /**
     * @Author zhangyang
     * @Description //通过船英文名获取信息
     * @Date 9:49 2019/3/6
     * @param name
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.String>>
     **/
    public List<Map<String,String>> getDateByBoartNo(String name){
        List<Map<String,String>> list = new ArrayList<>();
        String url = "http://newmf.szedi.cn/iftsaiQuery.do?method=getIftList";
        name.replaceAll(" ","+");
        Map<String,String> dataMap = new HashMap<>();
        dataMap.put("strEnName",name);
        dataMap.put("strVoy","");
        dataMap.put("strCnName","");
        dataMap.put("strCiSeqNo","");
        dataMap.put("page","1");
        dataMap.put("operate","f");
        dataMap.put("ciSeqNoCD","");
        String s = HttpTransfer.doPost(url,this.getHeader(),dataMap,"GBK");

//        System.out.println(s);

        if(s.indexOf("用户未登录")!=-1){
            System.out.println("登录失效");
            return null;
        }
        if(s.indexOf("没有找到任何数据")!=-1){
            System.out.println("没有找到任何数据");
            return null;
        }


        Document d =  Jsoup.parse(s);

        Elements tables = d.select("tbody");
        if(tables.size()>1) {
        	Element tbody = tables.get(1);
        	Elements trs = tbody.select("tr");
        	for(int i = 0;i<trs.size();i++) {
        		if(i==0) {
        			continue;
        		}
        		Map<String,String> urlMap = new HashMap<String, String>();
        		Element tr = trs.get(i);
        		String val = tr.attr("onclick");
        		urlMap.put("imoStr", val.split(",")[5].replaceAll("'", ""));
        		Elements tds = tr.select("td");
        		urlMap.put("enName", tds.get(0).text());
        		urlMap.put("cnName", tds.get(1).text());
        		urlMap.put("imo", tds.get(2).text());
        		urlMap.put("outAirLine", tds.get(3).text());
        		urlMap.put("inTime", tds.get(4).text());
        		urlMap.put("outTime", tds.get(6).text());
        		urlMap.put("boardCompany", tds.get(7).text());
        		list.add(urlMap);
        	}
        }
//        System.out.println(tables);
        for(Map<String,String> map : list) {
        	System.out.println("========================");
        	for(Entry<String, String> set:map.entrySet()) {
        		System.out.println(set.getKey()+"----"+set.getValue());
        	}
        }
        
        return list;
    }

    /**
     * @Author zhangyang
     * @Description //获取录入页面的信息
     * @Date 9:50 2019/3/6
     * @param enName
     * @param imo
     * @param imoStr
     * @param outAirLine
     * @return java.lang.String
     **/
    public String index(String enName,String imo,String imoStr,String outAirLine) {
    	String url = "http://newmf.szedi.cn/luRu.do?method=luRuAction";
    	Map<String,String> dataMap = new HashMap<String, String>();
    	dataMap.put("btnTest", "%C8%B7%B6%A8");
    	dataMap.put("ciSeqNo", "365");
    	dataMap.put("imoStr", imoStr);
    	dataMap.put("shipNameEn", enName);
    	dataMap.put("vesselCode", imo);
    	dataMap.put("voyageNo", outAirLine);
    	
    	String s = HttpTransfer.doPost(url, this.getHeader(), dataMap,"GBK");
    	System.out.println(s);
    	return "s";
    }


    public void insert(Map<String,String> dataMap){
        String url = "http://newmf.szedi.cn/luRu.do?method=luRuSubmitAction";

        dataMap.put("billNo","11903072152");
        dataMap.put("clientSeq","432");
        dataMap.put("contaMessStr","[{}]");
        dataMap.put("date","");
        dataMap.put("isEdit","");
        dataMap.put("method","");
        dataMap.put("voy","");
        dataMap.put("vssCode","");


        String returnValue = HttpTransfer.doPost(url,this.getHeader(),dataMap);

    }

    
    public static void main(String[] args) {
        PENGhyBiz p = new PENGhyBiz();
//        List<Map<String,String>> list = p.getDateByBoartNo("JIAN GONG 188");
        List<Map<String,String>> list = p.getDateByBoartNo("COSCO SANTOS");

        Map<String,String> map = list.get(0);
        p.index(map.get("enName"), map.get("imo"), map.get("imoStr"), map.get("outAirLine"));
    }
}
