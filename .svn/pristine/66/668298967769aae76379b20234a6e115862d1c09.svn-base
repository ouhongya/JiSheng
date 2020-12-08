package com.fh.controller.app;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fh.controller.base.BaseController;
import com.fh.dao.DaoSupport;
import com.fh.entity.Page;
import com.fh.entity.app.*;
import com.fh.entity.system.Menu;
import com.fh.entity.system.Role;
import com.fh.entity.system.RoleApp;
import com.fh.service.app.TaskService;
import com.fh.service.app.function.FunctionService;
import com.fh.service.app.unit.UnitService;
import com.fh.service.system.menu.MenuService;
import com.fh.service.system.role.RoleService;
import com.fh.service.system.user.UserService;
import com.fh.service.system.userphoto.UserPhotoManager;
import com.fh.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 客户端接口文档
 */
@RestController
@RequestMapping("/api/v1")
@Api(tags = "ct接口管理")
public class ApiController extends BaseController {
    public static void main(String[] args) {
        System.out.println(new SimpleHash("SHA-1", "ouhong1", "17736898261").toString());
    }

    @Resource(name = "userService")
    private UserService userService;

    @Autowired
    TaskService taskService;

    @Resource(name = "userphotoService")
    private UserPhotoManager userphotoService;

    @Resource(name = "roleService")
    private RoleService roleService;

    @Resource(name = "menuService")
    private MenuService menuService;

    @Resource(name = "functionService")
    private FunctionService functionService;
    @Resource(name = "daoSupport")
    private DaoSupport dao;

    @Resource(name = "redisTemplate")
    private RedisTemplate redisTemplate;


    @Resource(name="unitService")
    private UnitService unitService;

    /**
     * 手机端用户登录
     *
     * @throws Exception
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ApiOperation("用户登陆")
    public ResultModel<PageData> userLogin() throws Exception {
        PageData pdfirst = new PageData();
        pdfirst=this.getPageData();
        pdfirst.put("USERNAME",pdfirst.getString("passwd"));
        PageData byUId = userService.findByUId(pdfirst);
        if(byUId == null){
            return ResultModel.failure("您的账户未开户，请核实管理员或拨打028-82449088 处理！");
        }

        String passwd=pdfirst.getString("passwd");
        String code= pdfirst.getString("code");
        String IMEI=pdfirst.getString("IMEI");
        //账号密码 均用Base64解密
        //username = BASE64peUtil.decode(username);
        //passwd = BASE64Util.decode(passwd);
        //加密密码
        if (redisTemplate.hasKey(passwd)) {
            //取到验证码
            String str = (String) redisTemplate.opsForValue().get(passwd);
            if (!str.equals(code)) {
                return ResultModel.failure("验证码不正确");
            }
        } else {
            return ResultModel.failure("验证码不存在");
        }
        passwd = new SimpleHash("SHA-1", pdfirst.getString("username"), passwd).toString();
        PageData pd = new PageData();
        pd.put("USERNAME", pdfirst.getString("passwd"));
        pd.put("PASSWORD", passwd);
        PageData userPd = userService.userLoginMobile(pd);
        if (userPd != null) {
            String imei = userPd.getString("IMEI");
            if (imei == null || "".equals(imei)) {
                PageData userPdIMEI = new PageData();
                userPdIMEI.put("IMEI", IMEI);
                userPd.put("IMEI", userPdIMEI.getString("IMEI"));
                userPdIMEI.put("USER_ID", userPd.getString("USER_ID"));
                userService.editIMEI(userPdIMEI);
            } else {
                boolean answer = imei.equals(IMEI);
                if (!answer) {
                    return ResultModel.failure("请使用初次登陆的设备登陆");
                }
            }
            PageData userproject = new PageData();
            userproject.put("USER_ID", userPd.getString("USER_ID"));
            PageData pdPhoto = userphotoService.findById(userPd);
            //用户头像
            userPd.put("HEAD_IMG", null == pdPhoto ? "static/ace/avatars/user.jpg" : pdPhoto.getString("PHOTO1"));
            String ROLE_ID = userPd.getString("ROLE_ID");
            PageData objectByIdpj = roleService.findObjectByIdpj(userPd);
            userPd.put("functionid",objectByIdpj.getString("FUNCTION_ID"));
            //根据角色ID获取角色对象
            Role role = roleService.getRoleById(ROLE_ID);
            //取出本角色菜单权限
            String roleRights = role.getRIGHTS();
            //获取所有菜单
            List<Menu> menuList = menuService.listAllMenu();
            List<Menu> menus = new ArrayList<Menu>();
            //测试
            for (Menu menu : menuList) {
                menu.setHasMenu(RightsHelper.testRights(roleRights, menu.getMENU_ID()));
                if (menu.isHasMenu()) {
                    List<Menu> subMenuList = menu.getSubMenu();
                    for (Menu sub : subMenuList) {
                        sub.setHasMenu(RightsHelper.testRights(roleRights, sub.getMENU_ID()));
                        if(sub.isHasMenu()){
                            menus.add(sub);
                        }
                    }
                }
            }
            userPd.put("menuList", menus);
            //测试
            return ResultModel.success("登陆成功", userPd);
        } else {
            return ResultModel.failure("用户名或者密码错误");
        }

    }


    @RequestMapping({"/getSmsCaptcha"})
    @ApiOperation("手机登录获取验证码")
    public ResultModel<String> getSmsCaptcha() throws Exception {
        PageData pdfirst = this.getPageData();
        String passwd=pdfirst.getString("passwd");
        boolean b = Tools.checkMobileNumber(passwd);
        if (b) {
            String str = null;
            String code = String.valueOf((int) ((Math.random() * 9 + 1) * 1000));
            String status = SmsUtil.sendSms1(passwd, code).trim().replaceAll("\n", "");
            switch (status) {
                case "100":
                    redisTemplate.opsForValue().set(passwd, code, 15, TimeUnit.MINUTES);
                    str=("短信发送成功");
                    break;
                case "102":
                    str=("手机号码格式不正确");
                    break;
                case "108":
                    str=("手机号码发送太频繁;请稍后再试");
                    break;
                case "111":
                    str=("当前时间段禁止短信发送");
                    break;
                case "120":
                    str=("系统升级");
                    break;
                default:
                    str=("系统繁忙请稍后再试");
                    break;
            }
            return ResultModel.success(str);
        } else {
            return ResultModel.failure("请输入正确的手机号");
        }
    }

    /**
     * 显示用户列表(用户组)
     */
    @RequestMapping({"/listUsers"})
    @ApiOperation("用户列表(用户组)")
    public ResultModel<Page> listUsers(Page page)throws Exception{
        PageData pd = new PageData();
        pd = this.getPageData();

        String USERNAME = pd.getString("USERNAME");

        if(null != USERNAME && !"".equals(USERNAME)){
            USERNAME = USERNAME.trim();
            pd.put("USERNAME", USERNAME);
        }
        page.setPd(pd);
        List<PageData>	userList = userService.listPdPageUserpj(page);			//列出验收评价用户列表
        page.setPageDataList(userList);
        return ResultModel.success(page);
    }

    /**
     * 保存用户
     */
    @RequestMapping({"/saveU"})
    @ApiOperation("保存用户")
    public ResultModel<PageData> saveU() throws Exception{
        PageData pageData = new PageData();
        pageData=this.getPageData();
        List<String> names=new ArrayList<>();
        String accountList=pageData.getString("accountList");
        List<Account> accounts = new ArrayList<>();
        JSONArray jsonArray=JSONArray.parseArray(accountList);
        for (Object jsonObject : jsonArray) {
            Account platformModel = JSONObject.parseObject(jsonObject.toString(), Account.class);
            accounts.add(platformModel);
        }

        for(Account account:accounts){
            PageData pd=new PageData();
            pd.put("USERNAME",account.getPhone());
            pd.put("ROLE_ID",account.getRoleid());
            pd.put("PHONE",account.getPhone());
            pd.put("USER_ID", this.get32UUID());	//ID
            pd.put("NUMBER","");
            pd.put("RIGHTS", "");					//权限
            pd.put("LAST_LOGIN", "");				//最后登录时间
            pd.put("IP", "");						//IP
            pd.put("STATUS", "0");					//状态
            pd.put("SKIN", "default");				//默认皮肤
            pd.put("BZ", "");//备注
            pd.put("EMAIL", "");//EMAIL
            pd.put("NAME", account.getUsername());//姓名
            pd.put("PASSWORD", new SimpleHash("SHA-1", pd.getString("NAME"), account.getPhone()).toString());
            if(null == userService.findByUId(pd)){
                userService.saveU(pd);
                SmsUtil.sendSms1(pd.getString("PHONE"),"账号："+pd.getString("NAME")+"开户成功!");

            }else{
                names.add(pd.getString("NAME"));
            }
        }
        pageData.put("names",names);
        return  ResultModel.success(pageData);
    }

    /**
     * 新增用户需要角色ids
     */
    @RequestMapping({"/goAddU"})
    @ApiOperation("新增用户需要角色ids")
    public ResultModel<List<RoleApp>> goAddU()throws Exception{
        PageData pd = new PageData();
        pd = this.getPageData();
        List<Role> roleList;
        roleList = roleService.listAllERRolespj();			//列出所有验收评价二级角色
        List<RoleApp> roleListApp=new ArrayList<>();
        for(Role role:roleList){
            RoleApp roleapp=new RoleApp();
            roleapp.setText(role.getROLE_NAME());
            roleapp.setValue(role.getROLE_ID());
            roleapp.setROLE_ID(role.getROLE_ID());
            roleapp.setROLE_NAME(role.getROLE_NAME());
            roleapp.setRIGHTS(role.getRIGHTS());
            roleapp.setPARENT_ID(role.getPARENT_ID());
            roleapp.setADD_QX(role.getADD_QX());
            roleapp.setDEL_QX(role.getDEL_QX());
            roleapp.setEDIT_QX(role.getEDIT_QX());
            roleapp.setCHA_QX(role.getCHA_QX());
            roleapp.setQX_ID(role.getQX_ID());
            roleListApp.add(roleapp);
        }


        return ResultModel.success(roleListApp);
    }

    /**
     * 修改用户
     */

