package com.cimcitech.cimcly.utils;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import com.cimcitech.cimcly.bean.AreaVo;
import com.cimcitech.cimcly.bean.Loginback;

import java.net.Socket;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by cimcitech on 2017/7/31.
 */

public class Config {

    public static  String  userName = "";

    public static  boolean isLeader = false;

    public static  String AppAuthStr  = "";

    public static boolean isLogin = false;

    public static Loginback loginback;

    public static AreaVo areaVo;

    public static String KEY_LOGIN_AUTO = "key_login_auto";

    public static Context context;

    public static boolean isSubordinate = false;
    public static boolean isAddMyClient = false;
    public static boolean isAddVisit = false;
    public static boolean isAddPerson = false;
    public static boolean isAddTrack = false;
    public static boolean isFollowUp = false;
    public static boolean isAddWork = false;
    public static boolean isCarInStorage = false;
    public static boolean isAlreadyInStorage = false;
    public static boolean isDepartRequest = false;
    public static boolean isQuotedPrice = false;
    public static int type = 1; //周报类型  1.今天 2.本周 3.历史

    public static final String IP = "http://test.lingyu.com:8081/ly";  //测试

    //public static final String IP = "http://zh.lingyu.com:8081/ly";  //正式

    //public static final String IP = "http://10.43.10.207:8080/ly";//本机

    //public static final String IP = "http://10.43.10.35" + ":8080/ly";

    public static final String custVisit = IP + "/custVisit/pageList"; //拜访记录

    public static final String custSubVisit = IP + "/custVisit/subPageList"; //下属的客户拜访记录

    public static final String USER_LOGIN_URL = IP + "/user/login"; //登录

    public static final String URL_CLIENT_LIST = IP + "/cust/pageList"; //我的客户

    public static final String getCustSelect = IP + "/cust/getCustSelect"; // 新增客户的选择项信息

    public static final String addCust = IP + "/cust/addCust"; //添加客户信息

    public static final String modifyCust = IP + "/cust/modifyCust"; //修改客户信息

    public static final String getCurrInfo = IP + "/cust/getCurrInfo"; //查询客户信息

    public static final String contactList = IP + "/contact/pageList"; //联系人

    public static final String subordinate = IP + "/cust/subPageList"; //下属客户

    public static final String subPageList = IP + "/contact/subPageList"; //下属联系人

    public static final String getContactInfo = IP + "/contact/getCurrInfo"; //联系人详情

    public static final String getProviceAndCity = IP + "/cate/getProviceAndCity"; //省市

    public static final String custList = IP + "/cust/list"; //获取我的客户名称列表

    public static final String addCont = IP + "/contact/addCont"; //添加联系人

    public static final String getCustVisitInfo = IP + "/custVisit/getCurrInfo"; //拜访信息

    public static final String modifyCustVisit = IP + "/custVisit/modifyCustVisit"; //修改拜访信息

    public static final String contactListData = IP + "/contact/list"; //通过custid获取联系人

    public static final String addCustVisit = IP + "/custVisit/addCustVisit"; //添加客户拜访

    public static final String opportUnitList = IP + "/opportUnit/pageList"; //意向订单list

    public static final String opportUnitSubList = IP + "/opportUnit/subPageList"; //下属意向订单list

    public static final String getOpportSelect = IP + "/opportUnit/getOpportSelect"; //添加意向订单选择项

    public static final String addOpportUnit = IP + "/opportUnit/addOpportUnit"; //添加意向订单

    public static final String opportUnitInfo = IP + "/opportUnit/getCurrInfo"; //意向订单详情

    public static final String updateOpportUnit = IP + "/opportUnit/addOpportFollow"; //跟进意向订单

    public static final String quoteBaseList = IP + "/quoteBase/pageList"; //报价单列表 分页

    public static final String opportUnitSubPageList = IP + "/quoteBase/subPageList"; //我的下属报价订单

    public static final String getEditQuoteBase = IP + "/quoteBase/getEditQuoteBase"; // 编辑报价单

    public static final String getChassis = IP + "/quoteBase/getChassis"; //获取是否自带底盘下拉值

    public static final String isCustExist = IP + "/cust/isCustExist"; //判断客户名称是否存在

    public static final String getListByOppoertId = IP + "/followRecord/getListByOppoertId"; //意向订单跟进记录

    public static final String announceList = IP + "/announce/pageList"; //获取公告列表

    public static final String announceDetail = IP + "/announce/getCurrInfo"; //获取公告详情

    public static final String workWeekly = IP + "/weeklyReport/pageList"; //工作周报

    public static final String workWeeklySub = IP + "/weeklyReport/subPageList"; //我下属的工作周报

    public static final String workWeeklyInfo = IP + "/weeklyReport/getCurrInfo"; //工作周报详情

