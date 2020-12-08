package com.fh.controller.app;

import com.fh.controller.base.BaseController;
import com.fh.service.app.SettingService;
import com.fh.util.PageData;
import com.fh.util.ResultModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 客户端接口文档
 */
@RestController
@RequestMapping("/api/v1")
@Api(tags = "oh系统配置接口管理")
public class SettingController  extends BaseController {

    @Autowired
    private SettingService settingService;

    /**
     * 查询公司以及单位
     * @throws Exception
     */
    @RequestMapping(value = "/queryUnitAndSite",method = RequestMethod.POST)
    @ApiOperation("查询公司以及单位")
    public ResultModel<List<PageData>> queryUnitAndSite() throws Exception {
        List<PageData> systemSettings = settingService.queryUnitAndSite(this.getPageData()) ;
        return ResultModel.success(systemSettings);
    }

    /**
     * 查询系统配置列表
     * @throws Exception
     */
    @RequestMapping(value = "/findAllSystemSettings",method = RequestMethod.POST)
    @ApiOperation("查询系统配置列表")
    public ResultModel<List<PageData>> findAllSystemSettings() throws Exception {
        List<PageData> systemSettings = settingService.findAllSystemSettings() ;
        return ResultModel.success(systemSettings);
    }

    /**
     * 修改MySQL配置
     * @throws Exception
     */
    @RequestMapping(value = "/updateMySQLSetting",method = RequestMethod.POST)
    @ApiOperation("修改MySQL配置")
    public ResultModel<String> updateMySQLSetting() throws Exception {
        PageData pd = new PageData();
        pd = this.getPageData();
        String message = settingService.updateMySQLSetting(pd);
        return ResultModel.success(message);
    }


    /**
     * 修改Redis配置
     * @throws Exception
     */
    @RequestMapping(value = "/updateRedisSetting",method = RequestMethod.POST)
    @ApiOperation("修改Redis配置")
    public ResultModel<String> updateRedisSetting() throws Exception {
        PageData pd = new PageData();
        pd = this.getPageData();
        String message = null;
        try {
            message=settingService.updateRedisSetting(pd) ;
        }catch (Exception e){
            return ResultModel.failure("系统繁忙请稍后子再试~");
        }
        return ResultModel.success(message);
    }

    /**
     * 修改短信配置
     * @throws Exception 您的验证码是：【变量】。如需帮助请联系客服。
     */
    @RequestMapping(value = "/updateSmsSetting",method = RequestMethod.POST)
    @ApiOperation("修改Redis配置")
    public ResultModel<String> updateSmsSetting() throws Exception {
        PageData pd = new PageData();
        pd = this.getPageData();
        String message = null;
        try {
            message=settingService.updateSmsSetting(pd) ;
        }catch (Exception e){
            return ResultModel.failure("系统繁忙请稍后子再试~");
        }
        return ResultModel.success(message);
    }

    /**
     * 查询单位
     * @throws Exception
     */
    @RequestMapping(value = "/queryUnitAll",method = RequestMethod.POST)
    @ApiOperation("查询单位")
    public ResultModel<List<PageData>> queryUnitAll() throws Exception {
        PageData pd = new PageData();
        pd = this.getPageData();
        List<PageData> data = null;
        try {
            data=settingService.queryUnitAll(pd) ;
        }catch (Exception e){
            return ResultModel.failure("系统繁忙请稍后子再试~");
        }
        return ResultModel.success(data);
    }

    /**
     * 添加单位
     * @throws Exception
     */
    @RequestMapping(value = "/addUnit",method = RequestMethod.POST)
    @ApiOperation("添加单位")
    public ResultModel<String> addUnit() throws Exception {
        PageData pd = new PageData();
        pd = this.getPageData();
        String data = null;
        try {
            data=settingService.addUnit(pd) ;
        }catch (Exception e){
            return ResultModel.failure("系统繁忙请稍后子再试~");
        }
        return ResultModel.success(data);
    }

    /**
     * 修改单位
     * @throws Exception
     */
    @RequestMapping(value = "/updateUnit",method = RequestMethod.POST)
    @ApiOperation("修改单位")
    public ResultModel<String> updateUnit() throws Exception {
        PageData pd = new PageData();
        pd = this.getPageData();
        String data = null;
        try {
            data=settingService.updateUnit(pd) ;
        }catch (Exception e){
            return ResultModel.failure("系统繁忙请稍后子再试~");
        }
        return ResultModel.success(data);
    }

    /**
     * 删除单位
     * @throws Exception
     */
    @RequestMapping(value = "/deleteUnit",method = RequestMethod.POST)
    @ApiOperation("删除单位")
    public ResultModel<String> deleteUnit() throws Exception {
        PageData pd = new PageData();
        pd = this.getPageData();
        String data = null;
        try {
            data=settingService.deleteUnit(pd) ;
        }catch (Exception e){
            return ResultModel.failure("系统繁忙请稍后子再试~");
        }
        return ResultModel.success(data);
    }