    @RequestMapping({"/editU"})
    @ApiOperation("修改用户")
    public ResultModel<PageData> editU() throws Exception{
        PageData pd = new PageData();
        pd = this.getPageData();
        String accountList=pd.getString("accountList");
        JSONArray jsonArray=JSONArray.parseArray(accountList);
        Account platformModel =null;
        for (Object jsonObject : jsonArray) {
            platformModel = JSONObject.parseObject(jsonObject.toString(), Account.class);
        }
        pd.put("USERNAME",platformModel.getPhone());
        pd.put("ROLE_ID",platformModel.getRoleid());
        pd.put("PHONE",platformModel.getPhone());

        pd.put("NUMBER","");
        pd.put("NAME",platformModel.getUsername());
        pd.put("BZ","");
        pd.put("EMAIL","");
        pd.put("PASSWORD",platformModel.getPhone());
        if(pd.getString("PASSWORD") != null && !"".equals(pd.getString("PASSWORD"))){
            pd.put("PASSWORD", new SimpleHash("SHA-1", pd.getString("NAME"), pd.getString("PASSWORD")).toString());
        }

        PageData pagedata=userService.findByUId(pd);
        if(pagedata == null){
            userService.editU(pd);
            return ResultModel.success("success",pd);
        }else{
            String user_id = pagedata.getString("USER_ID");
            if(user_id.equals(pd.getString("USER_ID"))){
                userService.editU(pd);
                return ResultModel.success("success",pd);
            }else {
                return ResultModel.failure("修改的用户名重复！");

            }

        }

    }

    /**
     * 去修改用户页面
     */


    @RequestMapping({"/goEditU"})
    @ApiOperation("去修改用户页面")
    public ResultModel<PageData> goEditU() throws Exception{

        PageData pd = new PageData();
        pd = this.getPageData();
//        List<Role> roleList = roleService.listAllERRolespj();			//列出所有二级角色
        //USER_ID
        pd = userService.findByUiId(pd);							//根据ID读取
        Role role = roleService.getRoleById(pd.getString("ROLE_ID"));
        pd.put("ROLE_NAME",role.getROLE_NAME());
//        pd.put("roleList",roleList);
        return ResultModel.success(pd);
    }



    /**
     * 删除用户
     */
    @RequestMapping({"/deleteU"})
    @ApiOperation("删除用户")
    public ResultModel<String> deleteU(){
        //USER_ID
        PageData pd = new PageData();
        try{
            pd = this.getPageData();
            userService.deleteU(pd);
            return ResultModel.success("success");
        } catch(Exception e){
            logger.error(e.toString(), e);
            return ResultModel.failure("failed");
        }

    }


    /**
     * 清空用户IMEI
     */
    @RequestMapping({"/deleteIMEI"})
    @ApiOperation("清空用户IMEI")
    public ResultModel<String> deleteIMEI(){
        //USER_ID
        PageData pd = new PageData();
        try{
            pd = this.getPageData();
            pd.put("IMEI","");
            userService.deleteIMEI(pd);
            return ResultModel.success("清空IMEI成功！");
        } catch(Exception e){
            logger.error(e.toString(), e);
            return ResultModel.failure("清空IMEI失败！");
        }

    }


    /**
     * 导出用户信息到EXCEL
     */
    @RequestMapping({"/excel"})
    @ApiOperation("导出用户信息到EXCEL")
    public ModelAndView exportExcel(){
        PageData pd = new PageData();
        pd = this.getPageData();
        try{
            //检索条件===
            String USERNAME = pd.getString("USERNAME");
            if(null != USERNAME && !"".equals(USERNAME)){
                USERNAME = USERNAME.trim();
                pd.put("USERNAME", USERNAME);
            }
            //检索条件===
            Map<String,Object> dataMap = new HashMap<String,Object>();
            List<String> titles = new ArrayList<String>();
            titles.add("用户名"); 		//1
            titles.add("手机号码");      //2
            titles.add("角色");			//3
            titles.add("部门");			//4
            dataMap.put("titles", titles);
            List<PageData> userList = userService.listAllUserpj(pd);
            List<PageData> varList = new ArrayList<PageData>();
            for(int i=0;i<userList.size();i++){
                PageData vpd = new PageData();
                vpd.put("var1", userList.get(i).getString("USERNAME"));		//1
                vpd.put("var2", userList.get(i).getString("PHONE"));	//2
                vpd.put("var3", userList.get(i).getString("ROLE_NAME"));		//3
                PageData pd1 = new PageData();
                pd1.put("ROLE_ID",userList.get(i).getString("PARENT_ID"));
                PageData objectById = roleService.findObjectById(pd1);
                vpd.put("var4", objectById.getString("ROLE_NAME"));		//4
                varList.add(vpd);
            }
            dataMap.put("varList", varList);
            ObjectExcelView erv = new ObjectExcelView();					//执行excel操作
            ModelAndView mv = new ModelAndView();
            mv = new ModelAndView(erv,dataMap);
            return mv;
        } catch(Exception e){
            logger.error(e.toString(), e);
            return null;
        }

    }



    /**
     * 下载模版
     */
    @RequestMapping({"/downExcel"})
    @ApiOperation("下载模版")
    public HttpServletResponse downExcel(HttpServletResponse response)throws Exception{
        FileDownload.fileDownload(response, PathUtil.getClasspath() + Const.FILEPATHFILE + "Users.xls", "Users.xls");
        return response;
    }




    /**
     * 从EXCEL导入到数据库
     */
    @RequestMapping({"/readExcel"})
    @ApiOperation("从EXCEL导入到数据库")
    public ResultModel<String> readExcel(@RequestParam(value="excel",required=false) MultipartFile file) throws Exception{

        PageData pd = new PageData();
        if (null != file && !file.isEmpty()) {
            String filePath = PathUtil.getClasspath() + Const.FILEPATHFILE;								//文件上传路径
            String fileName =  FileUpload.fileUp(file, filePath, "userexcel");							//执行上传

            List<PageData> listPd = (List)ObjectExcelRead.readExcel(filePath, fileName, 2, 0, 0);	//执行读EXCEL操作,读出的数据导入List 2:从第3行开始；0:从第A列开始；0:第0个sheet

            if(listPd.size() == 0){
                return ResultModel.failure("批量导入失败！");
            }


            /*存入数据库操作======================================*/
            pd.put("RIGHTS", "");					//权限
            pd.put("LAST_LOGIN", "");				//最后登录时间
            pd.put("IP", "");						//IP
            pd.put("STATUS", "0");					//状态
            pd.put("SKIN", "default");				//默认皮肤




            /**
             * var0 :手机号码
             * var1 :角色
             * var2 :部门
             * var3 : 姓名
             */
            for(int i=0;i<listPd.size();i++){
                pd.put("USER_ID", this.get32UUID());										//ID

                String NAME = listPd.get(i).getString("var3");	//根据姓名汉字生成全拼
                pd.put("NAME", NAME);
                String USERNAME = listPd.get(i).getString("var0");	//根据姓名汉字生成全拼
                pd.put("USERNAME", USERNAME);
                if(userService.findByUId(pd) != null){										//判断用户名是否重复
                    return ResultModel.failure("用户名："+USERNAME+"重复"+",请重新导入。");

                }
                pd.put("BZ", "");								//备注
                pd.put("EMAIL", "");

                String var3 = listPd.get(i).getString("var2");
                List<Role> roles = roleService.listAllRoles();
                for(Role role:roles){
                    if(role.getROLE_NAME().equals(var3)){
                        PageData roledata=new PageData();
                        roledata.put("ROLE_ID",role.getROLE_ID());
                        List<Role> roleList = roleService.listAllRolesByPId(roledata);	//列出所有二级角色
                        for(Role erole:roleList){
                            String var2 = listPd.get(i).getString("var1");
                            if(var2.equals(erole.getROLE_NAME())){
                                pd.put("ROLE_ID", erole.getROLE_ID());	//设置角色ID为随便第一个
                            }

                        }
                    }
                }

                pd.put("NUMBER", String.valueOf(i));							//编号已存在就跳过
                pd.put("PHONE", listPd.get(i).getString("var0"));							//手机号

                pd.put("PASSWORD", new SimpleHash("SHA-1", NAME,  listPd.get(i).getString("var0")).toString());	//默认密码手机号码

                userService.saveU(pd);
            }
            /*存入数据库操作======================================*/

            return ResultModel.success("批量导入成功！");
        }
        return ResultModel.failure("批量导入失败！");

    }



    /**
     * 角色菜单列表
     */
    @RequestMapping({"/roleandrolemenu"})
    @ApiOperation("角色菜单列表")
    public ResultModel<Page> roleandrolemenu(Page page)throws Exception{
        PageData pd = new PageData();
        pd=this.getPageData();
        String ROLE_NAME=pd.getString("ROLE_NAME");
        if(null != ROLE_NAME && !"".equals(ROLE_NAME)){
            ROLE_NAME = ROLE_NAME.trim();
            pd.put("ROLE_NAME", ROLE_NAME);
        }
        String roleId = "7b26b450da954a6f81367c573bc8d84d";
        pd.put("ROLE_ID",roleId);

        page.setPd(pd);

        List<PageData> pageDatas = roleService.listAllRolesByPIdpj(page);//列出此部门的所有下级
        for(PageData pageData:pageDatas){
            List<Menu> menuList = menuService.listAllMenu();
            List<Menu> menus = new ArrayList<Menu>();
            String roleRights = pageData.getString("RIGHTS");
            //测试
            for (Menu menu : menuList) {
                menu.setHasMenu(RightsHelper.testRights(roleRights, menu.getMENU_ID()));
                if (menu.isHasMenu()) {
                    List<Menu> subMenuList = menu.getSubMenu();
                    for (Menu sub : subMenuList) {
                        sub.setHasMenu(RightsHelper.testRights(roleRights, sub.getMENU_ID()));
                        if(sub.isHasMenu()){
                            menus.add(sub);
                        }
                    }
                }
            }
            pageData.put("menus",menus);
        }
        page.setPageDataList(pageDatas);
        return  ResultModel.success(page);
    }



    /**
     * 新增角色页面数据
     */
    @RequestMapping({"/goaddroledata"})
    @ApiOperation("新增角色页面数据")
    public ResultModel<PageData> goaddroledata()throws Exception{
        Role roleById = roleService.getRoleById("7b26b450da954a6f81367c573bc8d84d");
        String roleRights = roleById.getRIGHTS();
        List<Menu> menuList = menuService.listAllMenu();
        List<Menu> menus = new ArrayList<Menu>();
        for (Menu menu : menuList) {
            menu.setHasMenu(RightsHelper.testRights(roleRights, menu.getMENU_ID()));
            if (menu.isHasMenu()) {
                List<Menu> subMenuList = menu.getSubMenu();
                for (Menu sub : subMenuList) {
                    sub.setHasMenu(RightsHelper.testRights(roleRights, sub.getMENU_ID()));
                    if(sub.isHasMenu()){
                        menus.add(sub);
                    }
                }
            }
        }
        PageData pd=new PageData();
        List<PageData>  pagedatas= new ArrayList<>();
        for(Menu menu:menus){
            PageData pdmenu=new PageData();
            pdmenu.put("MENU_ID",menu.getMENU_ID());
            pdmenu.put("name",menu.getMENU_NAME());
            pdmenu.put("checked",false);
            pdmenu.put("disabled",false);
            pagedatas.add(pdmenu);
        }


        pd.put("menus",pagedatas);


        List<PageData> pageDatas= functionService.listAll(pd);
        for(PageData  pagedata1:pageDatas){
            pagedata1.put("value",pagedata1.getString("FUNCTION_ID"));
            if(pagedata1.getString("FUNCTION_ID").equals("1")){
                pagedata1.put("text","专责");
            }else if (pagedata1.getString("FUNCTION_ID").equals("2")){
                pagedata1.put("text","组长");
            }else if(pagedata1.getString("FUNCTION_ID").equals("3")){
                pagedata1.put("text","检查员");
            }

        }


        pd.put("functions",pageDatas);
        return ResultModel.success(pd);
    }







