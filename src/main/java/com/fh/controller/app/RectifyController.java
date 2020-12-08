package com.fh.controller.app;

import com.fh.config.DelayMessageSender;
import com.fh.controller.base.BaseController;
import com.fh.service.app.RectifyService;
import com.fh.util.PageData;
import com.fh.util.ResultModel;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Api(tags = "oh审批流程接口")
public class RectifyController extends BaseController {

    @Autowired
    RectifyService rectifyService;


    /**
     * 查询待处理接口
     *
     * @throws Exception
     */
    @RequestMapping(value = "/queryUserPending", method = RequestMethod.POST)
    @ApiOperation("查询待处理接口")
    public ResultModel<List<PageData>> queryUserByBulletin() throws Exception {
        PageData pd = this.getPageData();
        List<PageData> data = rectifyService.queryUserPending(pd);
        return ResultModel.success(data);
    }

    /**
     * 查询已处理接口
     *
     * @throws Exception
     */
    @RequestMapping(value = "/queryUserProcessed", method = RequestMethod.POST)
    @ApiOperation("查询已处理接口")
    public ResultModel<List<PageData>> queryUserProcessed() throws Exception {
        PageData pd = this.getPageData();
        List<PageData> data = rectifyService.queryUserProcessed(pd);
        return ResultModel.success(data);
    }


    /**
     * 审核消息已读操作
     *
     * @throws Exception
     */
    @RequestMapping(value = "/queryReportMessage", method = RequestMethod.POST)
    @ApiOperation("审核消息已读操作")
    public ResultModel<String> queryReportMessage() throws Exception {
        PageData pd = this.getPageData();
        String data = rectifyService.queryReportMessage(pd);
        return ResultModel.success(data);
    }

    /**
     * 审核通过
     *
     * @throws Exception
     */
    @RequestMapping(value = "/pass", method = RequestMethod.POST)
    @ApiOperation("审核通过")
    public ResultModel<String> pass() throws Exception {
        PageData pd = this.getPageData();
        String data = rectifyService.pass(pd);
        return ResultModel.success(data);
    }

    /**
     * 审核驳回
     *
     * @throws Exception
     */
    @RequestMapping(value = "/reject", method = RequestMethod.POST)
    @ApiOperation("审核驳回")
    public ResultModel<String> reject() throws Exception {
        PageData pd = this.getPageData();
        String data = rectifyService.reject(pd);
        return ResultModel.success(data);
    }

    /**
     * 用户问题列表
     *
     * @throws Exception
     */
    @RequestMapping(value = "/questionsTaskList", method = RequestMethod.POST)
    @ApiOperation("用户问题列表")
    public ResultModel<List<PageData>> questionsTaskList() throws Exception {
        List<PageData> data = rectifyService.questionsTaskList(this.getPageData());
        return ResultModel.success(data);
    }

    /**
     * 任务问题明细
     *
     * @throws Exception
     */
    @RequestMapping(value = "/questionsTaskOne", method = RequestMethod.POST)
    @ApiOperation("任务问题明细")
    public ResultModel<List<PageData>> questionsTaskOne() throws Exception {
        List<PageData> data = rectifyService.questionsTaskOne(this.getPageData());
        return ResultModel.success(data);
    }

    /**
     * 任务问题列表
     *
     * @throws Exception
     */
    @RequestMapping(value = "/questionsList", method = RequestMethod.POST)
    @ApiOperation("任务问题列表")
    public ResultModel<PageData> questionsList() throws Exception {
        PageData data = rectifyService.questionsList(this.getPageData());
        return ResultModel.success(data);
    }


    /**
     * 人员关联
     *
     * @throws Exception
     */
    @RequestMapping(value = "/personnel", method = RequestMethod.POST)
    @ApiOperation("人员关联")
    public ResultModel<List<PageData>> personnel() throws Exception {
        List<PageData> data = rectifyService.personnel(this.getPageData());
        return ResultModel.success(data);
    }

    /**
     * 查询单个任务驳回列表
     *
     * @throws Exception
     */
    @RequestMapping(value = "/queryTaskIssue", method = RequestMethod.POST)
    @ApiOperation("查询单个任务驳回列表")
    public ResultModel<List<PageData>> queryTaskIssue() throws Exception {
        List<PageData> data = rectifyService.queryTaskIssue(this.getPageData());
        return ResultModel.success(data);
    }

    /**
     * 查询多个任务驳回列表
     * @throws Exception
     */
    @RequestMapping(value = "/queryTaskIssueList", method = RequestMethod.POST)
    @ApiOperation("查询多个任务驳回列表")
    public ResultModel<List<PageData>> queryTaskIssueList() throws Exception {
        List<PageData> data = rectifyService.queryTaskIssueList(this.getPageData());
        return ResultModel.success(data);
    }

    /**
     * 专责分配整改
     *
     * @throws Exception
     */
    @RequestMapping(value = "/allocation", method = RequestMethod.POST)
    @ApiOperation("专责分配整改")
    public ResultModel<String> allocation() throws Exception {
        String data = rectifyService.allocation(this.getPageData());
        return ResultModel.success(data);
    }

    /**
     * 组长分配整改列表
     *
     * @throws Exception
     */
    @RequestMapping(value = "/groupAllocationList", method = RequestMethod.POST)
    @ApiOperation("组长分配整改列表")
    public ResultModel<List<PageData>> groupAllocationList() throws Exception {
        List<PageData> data = rectifyService.groupAllocationList(this.getPageData());
        return ResultModel.success(data);
    }

    /**
     * 组长分配整改
     *
     * @throws Exception
     */
    @RequestMapping(value = "/groupAllocation", method = RequestMethod.POST)
    @ApiOperation("组长分配整改")
    public ResultModel<String> groupAllocation() throws Exception {
        String data = rectifyService.groupAllocation(this.getPageData());
        return ResultModel.success(data);
    }

    /**
     * 查询单个用户的任务列表不分页
     *
     * @throws Exception
     */
    @RequestMapping(value = "/queryUserByTask", method = RequestMethod.POST)
    @ApiOperation("驳回分配整改")
    public ResultModel<List<PageData>> queryUserByTask() throws Exception {
        List<PageData> data = rectifyService.queryUserByTask(this.getPageData());
        return ResultModel.success(data);
    }

    /**
     * 查询单个用户下的组员
     *
     * @throws Exception
     */
    @RequestMapping(value = "/queryUserByTaskToUser", method = RequestMethod.POST)
    @ApiOperation("查询单个用户下的组员")
    public ResultModel<List<PageData>> queryUserByTaskToUser() throws Exception {
        List<PageData> data = rectifyService.queryUserByTaskToUser(this.getPageData());
        return ResultModel.success(data);
    }

    /**
     * 查询单个用户的准则列表
     *
     * @throws Exception
     */
    @RequestMapping(value = "/queryUserNormList", method = RequestMethod.POST)
    @ApiOperation("查询单个用户的准则列表")
    public ResultModel<List<PageData>> queryUserNormList() throws Exception {
        List<PageData> data = rectifyService.queryUserNormList(this.getPageData());
        return ResultModel.success(data);
    }

    /**
     * 导出任务问题excel
     *
     * @throws Exception
     */
    @RequestMapping(value = "/exportTaskIssueList")
    @ApiOperation("导出任务问题excel")
    public ModelAndView exportTaskIssueList() throws Exception {
        ModelAndView data = rectifyService.exportTaskIssueList(this.getPageData());
        return data;
    }

}
