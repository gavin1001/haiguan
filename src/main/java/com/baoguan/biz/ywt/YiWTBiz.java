package com.baoguan.biz.ywt;/**
 * Function: <br/>
 * REASON: <br/>
 * VERSION: 4.0
 *
 * @Auther: zhangyang
 * @Date: 2019/3/4.
 */

import com.baoguan.tools.DateUtils;
import com.baoguan.tools.HttpTransfer;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * File Name:易网通
 * @Description TODO
 * Date:2019/3/4 17:15  
 * @author zhangyang
 * @Version 4.0
 * Copyright (c) 2019,  All Rights Reserved.  
 */
public class YiWTBiz {


    private Map<String,String> getHeader(){
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Accept"  ,"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        headerMap.put("Accept-Encoding"  ,"gzip, deflate");
        headerMap.put("User-Agent"  ,"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
        headerMap.put("Cookie"  ,"JSESSIONID=E938C9F2EFE806BAF97D57C740C6E609.mf2");
//        headerMap.put(""  ,"");
//        headerMap.put(""  ,"");
//        headerMap.put(""  ,"");
//        headerMap.put(""  ,"");

        return headerMap;
    }


    private String getDate(int pos,String par){

        long time = new Date().getTime();
        long _pos = pos*24*60*60*1000;
        String date = null;
        if("+".equals(par)){
            date = DateUtils.dateFormat(new Date(time+_pos),"yyyy-MM-dd");
        }else{
            date = DateUtils.dateFormat(new Date(time-_pos),"yyyy-MM-dd");
        }
        System.out.println(date);
        return date;
    }

    public void getBoatByName(String name,String no,String agentid){
//        String url = "http://120.76.198.92:8988/mf/ship/search_ship.action?agentid=941&selectType=null";
        String url = "http://120.76.198.92:8988/mf/ship/search_ship.action?agentid="+agentid+"&selectType=null";
        Map<String,String> dataMap = new HashMap<>();
        dataMap.put("shippingInfo.evoyageNo",no);
        dataMap.put("shippingInfo.shipName",name);
        dataMap.put("shippingInfo.startDate",this.getDate(7,"-"));
        dataMap.put("shippingInfo.endDate",this.getDate(7,"+"));
        dataMap.put("page","1");
        String s = HttpTransfer.doPost(url,this.getHeader(),dataMap,"UTF-8");
        System.out.println("------"+s);
    }


    public static void main(String[] args) {
        YiWTBiz y = new YiWTBiz();
        y.getBoatByName("","","");

    }


}
