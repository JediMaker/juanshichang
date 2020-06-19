package com.example.juanshichang.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.juanshichang.MyApp;
import com.example.juanshichang.bean.User;


/**
 * Created by Administrator on 2018/7/19/019.
 */

public class SpUtil {


    private static volatile SpUtil instance;
    private SharedPreferences sp;
    private SpUtil(Context context){
        sp=context.getSharedPreferences("Jsc", Context.MODE_PRIVATE);
    }
    public static SpUtil getIstance() {
        // 对象实例化时与否判断（不使用同步代码块，instance不等于null时，直接返回对象，提高运行效率）
        if (instance == null) {
            //同步代码块（对象未初始化时，使用同步代码块，保证多线程访问时对象在第一次创建后，不再重复被创建）
            synchronized (SpUtil.class) {
                //未初始化，则初始instance变量
                if (instance == null) {
                    instance = new SpUtil(MyApp.app);
                }
            }
        }
        return instance;
    }
    public String getString(String key, String defaultValue){
        return sp.getString(key, defaultValue);
    }
    public void setString(String key, String value){
        SharedPreferences.Editor editor=sp.edit();
        editor.putString(key,value);
        editor.commit();
    }
    public boolean getBoolean(String key, boolean defaultValue){
        return sp.getBoolean(key, defaultValue);
    }
    public void setBoolean(String key, boolean value){
        SharedPreferences.Editor editor=sp.edit();
        editor.putBoolean(key,value);
        editor.commit();
    }
    public void setUser(User entity ){
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("usertoken",entity.getUsertoken());
        editor.putLong("useruid",entity.getUseruid());
//        editor.putString("userimaccid",entity.getUserimaccid()); //todo 新添
//        editor.putString("userimtoken",entity.getUserimtoken());
//        editor.putString("userage",entity.getUserage());
        editor.putString("avatar",entity.getAvatar());
        editor.putString("nickname",entity.getNick_name());
        editor.putFloat("balance",entity.getBalance());
        editor.putString("phone_num",entity.getPhone_num());
        editor.putString("password",entity.getPassword());
        editor.putFloat("currentdaybenefit",entity.getCurrent_day_benefit());
        editor.putFloat("currentmonthbenefit",entity.getCurrent_month_benefit());
        editor.putFloat("lastdaybenefit",entity.getLast_day_benefit());
        editor.putLong("frominviteuserid",entity.getFrom_invite_userid());
        editor.putString("invitecode",entity.getInvite_code());
        editor.putString("ali_pay_account",entity.getAli_pay_account());
        editor.commit();
    }
    public User  getUser(){
        User entity=new  User();
        entity.setUsertoken(sp.getString("usertoken",""));
        entity.setUseruid(sp.getLong("useruid",0));
//        entity.setUserage(sp.getString("userage",""));
        entity.setAvatar(sp.getString("avatar",""));
        entity.setNick_name(sp.getString("nickname",""));
        entity.setPassword(sp.getString("password",""));
        entity.setPhone_num(sp.getString("phone_num",""));
        entity.setBalance(sp.getFloat("balance",0));
        entity.setCurrent_day_benefit(sp.getFloat("currentdaybenefit",0));
        entity.setCurrent_month_benefit(sp.getFloat("currentmonthbenefit",0));
        entity.setLast_day_benefit(sp.getFloat("lastdaybenefit",0));
        entity.setFrom_invite_userid(sp.getLong("frominviteuserid",0));
        entity.setInvite_code(sp.getString("invitecode",""));
        entity.setAli_pay_account(sp.getString("ali_pay_account",""));
        return entity;
    }

