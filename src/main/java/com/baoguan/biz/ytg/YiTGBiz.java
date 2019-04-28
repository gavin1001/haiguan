package com.baoguan.biz.ytg;import java.io.IOException;import java.util.HashMap;import java.util.Map;import java.util.Objects;import com.baoguan.biz.BizFlow;import com.baoguan.biz.entity.ytg.YTG_passgoods;import com.baoguan.biz.entity.ytg.YiTGBean;import com.baoguan.biz.entity.ytg.YiTG_cargos;import com.baoguan.biz.entity.ytg.YiTG_contas;import com.baoguan.biz.entity.ytg.YiTG_reQuery;import com.baoguan.tools.DataUtils;import com.baoguan.tools.HttpTransfer;import com.baoguan.tools.JsonUtils;/** * 易通关业务 * @author  zhangyang */public class YiTGBiz extends BizFlow<YiTGBean> {	private String loginCookie;		public String getLoginCookie() {		return loginCookie;	}	public void setLoginCookie(String loginCookie) {		this.loginCookie = loginCookie;	}	private String url = "https://sz.91etg.com/message/message!havePopMsg.action?dt=Mon%20Apr%2022%202019%2016:18:14%20GMT+0800%20(%E4%B8%AD%E5%9B%BD%E6%A0%87%E5%87%86%E6%97%B6%E9%97%B4)";		private String dataUrlString = "https://sz.91etg.com:8087/etgmanifestplatform/mfPrepare/list.action?"+loginCookie;		    private String getRandom(){        return String.valueOf(Math.random());    }//    private Map<String,String> getHeader(){//        Cookie cookie = new YTGCookie();//        ((YTGCookie) cookie).setCookie("");//        cookie.getHeaderMap();////        return cookie.getHeaderMap();//    }    public Map<String,String> getHeader(){        Map<String,String> headerMap = new HashMap<>();//        headerMap.put("Cookie","JSESSIONID=D70F7B86AD9F3CCA28628526B99E60CA.jvm751");        headerMap.put("Cookie","JSESSIONID=521404FB5C197B53CABA9E6BC738A009");        headerMap.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");        headerMap.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");        headerMap.put("Accept-Encoding","gzip, deflate, br");        headerMap.put("Accept-Language","zh-CN,zh;q=0.9");        return headerMap;    }    /**     * @Author zhangyang     * @Description //集装箱检索     * @Date 11:04 2019/3/2     * @param condation     * @param headerMap     * @return java.lang.String     **/    public YiTG_reQuery getBerthByBoxNo(String condation, Map<String,String> headerMap){        String url = "https://sz.91etg.com:8087/etgmanifestplatform/mfPrepare/searchByContaNo.action";        String a = getRandom();        String _condation = condation;        String con = "a="+a+"&contid="+condation;        headerMap = this.getHeader();        headerMap.put("Cookie",this.getUrlHeader());        String data  = HttpTransfer.httpGet(url,con,headerMap);        System.out.println(data);        YiTG_reQuery yi = JsonUtils.jsonToObject(data, YiTG_reQuery.class);        if(yi==null){        	log.info("壹通关--无法查询该集装箱号");            return yi;        }        YiTG_reQuery yia = this.getBghSection(yi.getDocSender(), headerMap);        DataUtils.copySimpleObject(yia, yi);        return yi;    }    /**     * @Author zhangyang     * @Description //获取其他的信息 ，如，startSectionNo等 (由于在打开网页的时候需要从两个请求中获取全部信息)     * @Date 11:19 2019/4/22     * @param agentid     * @param headerMap     * @return com.baoguan.biz.entity.YiTG_reQuery     **/    public YiTG_reQuery getBghSection(String agentid,Map<String,String> headerMap){        String url = "https://sz.91etg.com:8087/etgmanifestplatform/mfPrepare/getBghSection.action";        String a = getRandom();        String condation = "?a="+a+"&agency="+agentid;        headerMap = this.getHeader();        String data = HttpTransfer.httpGet(url+condation, null, headerMap);        return JsonUtils.jsonToObject(data, YiTG_reQuery.class);    }    /**     * @Author zhangyang     * @Description // 通过该请求返回查询数据的jsessionid,该处是拿着登录后的sessionid换取可以取数据的sessionid     * @Date 11:05 2019/3/2     * @param     * @return java.lang.String     **/    public String getUrlHeader(){        Map<String,String> headerMap = getHeader();        String sessionID = headerMap.get("Cookie");        String url = "https://sz.91etg.com:8087/etgmanifestplatform/mfPrepare/list.action?ucSessionId="+sessionID.substring(11,sessionID.length());        String data  = null;        try {            data = HttpTransfer.getCookieByGet(url,null,this.getHeader());        } catch (IOException e) {            e.printStackTrace();        }        log.info("兑换后的session："+data);        return data.split(";")[0];    }    /**     * @Author zhangyang     * @Description //获取卸货港的数据，该处有5万多条，目前按照原始页面请求发送的，以后可以先请求查看页数，然后再请求所有页     * @Date 11:07 2019/3/2     * @param     * @return java.util.List<com.baoguan.biz.ytg.entity.YTG_passgoods>     **/    public YTG_passgoods getPassGoods(){        String con = this.getRandom();        String url = "https://sz.91etg.com:8087/etgmanifestplatform/locationToInfo/getData.action?a="+con;        Map<String,String> dataMap = new HashMap<>();        dataMap.put("page","1");        dataMap.put("rows","15");        String data = HttpTransfer.doPost(url,this.getHeader(),dataMap);        System.out.println(data);        YTG_passgoods list = JsonUtils.jsonToObject(data,YTG_passgoods.class);        return list;    }    /**     * @Author zhangyang     * @Description //修改的连接     * @Date 11:11 2019/3/2     * @param dataMap     * @return void     **/    public void update(Map<String,String> dataMap){        String url = "https://sz.91etg.com:8087/etgmanifestplatform/mfPrepare/update.action";        String data = HttpTransfer.doPost(url,this.getHeader(),dataMap);    }    /**     * @Author zhangyang     * @Description //新增     * @Date 12:47 2019/3/6     * @param dataMap     * @return void     * 没有返回id 在插入成功之后，需要查询一次该id多少     *      **/    public void insertD(YiTGBean yi){        String url = "https://sz.91etg.com:8087/etgmanifestplatform/mfPrepare/insert.action";        Map<String,String> dataMap = new HashMap<String, String>();        dataMap.put("mfPrepare.preSaveTime","");        dataMap.put("mfPrepare.shipNameEn:","");        dataMap.put("mfPrepare.shipId","");        dataMap.put("mfPrepare.declareOutDate","");        dataMap.put("mfPrepare.mfPrepareBill.loadingDate","");        dataMap.put("mfPrepare.voyNo","");        dataMap.put("mfPrepare.docSender","");        dataMap.put("mfPrepare.imocode","");        dataMap.put("mfPrepare.mfPrepareBill.dischPort","");        dataMap.put("mfPrepare.mfPrepareBill.shipAgencyCode","");        dataMap.put("mfPrepare.mfPrepareBill.carrierId","");        dataMap.put("mfPrepare.deptCustCodeCnName","");        dataMap.put("mfPrepare.deptCustCode","");        dataMap.put("mfPrepare.cutoffDate","");        dataMap.put("mfPrepare.billId","");        dataMap.put("shipmentBillIdVo.year","");        dataMap.put("shipmentBillIdVo.monthAndDay","");        dataMap.put("shipmentBillIdVo.lastFourNo","");        dataMap.put("mfPrepare.mfPrepareBill.shipmentBillId","");        dataMap.put("mfPrepare.mfPrepareBill.customsId","");        dataMap.put("shipmentBillIdVo.isAuthorize","");        dataMap.put("shipmentBillIdVo.isAutoSectionNo","");        dataMap.put("mfPrepare.mfPrepareBill.bookingnum","");        dataMap.put("mfPrepare.declType","");        dataMap.put("mfPrepare.mfPrepareBill.tradeMode","");        dataMap.put("mfPrepare.mfPrepareBill.tradeModeName","");        dataMap.put("mfPrepare.mfPrepareBill.ownerCodeSelect","");        dataMap.put("mfPrepare.mfPrepareBill.ownerCodeValue","");        dataMap.put("mfPrepare.mfPrepareBill.ownerCode","");        dataMap.put("mfPrepare.mfPrepareBill.ownerName","");        dataMap.put("mfPrepare.mfPrepareBill.ownerAddress","");        dataMap.put("mfPrepare.mfPrepareBill.ownerCommCode","");        dataMap.put("mfPrepare.mfPrepareBill.ownerPhone","");        dataMap.put("mfPrepare.mfPrepareBill.consigneeCodeSelect","");        dataMap.put("mfPrepare.mfPrepareBill.consigneeCodeValue","");        dataMap.put("mfPrepare.mfPrepareBill.consigneeCode","");        dataMap.put("mfPrepare.mfPrepareBill.consignee","");        dataMap.put("mfPrepare.mfPrepareBill.consigneeAddress","");        dataMap.put("mfPrepare.mfPrepareBill.consigneeCommCode","");        dataMap.put("mfPrepare.mfPrepareBill.consigneePhone","");        dataMap.put("mfPrepare.mfPrepareBill.notifyCodeSelect","");        dataMap.put("mfPrepare.mfPrepareBill.notifyCodeValue","");        dataMap.put("mfPrepare.mfPrepareBill.notifyCode","");        dataMap.put("mfPrepare.mfPrepareBill.notifyParty","");        dataMap.put("mfPrepare.mfPrepareBill.notifyAddress","");        dataMap.put("mfPrepare.mfPrepareBill.notifyCommCode","");        dataMap.put("mfPrepare.mfPrepareBill.notifyPhone","");        dataMap.put("mfPrepare.mfPrepareBill.notifyCountryCode","");        dataMap.put("mfPrepare.mfPrepareBill.ifRefundDuty","");        dataMap.put("mfPrepare.firstArriveDock","");        dataMap.put("mfPrepare.mfPrepareBill.consigneeCountryCode","");        dataMap.put("mfPrepare.mfPrepareBill.deliveryCode","");        dataMap.put("mfPrepare.mfPrepareBill.packNo","");        dataMap.put("mfPrepare.mfPrepareBill.wrapType","");        dataMap.put("mfPrepare.mfPrepareBill.wrapTypeName","");        dataMap.put("mfPrepare.mfPrepareBill.grossWt","");        dataMap.put("mfPrepare.mfPrepareBill.ifundg","");        dataMap.put("mfPrepare.mfPrepareBill.undgcontactname","");        dataMap.put("mfPrepare.mfPrepareBill.undgcontacttel","");        dataMap.put("mfPrepare.mfPrepareBill.subBillId","");        dataMap.put("mfPrepare.mfPrepareBill.cargoStatusCode","");        dataMap.put("mfPrepare.mfPrepareBill.consignorAeo","");        dataMap.put("mfPrepare.mfPrepareBill.consigneeAeo","");        dataMap.put("mfPrepare.mfPrepareBill.chargeMethodCode","");        dataMap.put("mfPrepare.mfPrepareBill.carrigeContractCode","");        dataMap.put("mfPrepare.mfPrepareBill.ifBulk","");        dataMap.put("mfPrepare.mfPrepareBill.ifColdStorage","");        dataMap.put("mfPrepare.mfPrepareBill.transStartPlace","");        dataMap.put("mfPrepare.mfPrepareBill.transTargetPlace","");        dataMap.put("mfPrepare.mfPrepareBill.volume","");        dataMap.put("contaSeqNo","");        int i = 0;        for(YiTG_contas contas:yi.getContas()){            dataMap.put("mfPrepare.mfPrepareContas["+i+"].contaId","");            dataMap.put("mfPrepare.mfPrepareContas["+i+"].contaType","");            dataMap.put("mfPrepare.mfPrepareContas["+i+"].contaTypeCnName","");            dataMap.put("mfPrepare.mfPrepareContas["+i+"].fle","");            dataMap.put("mfPrepare.mfPrepareContas["+i+"].fleCnName","");            dataMap.put("mfPrepare.mfPrepareContas["+i+"].sealNoCa","");            dataMap.put("mfPrepare.mfPrepareContas["+i+"].sealType","");            dataMap.put("mfPrepare.mfPrepareContas["+i+"].sealCode","");            dataMap.put("mfPrepare.mfPrepareContas["+i+"].sealCodeCnName","");            dataMap.put("mfPrepare.mfPrepareContas["+i+"].contaSupplierType","");            dataMap.put("mfPrepare.mfPrepareContas["+i+"].contaSupplierTypeCnName","");            i++;        }        i=0;        for(YiTG_cargos cargos:yi.getCargos()){            dataMap.put("mfPrepare.mfPrepareCargos["+i+"].cargoSeqNo","");            dataMap.put("mfPrepare.mfPrepareCargos["+i+"].mainGoodsName","");            dataMap.put("mfPrepare.mfPrepareCargos["+i+"].packNo","");            dataMap.put("mfPrepare.mfPrepareCargos["+i+"].wrapType","");            dataMap.put("mfPrepare.mfPrepareCargos["+i+"].grossWt","");            dataMap.put("mfPrepare.mfPrepareCargos["+i+"].whatConta","");            dataMap.put("mfPrepare.mfPrepareCargos["+i+"].mark","");            dataMap.put("mfPrepare.mfPrepareCargos["+i+"].undgcode","");            i++;        }        i=0;        String reData = HttpTransfer.doPost(url,this.getHeader(),dataMap);    }    /**     * @Author zhangyang     * @Description //申报得请求     * @Date 11:17 2019/3/2     * @param firmId     * @return void     **/    public void sendSB(String firmId){        String url = "https://sz.91etg.com:8087/etgmanifestplatform/mfPrepare/send.action?mfPrepare.id=20004981844";        Map<String,String> dataMap = new HashMap<>();        dataMap.put("mfPrepare.id",firmId);        String data = HttpTransfer.doPost(url,this.getHeader(),dataMap);    }    public static void main(String[] args) {        YiTGBiz t = new YiTGBiz();//        t.getPassGoods();        t.getUrlHeader();//        t.getBerthByBoxNo("YMLU8772365",null);    }        	@Override	protected String getCookie() {		return loginCookie;	}	@Override	protected boolean checkDel(YiTGBean data) {		// TODO Auto-generated method stub		return true;	}	@Override	protected boolean checkUpd(YiTGBean data) {		// TODO Auto-generated method stub		return true;	}	@Override	protected boolean checkSend(YiTGBean data) {		// TODO Auto-generated method stub		return true;	}	private boolean check1(YiTGBean data) {		String idString = data.getShipmentBillId();		String url = "https://sz.91etg.com:8087/etgmanifestplatform/mfPrepare/checkShipmentBillIdUnique.action?a="+this.getRandom()+"&shipmentBillId="+idString;		String reDate = HttpTransfer.httpGet(url, null, this.getHeader());		if(reDate.contains("success")) {			return true;		}		return false;	}		private boolean check2(YiTGBean data) {		String idString = data.getShipmentBillId();				StringBuffer _contaNos =  new StringBuffer();		for(YiTG_contas contas:data.getContas()) {			_contaNos.append(contas.getContaId());			_contaNos.append(",");		}		String contaNos = _contaNos.substring(0, _contaNos.length()-2);				String url = "https://sz.91etg.com:8087/etgmanifestplatform/mfPrepare/checkContaInfo.action?a="+this.getRandom()+"&contaNos="+contaNos+"&mfPrepare.shipNameEn="+data.getShipNameEn()+"&mfPrepare.voyNo="+data.getVoyNo();		String reDate = HttpTransfer.httpGet(url, null, this.getHeader());		if(reDate!=null || "".equals(reDate)) {			return true;		}		return false;	}				@Override	protected boolean checkIns(YiTGBean data) {										return true;	}	@Override	public int insertData(YiTGBean data) {		this.insertD(data);		return 0;	}	@Override	public int sendData(YiTGBean data) {								return 0;	}	@Override	public int updateData(YiTGBean data) {		// TODO Auto-generated method stub		return 0;	}	@Override	public int deleteData(YiTGBean data) {		// TODO Auto-generated method stub		return 0;	}	@Override	public boolean checkLogined() {		String dataString = HttpTransfer.httpGet(url, "", super.getHeader());		if(dataString.contains("欢迎登录")) {//登录失败执行登录			return false;		}else if(Objects.equals(dataString, "false") || Objects.equals(dataString, "true")){			return true;		}else {			return false;		}			}		private void setCookie(String cookie) {		this.getHeader().put("Cookie", cookie);	}		@Override	public boolean login() {				 //调用登录得方法，返回sessionid；		setCookie(getCookie());		String dataCookie = this.getUrlHeader();		if(dataCookie!=null && dataCookie.contains(".jvm751")){			setCookie(dataCookie);			return true;		}		return false;	}	/**	 * 根据集装箱号查询	 * TODO 简单描述该方法的实现功能（可选）.  	 * @see com.baoguan.biz.BizFlow#initData(java.lang.String)	 */	@Override	protected YiTG_reQuery initData(String condation) {		return this.getBerthByBoxNo(condation,getHeader());	}}