    /**
     * 保存角色
     */
    @RequestMapping({"/saverole"})
    @ApiOperation("保存角色")
    public ResultModel<String> saverole()throws Exception{
        //menuIds ,   字符串
        //ROLE_NAME
        //menuIds
        //FUNCTION_ID
        PageData pd = new PageData();
        try{
            pd = this.getPageData();
            String roleform = pd.getString("roleform");
            RoleAppVo myclass = JSONObject.parseObject(roleform , RoleAppVo.class);// jsonStr 是String类型。
            pd.put("ROLE_NAME",myclass.getName());
            if(myclass.getRoleFunction().equals("专责")){
                pd.put("FUNCTION_ID","1");
            }else if (myclass.getRoleFunction().equals("组长")){
                pd.put("FUNCTION_ID","2");
            }else if(myclass.getRoleFunction().equals("检查员")){
                pd.put("FUNCTION_ID","3");
            }
            List<RoleCheckVo> rolecheckList = myclass.getRolecheckList();
            StringBuffer menuIds=new StringBuffer("");
            for(int i=0;i<rolecheckList.size();i++){
                if(rolecheckList.get(i).isChecked()){
                    menuIds.append(rolecheckList.get(i).getMENU_ID()+",");
                }
            }
            menuIds.replace(menuIds.length() - 1, menuIds.length(), "");
            String parent_id ="7b26b450da954a6f81367c573bc8d84d";		//父类角色id
            pd.put("ROLE_ID", parent_id);
            pd.put("PARENT_ID", "7b26b450da954a6f81367c573bc8d84d");
            menuIds.toString();
            BigInteger rights = RightsHelper.sumRights(Tools.str2StrArray("22,"+menuIds.toString()));
            pd.put("RIGHTS", rights.toString());
            pd.put("QX_ID", "");
            String UUID = this.get32UUID();
            pd.put("GL_ID", UUID);
            pd.put("FX_QX", 0);				//发信权限
            pd.put("FW_QX", 0);				//服务权限
            pd.put("QX1", 0);				//操作权限
            pd.put("QX2", 0);				//产品权限
            pd.put("QX3", 0);				//预留权限
            pd.put("QX4", 0);				//预留权限
            roleService.saveKeFu(pd);           //保存到K权限表
            pd.put("U_ID", UUID);
            pd.put("C1", 0);				//每日发信数量
            pd.put("C2", 0);
            pd.put("C3", 0);
            pd.put("C4", 0);
            pd.put("Q1", 0);				//权限1
            pd.put("Q2", 0);				//权限2
            pd.put("Q3", 0);
            pd.put("Q4", 0);
            roleService.saveGYSQX(pd);//保存到G权限表
            pd.put("QX_ID", UUID);
            pd.put("ROLE_ID", UUID);
            pd.put("ADD_QX", "0");
            pd.put("DEL_QX", "0");
            pd.put("EDIT_QX", "0");
            pd.put("CHA_QX", "0");
            roleService.addpj(pd);
            return ResultModel.success("success");
        } catch(Exception e){
            logger.error(e.toString(), e);
            return ResultModel.failure("failed");
        }

    }




    /**
     * 修改角色页面数据
     */
    @RequestMapping({"/editroledata"})
    @ApiOperation("修改角色页面数据")
    public ResultModel<PageData>  editroledata()throws Exception{
        //ROLE_ID
        PageData pd = new PageData();
        try{
            pd = this.getPageData();
            pd = roleService.findObjectByIdpj(pd);
            Role roleById = roleService.getRoleById("7b26b450da954a6f81367c573bc8d84d");
            String roleRights = roleById.getRIGHTS();
            List<Menu> menuList = menuService.listAllMenu();
            List<Menu> menus = new ArrayList<Menu>();
            for (Menu menu : menuList) {
                menu.setHasMenu(RightsHelper.testRights(roleRights, menu.getMENU_ID()));
                if (menu.isHasMenu()) {
                    List<Menu> subMenuList = menu.getSubMenu();
                    for (Menu sub : subMenuList) {
                        sub.setHasMenu(RightsHelper.testRights(roleRights, sub.getMENU_ID()));
                        if(sub.isHasMenu()){
                            menus.add(sub);
                        }
                    }
                }
            }


            String Rights = pd.getString("RIGHTS");
            List<Menu> menuListrole = menuService.listAllMenu();
            List<Menu> menusrole = new ArrayList<Menu>();
            for (Menu menu : menuListrole) {
                menu.setHasMenu(RightsHelper.testRights(Rights, menu.getMENU_ID()));
                if (menu.isHasMenu()) {
                    List<Menu> subMenuList = menu.getSubMenu();
                    for (Menu sub : subMenuList) {
                        sub.setHasMenu(RightsHelper.testRights(Rights, sub.getMENU_ID()));
                        if(sub.isHasMenu()){
                            menusrole.add(sub);
                        }
                    }
                }
            }

            List<String> ar=new ArrayList<>();
            for(Menu menu2:menusrole){
                ar.add(menu2.getMENU_ID());
            }
            List<PageData> pagedatas=new ArrayList<>();
            for (Menu item : menus) {//遍历list1

                if (ar.contains(item.getMENU_ID())) {//如果存在这个数
                    PageData pdmenu=new PageData();
                    pdmenu.put("MENU_ID",item.getMENU_ID());
                    pdmenu.put("name",item.getMENU_NAME());
                    pdmenu.put("checked",true);
                    pdmenu.put("disabled",false);
                    pagedatas.add(pdmenu);
                }else{
                    PageData pdmenu=new PageData();
                    pdmenu.put("MENU_ID",item.getMENU_ID());
                    pdmenu.put("name",item.getMENU_NAME());
                    pdmenu.put("checked",false);
                    pdmenu.put("disabled",false);
                    pagedatas.add(pdmenu);
                }
            }


            pd.put("menus",pagedatas);
            List<PageData> pageDatas= functionService.listAll(pd);
            for(PageData  pagedata1:pageDatas){
                pagedata1.put("value",pagedata1.getString("FUNCTION_ID"));
                if(pagedata1.getString("FUNCTION_ID").equals("1")){
                    pagedata1.put("text","专责");
                }else if (pagedata1.getString("FUNCTION_ID").equals("2")){
                    pagedata1.put("text","组长");
                }else if(pagedata1.getString("FUNCTION_ID").equals("3")){
                    pagedata1.put("text","检查员");
                }

            }
            pd.put("functions",pageDatas);
            return ResultModel.success(pd);

        } catch(Exception e){
            logger.error(e.toString(), e);
            return ResultModel.failure(null);
        }

    }





    /**
     * 编辑保存角色
     */
    @RequestMapping({"/editrole"})
    @ApiOperation("编辑保存角色")
    public ResultModel<String> editrole()throws Exception{
        //ROLE_ID
        //menuIds ,   字符串
        //ROLE_NAME
        //FUNCTION_ID
        PageData pd = new PageData();
        try{
            pd=this.getPageData();
            String roleform = pd.getString("roleform");
            RoleAppVo myclass = JSONObject.parseObject(roleform , RoleAppVo.class);// jsonStr 是String类型。
            StringBuffer menuIds=new StringBuffer("");
            for(int i=0;i<myclass.getRolecheckList().size();i++){
                if(myclass.getRolecheckList().get(i).isChecked()){
                    menuIds.append(myclass.getRolecheckList().get(i).getMENU_ID()+",");
                }
            }
            menuIds.replace(menuIds.length() - 1, menuIds.length(), "");

            BigInteger rights = RightsHelper.sumRights(Tools.str2StrArray("22,"+menuIds.toString()));
            Role role = new Role();
            role.setRIGHTS(rights.toString());
            role.setROLE_ID(pd.getString("ROLE_ID"));
            roleService.updateRoleRights(role);
//            PageData edit = roleService.editpj(pd);
            return ResultModel.success("修改成功！");
        } catch(Exception e){
            logger.error(e.toString(), e);
            return ResultModel.failure("修改失败！");
        }

    }





    /**
     * 删除角色
     */
    @RequestMapping({"/deleterole"})
    @ApiOperation("删除角色")
    public ResultModel<String> deleterole()throws Exception{
        PageData pd = new PageData();
        pd=this.getPageData();

        try{

            pd.put("ROLE_ID",pd.getString("ROLE_ID"));
            List<Role> roleList_z = roleService.listAllRolesByPId(pd);		//列出此部门的所有下级
            if(roleList_z.size() > 0){

                return  ResultModel.failure("请先删除子角色");
            }else{
                List<PageData> userlist = roleService.listAllUByRid(pd);
//                    List<PageData> appuserlist = roleService.listAllAppUByRid(pd);
                if(userlist.size() > 0){
                    return  ResultModel.failure("请先删除角色用户");
                }else{
                    roleService.deleteRoleById(pd.getString("ROLE_ID"));
                    roleService.deleteKeFuById(pd.getString("ROLE_ID"));
                    roleService.deleteGById(pd.getString("ROLE_ID"));
                    return  ResultModel.success("删除成功！");
                }
            }

        } catch(Exception e){
            logger.error(e.toString(), e);
            return  ResultModel.failure("删除失败！");
        }

    }



    /**
     * 查看角色人员
     */
    @RequestMapping({"/seeroleuser"})
    @ApiOperation("查看角色人员")
    public ResultModel<List<PageData>> seeroleuser()throws Exception{
        //ROLE_ID
        PageData pd = new PageData();
        pd=this.getPageData();
        try{
            List<PageData> userlist = roleService.listAllUByRidpj(pd);
            return  ResultModel.success(userlist);
        } catch(Exception e){
            logger.error(e.toString(), e);
            return  ResultModel.failure(null);
        }

    }




