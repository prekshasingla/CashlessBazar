package com.example.prekshasingla.cashlessbazar;

public class Configuration {
    final public  static String urlRegisterUser= "http://api2.cashlessbazar.com/api/customer/register";

    final public  static String urlPayFetchToken= "http://api2.cashlessbazar.com/api/getAccessToken";
    final public  static  String urlFeatured="http://api2.cashlessbazar.com/api/product/featured-product?";
    final public  static String urlBestSelling="http://api2.cashlessbazar.com/api/product/bestselling-product?";
    final public  static String urlMostSelling="http://api2.cashlessbazar.com/api/product/mostselling-product?";

    final public static String urlRequestTransfer="http://api2.cashlessbazar.com/api/wallet/requestpayment";
    final public static String urlTransferPayment="http://api2.cashlessbazar.com/api/wallet/transferpayment";
    final public static String urlCollectPayment="http://api2.cashlessbazar.com/api/wallet/collectpayment";

    final public  static String urlWalletTransfer="http://api2.cashlessbazar.com/api/wallet/transfer";
//    final public static String urlCollectPayment="http://api2.cashlessbazar.com/api/wallet/collectpayment";

    final public static String urlCheckBalance="http://api2.cashlessbazar.com/api/wallet/checkbalance";

    final public static String urlWalletUpdate="http://api2.cashlessbazar.com/api/wallet/update";

    final public static String urlWalletHistory="http://api2.cashlessbazar.com/api/wallet/GetWallet?";

    final public static String urlUserInfo="http://api2.cashlessbazar.com/api/customer/Info";
    final public static String urlChangePassword="http://api2.cashlessbazar.com/api/customer/reset-password";

    final public static String urlRequestOtp="http://api2.cashlessbazar.com/api/customer/PasswordRecovery";
    final public static String urlForgotPassword="http://api2.cashlessbazar.com/api/customer/PasswordRecoveryConfirm";

    final public static String urlTrasferPayment="http://api2.cashlessbazar.com/api/wallet/transferpayment";

    final public static String urlSocialLogin="http://api2.cashlessbazar.com/api/customer/sociallogin";
    final public static String urlSocialRegister="http://api2.cashlessbazar.com/api/customer/socialRegister";
    final public static String urlRequestAddFund="http://api2.cashlessbazar.com/api/wallet/createfundaddrequest";
    final public static String urlAddFund="http://api2.cashlessbazar.com/api/wallet/updatefundaddrequest";

    final public static String urlUpdateToken="http://api2.cashlessbazar.com/api/customer/updatetoken";
    final public static String urlProductDescription="http://api2.cashlessbazar.com/api/product/";
    final public static String urlGetEvents="http://api2.cashlessbazar.com/api/events/list";
    final public static String urlBannersHome="http://api2.cashlessbazar.com/api/banner/List";
    final public static String urlCategories="http://api2.cashlessbazar.com/api/category/list?";

    final public static String urlPostRequirement="http://api2.cashlessbazar.com/api/post/requirement/create";
    final public static String urlSellProduct="http://api2.cashlessbazar.com/api/sell/requirement/create";

    final public static String urlCityList="http://api2.cashlessbazar.com/api/city/list";
    final public static String urlLocalityList="http://api2.cashlessbazar.com/api/location/list?cityid=";
    final public static String urlOperatorList="http://api2.cashlessbazar.com/api/opertor/list?";
    final public static String urlRecharge="http://api2.cashlessbazar.com/api/recharge";
    final public static String urlRequirementList="http://api2.cashlessbazar.com/api/requirement/list";
    final public static String urlSellRequirement="http://api2.cashlessbazar.com/api/sell/requirement/create";
    final public static String urlRequirementProductDetail="http://api2.cashlessbazar.com/api/requirment/";
    final public static String urlInterestedRequirement="http://api2.cashlessbazar.com/api/requirment/enquired/create";
    final public static String urlRedeem="http://api2.cashlessbazar.com/api/customer/redeem";
    final public static String urlCartAdd="http://api2.cashlessbazar.com/api/cart/item";
    final public static String urlCartList="http://api2.cashlessbazar.com/api/cart/item/list?cookiesId=";
}