    /*public void setUserInfo(Member entity ){
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("username",entity.getUsername());
        editor.putString("mobile",entity.getMobile());
        editor.putString("realname",entity.getRealname());
        editor.putString("sex",entity.getSex());
        editor.putString("facepicurl",entity.getFacepicurl());
        editor.putString("paypassword",entity.getPaypassword());
        editor.putString("level",entity.getLevel());
        editor.putString("jifen",entity.getJifen());
        editor.putString("jifenic",entity.getJifenic());
        editor.putString("jifenu",entity.getJifenu());
        editor.putString("jifenuic",entity.getJifenuic());
        editor.putString("acc",entity.getAcc());
        editor.putString("accic",entity.getAccic());
        editor.putString("tixian",entity.getTixian());
        editor.putString("recharge",entity.getRecharge());
        editor.putString("inviteuserid",entity.getInviteuserid());
        editor.putString("inviteuser",entity.getInviteuser());
        editor.putString("inviteusercert",entity.getInviteusercert());
        editor.putString("isleader",entity.getIsleader());
        editor.putString("leaderdate",entity.getLeaderdate());
        editor.putString("iscert",entity.getIscert());
        editor.putString("certdate",entity.getCertdate());
        editor.putString("provinceid",entity.getProvinceid());
        editor.putString("cityid",entity.getCityid());
        editor.putString("areaid",entity.getAreaid());
        editor.putString("areafull",entity.getAreafull());
        editor.putString("regdate",entity.getRegdate());
        editor.putString("iccard",entity.getIccard());
        editor.putString("bankname",entity.getBankname());
        editor.putString("bankcode",entity.getBankcode());
        editor.putString("rebate",entity.getRebate());
        editor.putString("invitemobile",entity.getInvitemobile());
        editor.putString("nopaynum",entity.getNopaynum());
        editor.putString("nosendnum",entity.getNosendnum());
        editor.putString("noreceivenum",entity.getNoreceivenum());
        editor.putString("noevaluatenum",entity.getNoevaluatenum());
        editor.putString("finishnum",entity.getFinishnum());
        editor.putString("collectnum",entity.getCollectnum());
        editor.putString("isread",entity.getIsread());
        editor.putString("isshownotice",entity.getIsshownotice());
        editor.putString("noticeurl",entity.getNoticeurl());
        editor.putString("accurl",entity.getAccurl());
        editor.putString("rechargeurl",entity.getRechargeurl());
        editor.putString("txurl",entity.getTxurl());
        editor.putString("iurl",entity.getIurl());
        editor.putString("uurl",entity.getUurl());
        editor.putString("levelurl",entity.getLevelurl());
        editor.putString("shanghuurl",entity.getShanghuurl());
        editor.putString("teamurl",entity.getTeamurl());
        editor.putString("isshop",entity.getIsshop());
        editor.commit();
    }
    public Member  getUserInfo(){
        Member entity=new  Member();
        entity.setUsername(sp.getString("username",""));
        entity.setMobile(sp.getString("mobile",""));
        entity.setRealname(sp.getString("realname",""));
        entity.setSex(sp.getString("sex",""));
        entity.setFacepicurl(sp.getString("facepicurl",""));
        entity.setPaypassword(sp.getString("paypassword",""));
        entity.setLevel(sp.getString("level",""));
        entity.setJifen(sp.getString("jifen",""));
        entity.setJifenic(sp.getString("jifenic",""));
        entity.setJifenu(sp.getString("jifenu",""));
        entity.setJifenuic(sp.getString("jifenuic",""));
        entity.setAcc(sp.getString("acc",""));
        entity.setAccic(sp.getString("accic",""));
        entity.setTixian(sp.getString("tixian",""));
        entity.setRecharge(sp.getString("recharge",""));
        entity.setInviteuserid(sp.getString("inviteuserid",""));
        entity.setInviteuser(sp.getString("inviteuser",""));
        entity.setInviteusercert(sp.getString("inviteusercert",""));
        entity.setIsleader(sp.getString("isleader",""));
        entity.setLeaderdate(sp.getString("leaderdate",""));
        entity.setIscert(sp.getString("iscert",""));
        entity.setCertdate(sp.getString("certdate",""));
        entity.setProvinceid(sp.getString("provinceid",""));
        entity.setCityid(sp.getString("cityid",""));
        entity.setAreaid(sp.getString("areaid",""));
        entity.setAreafull(sp.getString("areafull",""));
        entity.setRegdate(sp.getString("regdate",""));
        entity.setIccard(sp.getString("iccard",""));
        entity.setBankname(sp.getString("bankname",""));
        entity.setBankcode(sp.getString("bankcode",""));
        entity.setRebate(sp.getString("rebate",""));
        entity.setInvitemobile(sp.getString("invitemobile",""));
        entity.setNopaynum(sp.getString("nopaynum",""));
        entity.setNosendnum(sp.getString("nosendnum",""));
        entity.setNoreceivenum(sp.getString("noreceivenum",""));
        entity.setNoevaluatenum(sp.getString("noevaluatenum",""));
        entity.setFinishnum(sp.getString("finishnum",""));
        entity.setCollectnum(sp.getString("collectnum",""));
        entity.setIsread(sp.getString("isread",""));
        entity.setIsshownotice(sp.getString("isshownotice",""));
        entity.setNoticeurl(sp.getString("noticeurl",""));
        entity.setAccurl(sp.getString("accurl",""));
        entity.setRechargeurl(sp.getString("rechargeurl",""));
        entity.setTxurl(sp.getString("txurl",""));
        entity.setIurl(sp.getString("iurl",""));
        entity.setUurl(sp.getString("uurl",""));
        entity.setLevelurl(sp.getString("levelurl",""));
        entity.setShanghuurl(sp.getString("shanghuurl",""));
        entity.setTeamurl(sp.getString("teamurl",""));
        entity.setIsshop(sp.getString("isshop",""));

        return entity;
    }*/

    /**
     * 删除
     * @param
     */
    public void getDelete(){
        SharedPreferences.Editor editor=sp.edit();
        editor.clear();
        editor.commit();
    }
    /**
     * 移除某个key值对应的值
     *
     * @param
     * @param fileName
     */
    public void remove(String fileName) {
        SharedPreferences.Editor editor=sp.edit();
        editor.remove(fileName);
        editor.commit();
    }

/*    public void saveAutherState(String value) {
        SharedPreferences.Editor editor=sp.edit();
        editor.putString(SHARED_KEEY_CURRENTAUTHER_STATE,value);
        editor.commit();
    }

    public String getAutherState() {
        return sp.getString(SHARED_KEEY_CURRENTAUTHER_STATE, null);
    }*/






}