    /**
     * 单位一级列表
     */
    @RequestMapping(value="/unitlist")
    @ApiOperation("单位一级列表")
    public ResultModel<Page> unitlist(Page page){
        PageData pd = new PageData();
        try{
            pd = this.getPageData();
            String NAME=pd.getString("NAME");
            if(null != NAME && !"".equals(NAME)){
                NAME = NAME.trim();
                pd.put("NAME", NAME);
            }

            page.setPd(pd);
            List<PageData>	varList = unitService.list(page);	//列出Unit列表
            for (PageData pagedata:varList){
                pagedata.put("isShow",false);
            }
            page.setPageDataList(varList);
            return  ResultModel.success(page);
        } catch(Exception e){
            logger.error(e.toString(), e);
            return  ResultModel.failure(null);
        }
    }



    /**
     * 单位一级列表
     */
    @RequestMapping(value="/unitlistsearch")
    @ApiOperation("单位一级列表")
    public ResultModel<List<PageData>> unitlistsearch(Page page){
        PageData pd = new PageData();
        try{
            pd = this.getPageData();
            String NAME=pd.getString("NAME");
            if(null != NAME && !"".equals(NAME)){
                NAME = NAME.trim();
                pd.put("NAME", NAME);
            }
            List<PageData> data = unitService.listAllone(pd);
            ArrayList<String> list=new ArrayList<>();
            ArrayList<String> list1=new ArrayList<>();
            for(PageData pagedata:data){
                if(!pagedata.getString("PARENT_ID").equals("0")){
                    list.add(pagedata.getString("PARENT_ID"));
                }else{
                    list1.add(pagedata.getString("UNIT_ID"));
                }
            }


            ArrayList<String> single = getSingle(list);
            ArrayList<String> single1 = getSingle(list1);
            List<PageData> alist=new ArrayList<>();
            for(String a:single){
                PageData pd1 = new PageData();
                pd1.put("UNIT_ID",a);
                PageData byId = unitService.findById(pd1);
                byId.put("isShow",true);
                alist.add(byId);
            }
            for(String a:single1){
                if(single.contains(a))
                continue;
                PageData pd1 = new PageData();
                pd1.put("UNIT_ID",a);
                PageData byId = unitService.findById(pd1);
                byId.put("isShow",false);
                alist.add(byId);
            }
            return ResultModel.success(alist);
        } catch(Exception e){
            logger.error(e.toString(), e);
            return  ResultModel.failure(null);
        }
    }


    public static ArrayList<String> getSingle(ArrayList<String> list) {
        ArrayList<String> tempList = new ArrayList();          //1,创建新集合
        Iterator<String> it = list.iterator();              //2,根据传入的集合(老集合)获取迭代器
        while(it.hasNext()) {                  //3,遍历老集合
            String obj = it.next();                //记录住每一个元素
            if(!tempList.contains(obj)) {            //如果新集合中不包含老集合中的元素
                tempList.add(obj);                //将该元素添加
            }
        }
        return tempList;
    }




    /**
     * 单位二级级列表
     */
    @RequestMapping(value="/unitsecondlist")
    @ApiOperation("单位二级级列表")
    public ResultModel<List<PageData>> unitsecondlist(){
        //PARENT_ID
        PageData pd = new PageData();
        try{
            pd = this.getPageData();
            List<PageData> pageData = unitService.listAll(pd);
            return  ResultModel.success(pageData);
        } catch(Exception e){
            logger.error(e.toString(), e);
            return  ResultModel.failure(null);
        }
    }


    /**
     * 单位二级级列表
     */
    @RequestMapping(value="/unitsecondlistName")
    @ApiOperation("单位二级级列表")
    public ResultModel<List<PageData>> unitsecondlistName(){
        //PARENT_ID
        PageData pd = new PageData();
        try{
            pd = this.getPageData();
            List<PageData> pageData = unitService.listNAME(pd);
            return  ResultModel.success(pageData);
        } catch(Exception e){
            logger.error(e.toString(), e);
            return  ResultModel.failure(null);
        }
    }





    /**
     * 新增单位
     */
    @RequestMapping(value="/saveunit")
    @ApiOperation("新增单位")
    public ResultModel<String> saveunit() throws Exception{
        // USER_ID
        PageData pd = new PageData();
        pd = this.getPageData();
        String fieldsList = pd.getString("FieldsList");
        List<Field> Fields = new ArrayList<>();
        JSONArray jsonArray=JSONArray.parseArray(fieldsList);
        for (Object jsonObject : jsonArray) {
            Field platformModel = JSONObject.parseObject(jsonObject.toString(), Field.class);
            Fields.add(platformModel);
        }
        for(Field field:Fields){
            PageData pd1 = new PageData();
            pd1.put("UNIT_ID", this.get32UUID());	//主键
            pd1.put("GROUP",field.getName());
            pd1.put("NAME",field.getCompany());
            pd1.put("PHONE",field.getPhone());
            pd1.put("ADDRESS",field.getAddress());
            String parent_id = pd.getString("PARENT_ID");
            if(parent_id==null || "".equals(parent_id)){
                pd1.put("PARENT_ID","0");
            }
            pd1.put("STATUS","1");
            pd1.put("USER_ID",pd.getString("uid"));
            pd1.put("CREATED_TIME",new Date().toString());
            pd1.put("UPDATE_TIME",new Date().toString());
            unitService.save(pd1);
        }

        return  ResultModel.success("success");
    }






    /**
     * 新增单位
     */
    @RequestMapping(value="/saveunitsecond")
    @ApiOperation("新增子单位")
    public ResultModel<List<PageData>> saveunitsecond() throws Exception{
        // USER_ID
        PageData pd = new PageData();
        pd = this.getPageData();
        String fieldsList = pd.getString("FieldsList");
        List<Fieldsecond> Fields = new ArrayList<>();
        JSONArray jsonArray=JSONArray.parseArray(fieldsList);
        for (Object jsonObject : jsonArray) {
            Fieldsecond platformModel = JSONObject.parseObject(jsonObject.toString(), Fieldsecond.class);
            Fields.add(platformModel);
        }
        for(Fieldsecond field:Fields){
            PageData pd1 = new PageData();
            pd1.put("UNIT_ID", this.get32UUID());	//主键
            pd1.put("GROUP",field.getName());
            pd1.put("NAME",field.getCompany());
            pd1.put("PHONE",field.getPhone());
            pd1.put("ADDRESS",field.getAddress());
            pd1.put("PARENT_ID",field.getParentcompanyid());
            pd1.put("STATUS","1");
            pd1.put("USER_ID",pd.getString("uid"));
            pd1.put("CREATED_TIME",new Date().toString());
            pd1.put("UPDATE_TIME",new Date().toString());
            unitService.save(pd1);
        }

        pd.put("PARENT_ID",Fields.get(0).getParentcompanyid());
        List<PageData> pageData = unitService.listAll(pd);
        return  ResultModel.success(pageData);
    }



    /**
     * 去修改单位页面
     */
    @RequestMapping(value="/goeditunit")
    @ApiOperation("去修改单位页面")
    public ResultModel<List<PageData>> goeditunit(){
        PageData pd = new PageData();
        pd = this.getPageData();
        try {
            pd = unitService.findById(pd);	//根据ID读取
            List<PageData> listtop = unitService.listtop(pd);
            listtop.add(pd);
            return ResultModel.success(listtop);
        } catch (Exception e) {
            logger.error(e.toString(), e);
            return ResultModel.failure(null);
        }

    }


    /**
     * 修改单位
     */
    @RequestMapping(value="/editunit")
    @ApiOperation("修改单位")
    public ResultModel<String> editunit() throws Exception{
        PageData pd = new PageData();
        pd = this.getPageData();

        String fieldsList = pd.getString("FieldsList");
        List<Field> Fields = new ArrayList<>();
        JSONArray jsonArray=JSONArray.parseArray(fieldsList);
        for (Object jsonObject : jsonArray) {
            Field platformModel = JSONObject.parseObject(jsonObject.toString(), Field.class);
            Fields.add(platformModel);
        }
        pd.put("NAME",Fields.get(0).getCompany());
        pd.put("GROUP",Fields.get(0).getName());
        pd.put("PHONE",Fields.get(0).getPhone());
        pd.put("ADDRESS",Fields.get(0).getAddress());
        pd.put("USER_ID",pd.getString("uid"));
        pd.put("UNIT_ID",pd.getString("UNIT_ID"));
        String parent_id = pd.getString("PARENT_ID");
        if(parent_id==null || "".equals(parent_id)){
            pd.put("PARENT_ID","0");
        }
        pd.put("STATUS","1");
        pd.put("UPDATE_TIME",new Date().toString());
        unitService.edit(pd);
        return  ResultModel.success("success");
    }




    /**
     * 修改单位
     */
    @RequestMapping(value="/editunitsecond")
    @ApiOperation("修改子单位")
    public ResultModel<List<PageData>> editunitsecond() throws Exception{
        PageData pd = new PageData();
        pd = this.getPageData();

        String fieldsList = pd.getString("FieldsList");
        List<Field> Fields = new ArrayList<>();
        JSONArray jsonArray=JSONArray.parseArray(fieldsList);
        for (Object jsonObject : jsonArray) {
            Field platformModel = JSONObject.parseObject(jsonObject.toString(), Field.class);
            Fields.add(platformModel);
        }
        pd.put("NAME",Fields.get(0).getCompany());
        pd.put("GROUP",Fields.get(0).getName());
        pd.put("PHONE",Fields.get(0).getPhone());
        pd.put("ADDRESS",Fields.get(0).getAddress());
        pd.put("USER_ID",pd.getString("uid"));
        pd.put("UNIT_ID",pd.getString("UNIT_ID"));
        String parent_id = pd.getString("PARENT_ID");
        if(parent_id==null || "".equals(parent_id)){
            pd.put("PARENT_ID","0");
        }
        pd.put("STATUS","1");
        pd.put("UPDATE_TIME",new Date().toString());
        unitService.edit(pd);

        List<PageData> pageData = unitService.listAll(pd);
        return  ResultModel.success(pageData);
    }



    /**
     * 删除单位
     */
    @RequestMapping(value="/deleteunit")
    @ApiOperation("删除单位")
    public ResultModel<String> deleteunit(){
        PageData pd = new PageData();
        try{
            pd = this.getPageData();


            pd.put("PARENT_ID",pd.getString("UNIT_ID"));
            List<PageData> pageData = unitService.listAll(pd);
            if(pageData.size() == 0){
                unitService.delete(pd);
            }else{
                return ResultModel.failure("先删除其下子单位！");
            }
            return ResultModel.success("删除成功！");
        } catch(Exception e){
            logger.error(e.toString(), e);
            return ResultModel.failure("failed");
        }

    }