    public static final String updateWorkWeekly = IP + "/weeklyReport/modifyWeeklyReport";//修改工作周报

    public static final String addWorkWeekly = IP + "/weeklyReport/addWeeklyReport"; //添加工作周报

    public static final String orderContract = IP + "/sContOrder/pageList"; //获取订单合同

    public static final String orderContractSub = IP + "/sContOrder/subPageList"; //获取下属订单合同

    public static final String orderContractInfo = IP + "/sContOrder/getCurrInfo";//获取订单合同详情

    public static final String modifyQuoteBase = IP + "/quoteBase/modifyQuoteBase"; //编辑 订单

    public static final String getNewQuoteBase = IP + "/quoteBase/getNewQuoteBase"; //报价单新增缺省值

    public static final String addQuoteBase = IP + "/quoteBase/addQuoteBase"; //新增报价单

    public static final String quoteBaseSumbit = IP + "/quoteBase/sumbit"; //提交报价单

    public static final String getOrderStandard = IP + "/sContOrder/getOrderStandard"; //订单合同的技术规格确认书

    public static final String sContOrderCancel = IP + "/sContOrder/cancel"; //终止订单合同

    public static final String sContOrderSubmit = IP + "/sContOrder/submit"; //提交订单合同

    public static final String sContOrderSave = IP + "/sContOrder/modfiySContOrder"; //修改订单合同

    public static final String getQuestionType = IP + "/codeValue/getQuestionType";//问题反馈类型

    public static final String addFeedback = IP + "/feedback/addFeedback"; //添加意见反馈

    public static final String getReportType = IP + "/codeValue/getReportType"; //工作类型缺省值

    public static final String updateApk = IP + "/appUpdate/getNewAppUpdate"; //更新apk

    public static final String modifyCont = IP + "/contact/modifyCont"; //修改联系人信息

    public static final String getQuoteStatus = IP + "/codeValue/getQuoteStatus"; //获取报价单状态缺省值

    public static final String getCurrStageSelect = IP + "/codeValue/getCurrStageSelect"; //获取意向跟踪状态缺省值

    public static final String getContStatus = IP + "/codeValue/getContStatus"; //获取合同状态缺省值

    public static final String PaymentUrl = IP + "/sContOrder/queryOrderDelivery"; //回款跟踪

    public static final String PaymentDetailUrl = IP + "/recon/getListByOrderId";
    //回款跟踪详情

    public static final String subPaymentUrl = IP + "/sContOrder/querySubOrderDelivery";
    //我下属的回款跟踪

    public static final String productionUrl = IP + "/prodOrder/pageList";//生产进度

    public static final String subProductionUrl = IP + "/prodOrder/subPageList";//我的下属的生产进度

    public static final String productionDetailUrl = IP + "/prodOrder/getCurrInfo";//生产进度详情

    public static final String getDiffAreaOrder = IP + "/opportUnit/rptRegionOpportCount";
    //获取不同区域的意向订单

    public static final String getDiffProductOrder = IP + "/opportUnit/rptProdOpportCount";
    //获取不同产品的意向订单

    public static final String getAreaContractAmount = IP + "/sContOrder/rptRegionScont";
    //不同区域的合同数量统计

    public static final String getProductContractAmount = IP + "/sContOrder/rptProdScont";
    //不同产品年度合同数统计

    public static final String getProvinceTop10 = IP + "/sContOrder/rptFirstProviceScont";
    //合同总额前10的省份合同数量统计

    public static final String getCustomerTop10 = IP + "/sContOrder/rptFirstCustScont";
    //合同总额前10的客户合同数量统计

    public static final String getContractDetailList = IP + "/sContOrder/rptPageList";
    //获取报表中合同详情列表

    public static final String rptOpportUnitList = IP + "/opportUnit/rptPageList"; //不同区域/产品的意向订单详细

    public static final String closeOpportUnit = IP + "/opportUnit/close";
    //“意向跟踪”中，关闭记录

    public static final String waitInStorageList = IP + "/prodOrder/getNotInWarehouse";// 待入库清单

    public static final String waitInStorageAction = IP + "/prodOrder/updateInWarehouse";// 入库操作

    public static final String alreadyInStorageList = IP + "/prodOrder/getInWarehouseList";// 已入库清单

    public static final String outStorageAction = IP + "/prodOrder/reFund";//退库操作

    public static final String departRequestAction = IP + "/prodOrder/addDepatureApply";// 发车申请

    public static final String alreadyRequestList = IP + "/prodOrder/getDepatureList";// 已申请发车清单

    public static final String carOutFactoryAction = IP + "/prodOrder/outgate";// 车辆出厂动作

    public static final String getVehicleInfo = IP + "/prodOrder/getVehicleInfo";// 获取车工号当前工序

    public static final String submitTestResult = IP + "/prodOrder/checkOutProduction";// 提交检验结果
}
