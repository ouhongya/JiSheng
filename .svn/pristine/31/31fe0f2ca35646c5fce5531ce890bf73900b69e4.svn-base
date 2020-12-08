package com.fh.controller.app;

import com.fh.controller.base.BaseController;
import com.fh.entity.Page;
import com.fh.entity.app.Excel;
import com.fh.service.app.ExcelSettingService;
import com.fh.util.Const;
import com.fh.util.PageData;
import com.fh.util.ResultModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Api(tags = "ohExcel配置接口管理")
public class ExcelSettingController extends BaseController {


    @Autowired
    private ExcelSettingService  excelSettingService;


    /**
     * 上传Excel导入数据库
     * @throws Exception
     */
    @RequestMapping(value = "/uploadExcel")
    @ApiOperation("上传Excel导入数据库")
    public ResultModel<String> uploadExcel(HttpServletRequest res, @RequestParam("file") MultipartFile file, @RequestParam("uid") String userId) throws Exception     {
        ResultModel<String> stringResultModel = excelSettingService.uploadExcel(res, file, userId);
        return stringResultModel;
    }

    /**
     * 查询Excel列表
     * @throws Exception
     */
    @RequestMapping(value = "/queryExcelList",method = RequestMethod.POST)
    @ApiOperation("查询Excel列表")
    public ResultModel<Page> queryExcelList(Page page) throws Exception {
        PageData pd = new PageData();
        try{
            pd = this.getPageData();
            page.setPd(pd);
            List<PageData> data = excelSettingService.queryExcelList(page);
            page.setPageDataList(data);
            return ResultModel.success(page);
        } catch(Exception e){
            logger.error(e.toString(), e);
            return ResultModel.failure(null);
        }

    }

    /**
     * Excel下发
     * @throws Exception
     */
    @RequestMapping(value = "/allot",method = RequestMethod.POST)
    @ApiOperation("Excel下发")
    public ResultModel<String> allot() throws Exception {
        String data=excelSettingService.allot(this.getPageData());
        return ResultModel.success(data);
    }

    /**
     * 查询下发
     * @throws Exception
     */
    @RequestMapping(value = "/queryAllot",method = RequestMethod.POST)
    @ApiOperation("查询下发")
    public ResultModel<List<String>> queryAllot() throws Exception {
        List<String> data=excelSettingService.queryAllot(this.getPageData());
        return ResultModel.success(data);
    }

    /**
     * 修改下发
     * @throws Exception
     */
    @RequestMapping(value = "/updateAllot",method = RequestMethod.POST)
    @ApiOperation("修改下发")
    public ResultModel<String> updateAllot() throws Exception {
        String data=excelSettingService.updateAllot(this.getPageData());
        return ResultModel.success(data);
    }

    /**
     * 启用Excel 1启用
     * @throws Exception
     */
    @RequestMapping(value = "disableExcel",method = RequestMethod.POST)
    @ApiOperation("启用Excel 1启用")
    public ResultModel<String> disable() throws Exception {
       String data = excelSettingService.disable(this.getPageData());
        return ResultModel.success(data);
    }

    /**
     * 停用Excel 2停用
     * @throws Exception
     */
    @RequestMapping(value = "enableExcel",method = RequestMethod.POST)
    @ApiOperation("启用Excel 2停用")
    public ResultModel<String> enable() throws Exception {
        String data = excelSettingService.enable(this.getPageData());
        return ResultModel.success(data);
    }

    /**
     * 批量删除Excel 3
     * @throws Exception
     */
    @RequestMapping(value = "/deleteExcel",method = RequestMethod.POST)
    @ApiOperation("删除Excel")
    public ResultModel<String> deleteExcel() throws Exception {
        String systemSettings=excelSettingService.deleteExcel(this.getPageData());
        return ResultModel.success(systemSettings);
    }

    /**
     * Excel重命名
     * @throws Exception
     */
    @RequestMapping(value = "rename",method = RequestMethod.POST)
    @ApiOperation("Excel重命名")
    public ResultModel<String> rename() throws Exception {
        String data =excelSettingService.rename(this.getPageData());
        return ResultModel.success(data);
    }

    /**
     * 查询Excel一二三级
     * @throws Exception
     */
    @RequestMapping(value = "/queryExcelOneAndTwo",method = RequestMethod.POST)
    @ApiOperation("查询Excel一二级")
    public ResultModel<List<Excel>> queryExcelOneAndTwo() throws Exception {
        List<Excel> data=excelSettingService.queryExcelOneAndTwo(this.getPageData());
        return ResultModel.success(data);
    }

    /**
     * 查询专责和组长列表
     * @throws Exception
     */
    @RequestMapping(value = "/queryUserBySpecialty",method = RequestMethod.POST)
    @ApiOperation("查询专责和组长列表")
    public ResultModel<PageData> queryUserBySpecialty() throws Exception {
        PageData data=excelSettingService.queryUserBySpecialty(this.getPageData());
        return ResultModel.success(data);
    }


    /**
     * 下载模板
     * @throws Exception
     */
    @RequestMapping(value = "/downloadTemplate")
    @ApiOperation("下载模板")
    public HttpServletResponse downloadTemplate(HttpServletRequest request,HttpServletResponse response) throws Exception {
        HttpServletResponse data=excelSettingService.downloadTemplate(request,response);
        return data;
    }

    /**
     * 模板上传
     * @throws Exception
     */
    @RequestMapping(value = "/uploadTemplate",method = RequestMethod.POST)
    @ApiOperation("模板上传")
    public ResultModel<String> uploadTemplate(@RequestParam("template") MultipartFile multipartFile) throws Exception {
        String data=excelSettingService.uploadTemplate(multipartFile);
        return ResultModel.success(data);
    }


}