    /**
     * 从EXCEL导入单位到数据库
     */
    @RequestMapping({"/readunitExcel"})
    @ApiOperation("从EXCEL导入单位到数据库")
    public ResultModel<String> readunitExcel(@RequestParam(value="excel",required=false) MultipartFile file) throws Exception{

        PageData pd = new PageData();
        if (null != file && !file.isEmpty()) {
            String filePath = PathUtil.getClasspath() + Const.FILEPATHFILE;								//文件上传路径
            String fileName =  FileUpload.fileUp(file, filePath, "excelunit");							//执行上传

            List<PageData> listPd = (List)ObjectExcelRead.readExcel(filePath, fileName, 2, 0, 0);	//执行读EXCEL操作,读出的数据导入List 2:从第3行开始；0:从第A列开始；0:第0个sheet
            if(listPd.size() == 0){
                return ResultModel.failure("导入失败！");
            }

            /**
             * var0 :单位名称
             * var1 :负责人
             * var2 :负责人电话
             * var3 :单位地址
             * var4 :上级单位
             */
            for(int i=0;i<listPd.size();i++){
                String  parentunit = listPd.get(i).getString("var4");
                if(parentunit == null || "".equals(parentunit)){
                    pd.put("UNIT_ID", this.get32UUID());	//主键
                    String GROUP = listPd.get(i).getString("var1");
                    pd.put("GROUP", GROUP);
                    String PHONE = listPd.get(i).getString("var2");
                    pd.put("PHONE", PHONE);
                    String ADDRESS = listPd.get(i).getString("var3");
                    pd.put("ADDRESS", ADDRESS);
                    pd.put("USER_ID","");
                    String NAME = listPd.get(i).getString("var0");
                    pd.put("NAME", NAME);
                    if(unitService.findByNAMEpd(pd) != null){
                        return ResultModel.failure("单位名："+NAME+"重复"+",请重新导入。");
                    }
                    pd.put("PARENT_ID","0");
                    pd.put("STATUS","1");
                    pd.put("CREATED_TIME",new Date());
                    pd.put("UPDATE_TIME",new Date());
                    unitService.save(pd);
                }else{
                    pd.put("UNIT_ID", this.get32UUID());	//主键
                    String GROUP = listPd.get(i).getString("var1");
                    pd.put("GROUP", GROUP);
                    String PHONE = listPd.get(i).getString("var2");
                    pd.put("PHONE", PHONE);
                    String ADDRESS = listPd.get(i).getString("var3");
                    pd.put("ADDRESS", ADDRESS);
                    pd.put("USER_ID","");
                    String NAME = listPd.get(i).getString("var0");
                    pd.put("NAME", NAME);
                    if(unitService.findByNAMEpd(pd) != null){
                        return ResultModel.failure("单位名："+NAME+"重复"+",请重新导入。");
                    }
                    PageData pd1=new PageData();
                    pd1.put("NAME",parentunit);
                    PageData byNAMEpd = unitService.findByNAMEpd(pd1);
                    if(byNAMEpd == null){
                        return ResultModel.failure("上级单位不存在："+parentunit+",请重新导入。");
                    }
                    pd.put("PARENT_ID",byNAMEpd.getString("UNIT_ID"));
                    pd.put("STATUS","1");
                    pd.put("CREATED_TIME",new Date());
                    pd.put("UPDATE_TIME",new Date());
                    unitService.save(pd);
                }

            }
            /*存入数据库操作======================================*/
            return ResultModel.success("导入成功！");
        }
        return ResultModel.failure("导入失败！");

    }



    /**
     * 下载单位模版
     */
    @RequestMapping({"/downunitExcel"})
    @ApiOperation("下载单位模版")
    public HttpServletResponse downunitExcel(HttpServletResponse response)throws Exception{
        FileDownload.fileDownload(response, PathUtil.getClasspath() + Const.FILEPATHFILE + "Unitexcel.xls", "Unitexcel.xls");
        return response;
    }





    /**
     * 回收站列表
     */
    @RequestMapping(value="/recyclebin")
    @ApiOperation("回收站列表")
    public ResultModel<Page> recyclebin(Page page){
        PageData pd = new PageData();
        try{
            pd = this.getPageData();
            String NAME=pd.getString("NAME");
            if(null != NAME && !"".equals(NAME)){
                NAME = NAME.trim();

                String[] split = NAME.split("/");
                String lastLoginStart = split[0];
                String lastLoginEnd = split[1];
                if(lastLoginStart != null && !"".equals(lastLoginStart)){
                    lastLoginStart = lastLoginStart+" 00:00:00";
                    pd.put("lastLoginStart", lastLoginStart);
                }
                if(lastLoginEnd != null && !"".equals(lastLoginEnd)){
                    lastLoginEnd = lastLoginEnd+" 00:00:00";
                    pd.put("lastLoginEnd", lastLoginEnd);
                }
            }
            page.setPd(pd);
            List<PageData>	varList = unitService.listtask(page);

            if (varList.size() != 0) {
                for (PageData pageData : varList) {
                    Date data = (Date)pageData.get("update_time");
                    Date newDate = addDate(data, 180); // 指定日期加上20天
                    long difference =  (newDate.getTime()-new Date().getTime())/86400000;
                    long abs = Math.abs(difference);
                    pageData.put("abs",abs);

                    List<PageData> forObject = unitService.listtaskfind(pageData);
                    String normName="";
                    for(PageData pageData1:forObject){
                        normName=normName+"/"+pageData1.getString("NAME");
                    }
                    normName=normName.substring(1);
                    pageData.put("normName", normName);
                    // 创建一个数值格式化对象
                    NumberFormat numberFormat = NumberFormat.getInstance();
                    numberFormat.setMaximumFractionDigits(2);
                    int general_inspection = new Double(String.valueOf(pageData.get("general_inspection"))).intValue();
                    int checked = new Double(String.valueOf(pageData.get("checked"))).intValue();
                    String result = numberFormat.format((float) checked / (float) general_inspection * 100);
                    pageData.put("result", result);
                }
            }

            page.setPageDataList(varList);
            return  ResultModel.success(page);
        } catch(Exception e){
            logger.error(e.toString(), e);
            return  ResultModel.failure(null);
        }


    }


    public  Date addDate(Date date,long day)  {
        long time = date.getTime(); // 得到指定日期的毫秒数
        day = day*24*60*60*1000; // 要加上的天数转换成毫秒数
        time+=day; // 相加得到新的毫秒数
        return new Date(time); // 将毫秒数转换成日期
    }


    /**
     * 回收站删除
     */
    @RequestMapping(value="/deleterecyclebin")
    @ApiOperation("回收站删除")
    public ResultModel<String> deleterecyclebin(){
        PageData pd = new PageData();
        pd = this.getPageData();
        pd.put("status","12");
        try {
            unitService.recordTask(pd);
            return ResultModel.success("删除成功！");
        }catch (Exception e){
            e.printStackTrace();
            return ResultModel.failure("删除失败！");
        }



    }

    /**
     * 回收站过期任务处理
     */

    @Scheduled(cron = "0/5 * * * * ? ")   //每5秒执行一次
    public void solveTask(){
        PageData pd = new PageData();
        try{
            unitService.solveTask(pd);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("回收站过期任务处理异常！");
        }



    }





    /**
     * 回收站恢复
     */
    @RequestMapping(value="/recordrecyclebin")
    @ApiOperation("回收站恢复")
    public ResultModel<String> recordrecyclebin(){
        PageData pd = new PageData();
        pd = this.getPageData();
        String str = (String) redisTemplate.opsForValue().get(pd.getString("id"));
        pd.put("status",str);
        try {
            unitService.recordTask(pd);
            return ResultModel.success("恢复成功！");
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("恢复失败！");
            return ResultModel.failure("恢复失败！");
        }

    }