    /**
     * 查询站点
     * @throws Exception
     */
    @RequestMapping(value = "/querySiteAll",method = RequestMethod.POST)
    @ApiOperation("查询站点")
    public ResultModel<List<PageData>> querySiteAll() throws Exception {
        PageData pd = new PageData();
        pd = this.getPageData();
        List<PageData> data = null;
        try {
            data=settingService.querySiteAll(pd) ;
        }catch (Exception e){
            return ResultModel.failure("系统繁忙请稍后子再试~");
        }
        return ResultModel.success(data);
    }

    /**
     * 添加站点
     * @throws Exception
     */
    @RequestMapping(value = "/addSite",method = RequestMethod.POST)
    @ApiOperation("添加站点")
    public ResultModel<String> addSite() throws Exception {
        PageData pd = new PageData();
        pd = this.getPageData();
        String data = null;
        try {
            data=settingService.addSite(pd) ;
        }catch (Exception e){
            return ResultModel.failure("系统繁忙请稍后子再试~");
        }
        return ResultModel.success(data);
    }

    /**
     * 修改站点
     * @throws Exception
     */
    @RequestMapping(value = "/updateSite",method = RequestMethod.POST)
    @ApiOperation("修改站点")
    public ResultModel<String> updateSite() throws Exception {
        PageData pd = new PageData();
        pd = this.getPageData();
        String data =null;
        try {
            data=settingService.updateSite(pd) ;
        }catch (Exception e){
            return ResultModel.failure("系统繁忙请稍后子再试~");
        }
        return ResultModel.success(data);
    }

    /**
     * 删除站点
     * @throws Exception
     */
    @RequestMapping(value = "/deleteSite",method = RequestMethod.POST)
    @ApiOperation("删除站点")
    public ResultModel<String> deleteSite() throws Exception {
        PageData pd = new PageData();
        pd = this.getPageData();
        String data =null;
        try {
            data=settingService.deleteSite(pd) ;
        }catch (Exception e){
            return ResultModel.failure("系统繁忙请稍后子再试~");
        }
        return ResultModel.success(data);
    }


    /**
     * 公告列表查询
     * @throws Exception
     */
    @RequestMapping(value = "/queryBulletin",method = RequestMethod.POST)
    @ApiOperation("公告列表查询")
    public ResultModel<List<PageData>> queryBulletin() throws Exception {
        PageData pd = this.getPageData();
        List<PageData> data = settingService.queryBulletin(pd);
        return ResultModel.success(data);
    }

    /**
     * 添加公告
     * @throws Exception
     */
    @RequestMapping(value = "/addBulletin",method = RequestMethod.POST)
    @ApiOperation("添加公告")
    public ResultModel<String> addBulletin() throws Exception {
        PageData pd = this.getPageData();
        String data = settingService.addBulletin(pd);
        return ResultModel.success(data);
    }

    /**
     * 删除公告
     * @throws Exception
     */
    @RequestMapping(value = "/deleteBulletin",method = RequestMethod.POST)
    @ApiOperation("删除公告")
    public ResultModel<String> deleteBulletin() throws Exception {
        PageData pd = this.getPageData();
        String data = settingService.deleteBulletin(pd);
        return ResultModel.success(data);
    }

    /**
     * 查询单个用户的消息
     * @throws Exception
     */
    @RequestMapping(value = "/queryUserByBulletin",method = RequestMethod.POST)
    @ApiOperation("查询单个用户的消息列表")
    public ResultModel<List<PageData>> queryUserByBulletin() throws Exception {
        PageData pd = this.getPageData();
        List<PageData> data = settingService.queryUserByBulletin(pd);
        return ResultModel.success(data);
    }

    /**
     * 查询单个用户的消息数量
     * @throws Exception
     */
    @RequestMapping(value = "/queryUserByBulletinNum",method = RequestMethod.POST)
    @ApiOperation("查询单个用户的消息数量")
    public ResultModel<Integer> queryUserByBulletinNum() throws Exception {
        Integer data = settingService.queryUserByBulletinNum(this.getPageData());
        return ResultModel.success(data);
    }

    /**
     * 公告已读操作
     * @throws Exception
     */
    @RequestMapping(value = "/readByBulletin",method = RequestMethod.POST)
    @ApiOperation("公告已读操作")
    public ResultModel<String> readByBulletin() throws Exception {
        PageData pd = this.getPageData();
        String data = settingService.readByBulletin(pd);
        return ResultModel.success(data);
    }

}