    /**
     * 导出全部
     */
    @RequestMapping(value="/downloaddatabase")
    @ApiOperation("导出全部")
    public ModelAndView downloaddatabase(HttpServletResponse response){
        //用户
        PageData pd = new PageData();
        pd = this.getPageData();

        try{
            //检索条件===
            Map<String,Object> dataMap = new HashMap<String,Object>();
            List<String> titles = new ArrayList<String>();
            titles.add("用户名"); 		//1
            titles.add("手机号码");      //2
            titles.add("角色");			//3
            titles.add("部门");			//4
            dataMap.put("titles", titles);
            String[] USER_IDS=(String[]) pd.get("USER_IDS");
            List<PageData> userList=null;
            if(USER_IDS !=null && USER_IDS.length>0){
                userList = userService.listAllUserByIds(USER_IDS);
            }else{
                userList = userService.listAllUserpj(pd);
            }

            List<PageData> varList = new ArrayList<PageData>();
            for(int i=0;i<userList.size();i++){
                PageData vpd = new PageData();
                vpd.put("var1", userList.get(i).getString("USERNAME"));		//1
                vpd.put("var2", userList.get(i).getString("PHONE"));	//2
                vpd.put("var3", userList.get(i).getString("ROLE_NAME"));		//3
                PageData pd1 = new PageData();
                pd1.put("ROLE_ID",userList.get(i).getString("PARENT_ID"));
                PageData objectById = roleService.findObjectById(pd1);
                vpd.put("var4", objectById.getString("ROLE_NAME"));		//4
                varList.add(vpd);
            }
            dataMap.put("varList", varList);
            List<String> titles1 = new ArrayList<String>();
            titles1.add("单位名称");	//1
            titles1.add("负责人");	//2
            titles1.add("负责人电话");	//3
            titles1.add("单位地址");	//4
            titles1.add("上级单位");	//5
            dataMap.put("titles1", titles1);
            String[] UNIT_IDS=(String[])pd.get("UNIT_IDS");
            List<PageData> varOList=null;
            if(UNIT_IDS !=null && UNIT_IDS.length>0){
                varOList = unitService.listAllByIds(UNIT_IDS);
            }else{
                varOList = unitService.listAllUNIT(pd);
            }
            List<PageData> varList1 = new ArrayList<PageData>();
            for(int i=0;i<varOList.size();i++){
                PageData vpd = new PageData();
                vpd.put("var1", varOList.get(i).getString("NAME"));	//1
                vpd.put("var2", varOList.get(i).getString("GROUPUSER"));	//2
                vpd.put("var3", varOList.get(i).getString("PHONE"));	//3
                vpd.put("var4", varOList.get(i).getString("ADDRESS"));	//4
                String parent_id = varOList.get(i).getString("PARENT_ID");
                PageData pdunit=new PageData();
                pdunit.put("UNIT_ID",parent_id);
                PageData unitpd = unitService.findById(pdunit);
                if(unitpd!=null){
                    vpd.put("var5", unitpd.getString("NAME"));	//5
                }else{
                    vpd.put("var5", "");	//5
                }
                varList1.add(vpd);
            }

            dataMap.put("varList1", varList1);

            List<HSSFSheet> sheets = new ArrayList<>();
            List<PageData> pageData = unitService.listUserExecel(pd);
            if(pageData!=null && pageData.size()>0){
                List<String> ids = new ArrayList<>();
                for(PageData PageDataexecel:pageData){
                    ids.add(PageDataexecel.getString("excel_id"));
                }

                String[] strings = new String[ids.size()];
                ids.toArray(strings);
                List<PageData> pageData1 = unitService.listUserExecelsecond(strings);
                for(PageData PageDataexecel:pageData1) {
                    if ("全部标准".equals(pd.getString("checkstandrd"))) {
                        // 打开已有的excel
                        String strExcelPath = PathUtil.getClasspath() + Const.EXCELLIST + PageDataexecel.getString("original_name");
                        InputStream in = new FileInputStream(strExcelPath);
                        if (PageDataexecel.getString("original_name").endsWith("xlsx")) {
                            XSSFWorkbook xb = new XSSFWorkbook(in);
                            HSSFWorkbook workBook = new HSSFWorkbook();

                            Xssf2Hssf xlsx2xls = new Xssf2Hssf();
                            xlsx2xls.transformXSSF(xb, workBook);

                            for (int ii = 0; ii < workBook.getNumberOfSheets(); ii++) {
                                HSSFSheet sheet = workBook.getSheetAt(ii);
                                sheets.add(sheet);
                            }

                        } else if (PageDataexecel.getString("original_name").endsWith("xls")) {
                            HSSFWorkbook wb = new HSSFWorkbook(in);
                            for (int ii = 0; ii < wb.getNumberOfSheets(); ii++) {
                                HSSFSheet sheet = wb.getSheetAt(ii);
                                sheets.add(sheet);
                            }

                        }
                    }else {

                        if(pd.getString("checkstandrd").contains(PageDataexecel.getString("original_name"))){
                            // 打开已有的excel
                            String strExcelPath = PathUtil.getClasspath() + Const.EXCELLIST + PageDataexecel.getString("original_name");
                            InputStream in = new FileInputStream(strExcelPath);
                            if (PageDataexecel.getString("original_name").endsWith("xlsx")) {
                                XSSFWorkbook xb = new XSSFWorkbook(in);
                                HSSFWorkbook workBook = new HSSFWorkbook();

                                Xssf2Hssf xlsx2xls = new Xssf2Hssf();
                                xlsx2xls.transformXSSF(xb, workBook);

                                for (int ii = 0; ii < workBook.getNumberOfSheets(); ii++) {
                                    HSSFSheet sheet = workBook.getSheetAt(ii);
                                    sheets.add(sheet);
                                }

                            } else if (PageDataexecel.getString("original_name").endsWith("xls")) {
                                HSSFWorkbook wb = new HSSFWorkbook(in);
                                for (int ii = 0; ii < wb.getNumberOfSheets(); ii++) {
                                    HSSFSheet sheet = wb.getSheetAt(ii);
                                    sheets.add(sheet);
                                }

                            }

                        }

                    }
                }
            }
            dataMap.put("sheets",sheets);

            PageData pagedatatask = new PageData();
            pagedatatask.put("uid",pd.getString("USER_ID"));
            pagedatatask.put("page",1);
            pagedatatask.put("size",1000000);
            pagedatatask.put("type",2);
            String checktime = pd.getString("checktime");
            if(checktime!=null &&  !"".equals(checktime)){
                String[] time = checktime.split("至");
                String lastLoginStart = time[0];
                String lastLoginEnd = time[1];
                if(lastLoginStart != null && !"".equals(lastLoginStart)){
                    lastLoginStart = lastLoginStart+" 00:00:00";
                    pagedatatask.put("lastLoginStart", lastLoginStart);
                }

                if(lastLoginEnd != null && !"".equals(lastLoginEnd)){
                    lastLoginEnd = lastLoginEnd+" 00:00:00";
                    pagedatatask.put("lastLoginEnd", lastLoginEnd);
                }

            }
            List<PageData> dataList = taskService.queryTaskList(pagedatatask);
            for (PageData data : dataList) {
                pagedatatask.put("task_id",data.getString("id"));
                List<TaskCensorRes> taskCensorRes = taskService.checkingTaskList(pagedatatask);
                data.put("taskDetail",taskCensorRes);
            }
            List<PageData> data = new LinkedList<>();
            for (PageData pageData2 : dataList) {
                List<TaskCensorRes> taskDetail = (List<TaskCensorRes>) pageData2.get("taskDetail");
                for (TaskCensorRes censorRe : taskDetail) {

                    for (CensorRow censorRow : censorRe.getCensorRowList()) {
                        String remark = null;
                        String content = null;
                        String type = null;
                        for (CensorRowIssue censorRowIssue : censorRow.getCensorRowIssueList()) {
                            remark = censorRowIssue.getRemark();
                            content = censorRowIssue.getContent();
                            type = censorRowIssue.getType();
                        }


                        if("1".equals(pd.getString("type"))){
                            PageData pageDataDate = new PageData();
                            pageDataDate.put("name",pageData2.get("taskName"));
                            pageDataDate.put("unitName",pageData2.get("unitName"));
                            pageDataDate.put("siteName",pageData2.get("siteName"));
                            pageDataDate.put("normName",pageData2.get("normName"));
                            pageDataDate.put("content",censorRow.getRow_content());
                            pageDataDate.put("serialParentName",unitService.queryNormDetailName(censorRow.getId()));
                            pageDataDate.put("serialName",censorRow.getRow_item());
                            pageDataDate.put("issueContent",content);
                            pageDataDate.put("serial",unitService.queryNormDetailSerial(censorRow.getId()));
                            pageDataDate.put("score",censorRow.getScore());
                            pageDataDate.put("issueRemark",remark);
                            if(StringUtils.isNotBlank(type)){
                                if(type.equals("2")){
                                    pageDataDate.put("typeName","严重");
                                }else{
                                    pageDataDate.put("typeName","一般");
                                }
                            }

                            String s = unitService.querytaskcheckId(censorRe.getCensor_id());
                            PageData checkuser=new PageData();
                            checkuser.put("USER_ID",s);
                            PageData byUiId = userService.findByUiId(checkuser);
                            if(byUiId!=null){
                                pageDataDate.put("userName",byUiId.getString("USERNAME"));
                            }
                            pageDataDate.put("startTime",pageData2.get("star_time"));
                            pageDataDate.put("endTime",pageData2.get("end_time"));
                            pageDataDate.put("location",pageData2.get("location"));
                            pageDataDate.put("recordTime",unitService.queryIssueTime(censorRow.getId()));
                            data.add(pageDataDate);

                        }else if("2".equals(pd.getString("type"))){

                            if(censorRow.getCensorRowIssueList().size()!=0){
                                PageData pageDataDate = new PageData();
                                pageDataDate.put("name",pageData2.get("taskName"));
                                pageDataDate.put("unitName",pageData2.get("unitName"));
                                pageDataDate.put("siteName",pageData2.get("siteName"));
                                pageDataDate.put("normName",pageData2.get("normName"));
                                pageDataDate.put("content",censorRow.getRow_content());
                                pageDataDate.put("serialParentName",unitService.queryNormDetailName(censorRow.getId()));
                                pageDataDate.put("serialName",censorRow.getRow_item());
                                pageDataDate.put("issueContent",content);
                                pageDataDate.put("serial",unitService.queryNormDetailSerial(censorRow.getId()));
                                pageDataDate.put("score",censorRow.getScore());
                                pageDataDate.put("issueRemark",remark);
                                if(StringUtils.isNotBlank(type)){
                                    if(type.equals("2")){
                                        pageDataDate.put("typeName","严重");
                                    }else{
                                        pageDataDate.put("typeName","一般");
                                    }
                                }

                                String s = unitService.querytaskcheckId(censorRe.getCensor_id());
                                PageData checkuser=new PageData();
                                checkuser.put("USER_ID",s);
                                PageData byUiId = userService.findByUiId(checkuser);
                                if(byUiId!=null){
                                    pageDataDate.put("userName",byUiId.getString("USERNAME"));
                                }
                                pageDataDate.put("startTime",pageData2.get("star_time"));
                                pageDataDate.put("endTime",pageData2.get("end_time"));
                                pageDataDate.put("location",pageData2.get("location"));
                                pageDataDate.put("recordTime",unitService.queryIssueTime(censorRow.getId()));
                                data.add(pageDataDate);
                            }


                        }


                    }
                }
            }
            System.out.println(data+"\ndata");
            pagedatatask.put("taskCensorRes",data);
            List<String> titles2 = new ArrayList<String>();
            titles2.add("编号"); 		     //1
            titles2.add("任务名称");          //2
            titles2.add("单位名称");			 //3
            titles2.add("子单位");			 //4
            titles2.add("评价准则"); 		 //5
            titles2.add("评价内容");          //6
            titles2.add("所属大类");			  //7
            titles2.add("所属小类");			//8
            titles2.add("问题描述"); 		//9
            titles2.add("序号");            //10
            titles2.add("评分");			//11
            titles2.add("问题备注");			//12
            titles2.add("问题性质");			//13
            titles2.add("检查人员");			//14
            titles2.add("开始时间");			//15
            titles2.add("结束时间");			//16
            titles2.add("检查地点");			//17
            titles2.add("记录时间");			//18
            dataMap.put("titles2", titles2);


            List<PageData> varList2 = new ArrayList<PageData>();
            for(int i=0;i<data.size();i++){
                PageData vpd = new PageData();
                vpd.put("var1", String.valueOf(i));		//1
                vpd.put("var2", data.get(i).getString("name"));	//2
                vpd.put("var3", data.get(i).getString("unitName"));		//3
                vpd.put("var4", data.get(i).getString("siteName"));		//4

                List<PageData> normlist=(ArrayList<PageData>)data.get(i).get("normName");
                String name="";
                for(int j=0;j<=normlist.size()-1;j++){
                    if(j < normlist.size()-1){
                        name +=  normlist.get(j).getString("NAME")+ ",";
                    }else{
                        name += normlist.get(j).getString("NAME");
                    }
                }
                vpd.put("var5",name);		//5
                vpd.put("var6", data.get(i).getString("content"));		//6
                vpd.put("var7", data.get(i).getString("serialParentName"));		//7
                vpd.put("var8", data.get(i).getString("serialName"));		//8
                vpd.put("var9", data.get(i).getString("issueContent"));		//9
                vpd.put("var10", data.get(i).getString("serial"));		//10
                vpd.put("var11", data.get(i).get("score").toString());		//11
                vpd.put("var12", data.get(i).getString("issueRemark"));		//12
                vpd.put("var13", data.get(i).getString("typeName"));		//13
                vpd.put("var14", data.get(i).getString("userName"));		//14
                vpd.put("var15",  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(data.get(i).get("startTime")));		//15
                vpd.put("var16", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(data.get(i).get("endTime")));		//16
                vpd.put("var17", data.get(i).getString("location"));		//17
                vpd.put("var18",data.get(i).getString("recordTime"));		//18
                varList2.add(vpd);
            }
            dataMap.put("varList2", varList2);
            ObjectExcelViewAll erv = new ObjectExcelViewAll();
            ModelAndView mv = new ModelAndView();
            mv = new ModelAndView(erv,dataMap);
            return mv;
        } catch(Exception e){
            logger.error(e.toString(), e);
            return null;
        }

    }





    /**
     * 导出标准问题图片
     */
    @RequestMapping(value="/downloadcheckpicture")
    @ApiOperation("导出标准问题图片")
    public ResultModel<String> downloadcheckpicture(HttpServletResponse response) throws Exception{
        DelAllFile.delFolder(PathUtil.getClasspath()+"admin/ftl/checkproblem");
        DelAllFile.delFolder(PathUtil.getClasspath()+"admin/ftl/checkproblem.zip");
        String filePath = PathUtil.getClasspath() + "admin/ftl/checkproblem/";
        File files=new File(filePath);
        if(!files.exists()){	//如果_test2文件夹不存在
            files.mkdir();		//创建文件夹
        }
        PageData pd = new PageData();
        pd = this.getPageData();
        PageData pagedatatask = new PageData();
        pagedatatask.put("uid",pd.getString("USER_ID"));
        pagedatatask.put("page",1);
        pagedatatask.put("size",1000000);
        pagedatatask.put("type",2);
        String checktime = pd.getString("checktime");
        if(checktime!=null &&  !"".equals(checktime)){
            String[] time = checktime.split("至");
            String lastLoginStart = time[0];
            String lastLoginEnd = time[1];
            if(lastLoginStart != null && !"".equals(lastLoginStart)){
                lastLoginStart = lastLoginStart+" 00:00:00";
                pagedatatask.put("lastLoginStart", lastLoginStart);
            }

            if(lastLoginEnd != null && !"".equals(lastLoginEnd)){
                lastLoginEnd = lastLoginEnd+" 00:00:00";
                pagedatatask.put("lastLoginEnd", lastLoginEnd);
            }

        }
        List<PageData> dataList = taskService.queryTaskList(pagedatatask);
        if(dataList==null || dataList.size()==0){
            return ResultModel.failure("暂无数据图片");
        }
        for (PageData data : dataList) {
            pagedatatask.put("task_id",data.getString("id"));
            List<TaskCensorRes> taskCensorRes = taskService.checkingTaskList(pagedatatask);
            data.put("taskDetail",taskCensorRes);
        }
        List<PageData> data = new LinkedList<>();
        for (PageData pageData2 : dataList) {
            List<TaskCensorRes> taskDetail = (List<TaskCensorRes>) pageData2.get("taskDetail");
            for (TaskCensorRes censorRe : taskDetail) {
                for (CensorRow censorRow : censorRe.getCensorRowList()) {
                    for (CensorRowIssue censorRowIssue : censorRow.getCensorRowIssueList()) {
                        List<CensorRowIssueImage> censorRowIssueImageList = censorRowIssue.getCensorRowIssueImageList();
                        for(CensorRowIssueImage censorrowissueimage:censorRowIssueImageList){

                            if(censorrowissueimage.getUrl()!=null && !"".equals(censorrowissueimage.getUrl())){
                                String filePath1 = PathUtil.getClasspath() + "admin/ftl/checkproblem/" + censorRe.getNname();
                                File file1=new File(filePath1);
                                if(!file1.exists()){	//如果_test2文件夹不存在
                                    file1.mkdir();		//创建文件夹
                                }
                                File result  = new File(PathUtil.getClasspath() + "admin/ftl/checkproblem/" + censorRe.getNname()+"/"+censorrowissueimage.getId()+".png");
                                FileInputStream input = new FileInputStream(PathUtil.getClasspath()+censorrowissueimage.getUrl());     //需要复制的原图的路径+图片名+ .png(这是该图片的格式)
                                FileOutputStream out = new FileOutputStream(result);
                                byte[] buffer = new byte[100];//一个容量，相当于打水的桶，可以自定义大小
                                int hasRead = 0;
                                while ((hasRead = input.read(buffer)) > 0) {
                                    out.write(buffer, 0, hasRead);//0：表示每次从0开始
                                }
                                System.out.println(result.getAbsolutePath());
                                input.close();//关闭
                                out.close();

                            }


                        }


                    }

                }
            }
        }

        /*生成的全部代码压缩成zip文件*/
        FileZip.zip(PathUtil.getClasspath()+"admin/ftl/checkproblem", PathUtil.getClasspath()+"admin/ftl/checkproblem.zip");
        /*下载代码*/
        FileDownload.fileDownload(response,  PathUtil.getClasspath() + "admin/ftl/checkproblem.zip" , "checkproblem.zip");
        return ResultModel.success("successed");
    }




    @RequestMapping(value="/downloaddatabasecheck")
    @ApiOperation("导出检查全部")
    public ModelAndView downloaddatabasecheck(HttpServletResponse response){
        //用户
        PageData pd = new PageData();
        pd = this.getPageData();
        try{
            //检索条件===
            Map<String,Object> dataMap = new HashMap<String,Object>();
            PageData pagedatatask = new PageData();
            pagedatatask.put("uid",pd.getString("USER_ID"));
            pagedatatask.put("page",1);
            pagedatatask.put("size",1000000);
            pagedatatask.put("type",2);

            String checktime = pd.getString("checktime");
            if(checktime!=null &&  !"".equals(checktime)){
                String[] time = checktime.split("至");
                String lastLoginStart = time[0];
                String lastLoginEnd = time[1];
                if(lastLoginStart != null && !"".equals(lastLoginStart)){
                    lastLoginStart = lastLoginStart+" 00:00:00";
                    pagedatatask.put("lastLoginStart", lastLoginStart);
                }

                if(lastLoginEnd != null && !"".equals(lastLoginEnd)){
                    lastLoginEnd = lastLoginEnd+" 00:00:00";
                    pagedatatask.put("lastLoginEnd", lastLoginEnd);
                }

            }

            List<PageData> dataList = taskService.queryTaskList(pagedatatask);
            if(dataList== null || dataList.size() == 0){
                ModelAndView mv= new ModelAndView();
                return  mv;
            }

            for (PageData data : dataList) {
                pagedatatask.put("task_id",data.getString("id"));
                List<TaskCensorRes> taskCensorRes = taskService.checkingTaskList(pagedatatask);
                data.put("taskDetail",taskCensorRes);
            }
            List<PageData> data = new LinkedList<>();
            for (PageData pageData2 : dataList) {
                List<TaskCensorRes> taskDetail = (List<TaskCensorRes>) pageData2.get("taskDetail");
                for (TaskCensorRes censorRe : taskDetail) {
                    for (CensorRow censorRow : censorRe.getCensorRowList()) {
                        String remark = null;
                        String content = null;
                        String type = null;
                        for (CensorRowIssue censorRowIssue : censorRow.getCensorRowIssueList()) {
                            remark = censorRowIssue.getRemark();
                            content = censorRowIssue.getContent();
                            type = censorRowIssue.getType();
                        }
                        if("1".equals(pd.getString("type"))){
                            PageData pageDataDate = new PageData();
                            pageDataDate.put("name",pageData2.get("taskName"));
                            pageDataDate.put("unitName",pageData2.get("unitName"));
                            pageDataDate.put("siteName",pageData2.get("siteName"));
                            pageDataDate.put("normName",pageData2.get("normName"));
                            pageDataDate.put("content",censorRow.getRow_content());
                            pageDataDate.put("serialParentName",unitService.queryNormDetailName(censorRow.getId()));
                            pageDataDate.put("serialName",censorRow.getRow_item());
                            pageDataDate.put("issueContent",content);
                            pageDataDate.put("serial",unitService.queryNormDetailSerial(censorRow.getId()));
                            pageDataDate.put("score",censorRow.getScore());
                            pageDataDate.put("issueRemark",remark);
                            if(StringUtils.isNotBlank(type)){
                                if(type.equals("2")){
                                    pageDataDate.put("typeName","严重");
                                }else{
                                    pageDataDate.put("typeName","一般");
                                }
                            }

                            String s = unitService.querytaskcheckId(censorRe.getCensor_id());
                            PageData checkuser=new PageData();
                            checkuser.put("USER_ID",s);
                            PageData byUiId = userService.findByUiId(checkuser);
                            if(byUiId!=null){
                                pageDataDate.put("userName",byUiId.getString("USERNAME"));
                            }
                            pageDataDate.put("startTime",pageData2.get("star_time"));
                            pageDataDate.put("endTime",pageData2.get("end_time"));
                            pageDataDate.put("location",pageData2.get("location"));
                            pageDataDate.put("recordTime",unitService.queryIssueTime(censorRow.getId()));
                            data.add(pageDataDate);

                        }else if("2".equals(pd.getString("type"))){

                            if(censorRow.getCensorRowIssueList().size()!=0){
                                PageData pageDataDate = new PageData();
                                pageDataDate.put("name",pageData2.get("taskName"));
                                pageDataDate.put("unitName",pageData2.get("unitName"));
                                pageDataDate.put("siteName",pageData2.get("siteName"));
                                pageDataDate.put("normName",pageData2.get("normName"));
                                pageDataDate.put("content",censorRow.getRow_content());
                                pageDataDate.put("serialParentName",unitService.queryNormDetailName(censorRow.getId()));
                                pageDataDate.put("serialName",censorRow.getRow_item());
                                pageDataDate.put("issueContent",content);
                                pageDataDate.put("serial",unitService.queryNormDetailSerial(censorRow.getId()));
                                pageDataDate.put("score",censorRow.getScore());
                                pageDataDate.put("issueRemark",remark);
                                if(StringUtils.isNotBlank(type)){
                                    if(type.equals("2")){
                                        pageDataDate.put("typeName","严重");
                                    }else{
                                        pageDataDate.put("typeName","一般");
                                    }
                                }

                                String s = unitService.querytaskcheckId(censorRe.getCensor_id());
                                PageData checkuser=new PageData();
                                checkuser.put("USER_ID",s);
                                PageData byUiId = userService.findByUiId(checkuser);
                                if(byUiId!=null){
                                    pageDataDate.put("userName",byUiId.getString("USERNAME"));
                                }
                                pageDataDate.put("startTime",pageData2.get("star_time"));
                                pageDataDate.put("endTime",pageData2.get("end_time"));
                                pageDataDate.put("location",pageData2.get("location"));
                                pageDataDate.put("recordTime",unitService.queryIssueTime(censorRow.getId()));
                                data.add(pageDataDate);
                            }
                        }


                    }
                }
            }
            System.out.println(data+"\ndata");
            pagedatatask.put("taskCensorRes",data);
            List<String> titles2 = new ArrayList<String>();
            titles2.add("编号"); 		     //1
            titles2.add("任务名称");          //2
            titles2.add("单位名称");			 //3
            titles2.add("子单位");			 //4
            titles2.add("评价准则"); 		 //5
            titles2.add("评价内容");          //6
            titles2.add("所属大类");			  //7
            titles2.add("所属小类");			//8
            titles2.add("问题描述"); 		//9
            titles2.add("序号");            //10
            titles2.add("评分");			//11
            titles2.add("问题备注");			//12
            titles2.add("问题性质");			//13
            titles2.add("检查人员");			//14
            titles2.add("开始时间");			//15
            titles2.add("结束时间");			//16
            titles2.add("检查地点");			//17
            titles2.add("记录时间");			//18
            dataMap.put("titles2", titles2);


            List<PageData> varList2 = new ArrayList<PageData>();
            for(int i=0;i<data.size();i++){
                PageData vpd = new PageData();
                vpd.put("var1", String.valueOf(i));		//1
                vpd.put("var2", data.get(i).getString("name"));	//2
                vpd.put("var3", data.get(i).getString("unitName"));		//3
                vpd.put("var4", data.get(i).getString("siteName"));		//4

                List<PageData> normlist=(ArrayList<PageData>)data.get(i).get("normName");
                String name="";
                for(int j=0;j<=normlist.size()-1;j++){
                    if(j < normlist.size()-1){
                        name +=  normlist.get(j).getString("NAME")+ ",";
                    }else{
                        name += normlist.get(j).getString("NAME");
                    }
                }
                vpd.put("var5",name);		//5
                vpd.put("var6", data.get(i).getString("content"));		//6
                vpd.put("var7", data.get(i).getString("serialParentName"));		//7
                vpd.put("var8", data.get(i).getString("serialName"));		//8
                vpd.put("var9", data.get(i).getString("issueContent"));		//9
                vpd.put("var10", data.get(i).getString("serial"));		//10
                vpd.put("var11", data.get(i).get("score").toString());		//11
                vpd.put("var12", data.get(i).getString("issueRemark"));		//12
                vpd.put("var13", data.get(i).getString("typeName"));		//13
                vpd.put("var14", data.get(i).getString("userName"));		//14
                vpd.put("var15",  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(data.get(i).get("startTime")));		//15
                vpd.put("var16", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(data.get(i).get("endTime")));		//16
                vpd.put("var17", data.get(i).getString("location"));		//17
                vpd.put("var18",data.get(i).getString("recordTime"));		//18
                varList2.add(vpd);
            }
            dataMap.put("varList2", varList2);
            ObjectExcelViewAllCheck erv = new ObjectExcelViewAllCheck();
            ModelAndView mv = new ModelAndView();
            mv = new ModelAndView(erv,dataMap);
            return mv;
        } catch(Exception e){
            logger.error(e.toString(), e);
            return null;
        }

    }




    /**
     * 获取个人标准
     */
    @RequestMapping({"/getnormal"})
    @ApiOperation("获取个人标准")
    public ResultModel<List<Listout3>> getnormal() throws Exception {
        //USER_ID
        PageData pd = new PageData();
        pd = this.getPageData();
        pd.put("USER_ID",pd.getString("uid"));
        List<PageData> pageData = unitService.listUserExecel(pd);
        if(pageData!=null && pageData.size()>0){
            List<String> ids = new ArrayList<>();
            for(PageData PageDataexecel:pageData){
                ids.add(PageDataexecel.getString("excel_id"));
            }

            String[] strings = new String[ids.size()];
            ids.toArray(strings);
            List<PageData> pageData1 = unitService.listUserExecelsecond(strings);
            List<Listout3> data=new ArrayList<>();
            for(PageData pagedata:pageData1){
                Listout3 listout3 = new Listout3();
                listout3.setChecked(false);
                listout3.setName(pagedata.getString("original_name"));
                data.add(listout3);
            }
            return ResultModel.success(data);
        }
        return  ResultModel.success(new ArrayList<Listout3>());
    }






    @RequestMapping(value="/downloaddatabasecheckone")
    @ApiOperation("导出其中一条检查任务数据")
    public ModelAndView downloaddatabasecheckone(HttpServletResponse response){
        //用户
        PageData pd = new PageData();
        pd = this.getPageData();
        try{
            //检索条件===
            Map<String,Object> dataMap = new HashMap<String,Object>();
            PageData pagedatatask = new PageData();
            pagedatatask.put("uid",pd.getString("USER_ID"));
            pagedatatask.put("page",1);
            pagedatatask.put("size",1000000);
            pagedatatask.put("type",2);
            pagedatatask.put("task_id",pd.getString("id"));
            PageData pageData = taskService.queryTaskToOne(pagedatatask);
            List<TaskCensorRes> taskDetail = taskService.checkingTaskList(pagedatatask);
            List<PageData> data = new LinkedList<>();
            for (TaskCensorRes censorRe : taskDetail) {
                for (CensorRow censorRow : censorRe.getCensorRowList()) {
                    String remark = null;
                    String content = null;
                    String type = null;
                    for (CensorRowIssue censorRowIssue : censorRow.getCensorRowIssueList()) {
                        remark = censorRowIssue.getRemark();
                        content = censorRowIssue.getContent();
                        type = censorRowIssue.getType();
                    }

                    PageData pageDataDate = new PageData();
                    pageDataDate.put("name",pageData.get("taskName"));
                    PageData pageDatacen=new PageData();
                    pageDatacen.put("UNIT_ID", pageData.get("unit_id"));
                    PageData byId = unitService.findById(pageDatacen);
                    pageDataDate.put("unitName", byId.getString("NAME") );
                    pageDatacen.put("UNIT_ID", pageData.get("site_id"));
                    PageData byId1 = unitService.findById(pageDatacen);
                    pageDataDate.put("siteName",byId1.getString("NAME"));
                    pageDataDate.put("normName",pageData.get("normName"));
                    pageDataDate.put("content",censorRow.getRow_content());
                    pageDataDate.put("serialParentName",unitService.queryNormDetailName(censorRow.getId()));
                    pageDataDate.put("serialName",censorRow.getRow_item());
                    pageDataDate.put("issueContent",content);
                    pageDataDate.put("serial",unitService.queryNormDetailSerial(censorRow.getId()));
                    pageDataDate.put("score",censorRow.getScore());
                    pageDataDate.put("issueRemark",remark);
                    if(StringUtils.isNotBlank(type)){
                        if(type.equals("2")){
                            pageDataDate.put("typeName","严重");
                        }else{
                            pageDataDate.put("typeName","一般");
                        }
                    }

                    String s = unitService.querytaskcheckId(censorRe.getCensor_id());
                    PageData checkuser=new PageData();
                    checkuser.put("USER_ID",s);
                    PageData byUiId = userService.findByUiId(checkuser);
                    if(byUiId!=null){
                        pageDataDate.put("userName",byUiId.getString("USERNAME"));
                    }
                    pageDataDate.put("startTime",pageData.get("star_time"));
                    pageDataDate.put("endTime",pageData.get("end_time"));
                    pageDataDate.put("location",pageData.get("location"));
                    pageDataDate.put("recordTime",unitService.queryIssueTime(censorRow.getId()));
                    data.add(pageDataDate);




                }
            }
            System.out.println(data+"\ndata");
            pagedatatask.put("taskCensorRes",data);
            List<String> titles2 = new ArrayList<String>();
            titles2.add("编号"); 		     //1
            titles2.add("任务名称");          //2
            titles2.add("单位名称");			 //3
            titles2.add("子单位");			 //4
            titles2.add("评价准则"); 		 //5
            titles2.add("评价内容");          //6
            titles2.add("所属大类");			  //7
            titles2.add("所属小类");			//8
            titles2.add("问题描述"); 		//9
            titles2.add("序号");            //10
            titles2.add("评分");			//11
            titles2.add("问题备注");			//12
            titles2.add("问题性质");			//13
            titles2.add("检查人员");			//14
            titles2.add("开始时间");			//15
            titles2.add("结束时间");			//16
            titles2.add("检查地点");			//17
            titles2.add("记录时间");			//18
            dataMap.put("titles2", titles2);

            List<PageData> varList2 = new ArrayList<PageData>();
            for(int i=0;i<data.size();i++){
                PageData vpd = new PageData();
                vpd.put("var1", String.valueOf(i));		//1
                vpd.put("var2", data.get(i).getString("name"));	//2
                vpd.put("var3", data.get(i).getString("unitName"));		//3
                vpd.put("var4", data.get(i).getString("siteName"));		//4

                List<String> normlist=(ArrayList<String>)data.get(i).get("normName");
                String name="";
                for(int j=0;j<=normlist.size()-1;j++){
                    if(j < normlist.size()-1){
                        name +=  normlist.get(j)+ ",";
                    }else{
                        name += normlist.get(j);
                    }
                }
                vpd.put("var5",name);		//5
                vpd.put("var6", data.get(i).getString("content"));		//6
                vpd.put("var7", data.get(i).getString("serialParentName"));		//7
                vpd.put("var8", data.get(i).getString("serialName"));		//8
                vpd.put("var9", data.get(i).getString("issueContent"));		//9
                vpd.put("var10", data.get(i).getString("serial"));		//10
                vpd.put("var11", data.get(i).get("score").toString());		//11
                vpd.put("var12", data.get(i).getString("issueRemark"));		//12
                vpd.put("var13", data.get(i).getString("typeName"));		//13
                vpd.put("var14", data.get(i).getString("userName"));		//14
                vpd.put("var15",  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(data.get(i).get("startTime")));		//15
                vpd.put("var16", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(data.get(i).get("endTime")));		//16
                vpd.put("var17", data.get(i).getString("location"));		//17
                vpd.put("var18",data.get(i).getString("recordTime"));		//18
                varList2.add(vpd);
            }
            dataMap.put("varList2", varList2);
            ObjectExcelViewAllCheck erv = new ObjectExcelViewAllCheck();
            ModelAndView mv = new ModelAndView();
            mv = new ModelAndView(erv,dataMap);
            return mv;
        } catch(Exception e){
            logger.error(e.toString(), e);
            return null;
        }
    }



}

