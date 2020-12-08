package com.fh.controller.app;

import com.fh.controller.base.BaseController;
import com.fh.service.app.StatisticsService;
import com.fh.util.PageData;
import com.fh.util.ResultModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 客户端接口文档
 */
@RestController
@RequestMapping("/api/v1")
@Api(tags = "oh统计接口管理")
public class StatisticsController extends BaseController {

    @Resource(name = "redisTemplate")
    private RedisTemplate redisTemplate;

    @Autowired
    StatisticsService statisticsService;

    /**
     * 统计图列表
     *
     * @throws Exception
     */
    @RequestMapping(value = "/queryStatisticsList", method = RequestMethod.POST)
    @ApiOperation("统计图列表")
    public ResultModel<PageData> queryStatisticsList() throws Exception {
        PageData pageDataList = statisticsService.queryStatisticsList(this.getPageData());
        return ResultModel.success(pageDataList);
    }

    /**
     * 总体完成明细查询
     * @throws Exception
     */
    @RequestMapping(value = "/queryTaskOverDetail", method = RequestMethod.POST)
    @ApiOperation("总体完成明细查询")
    public ResultModel<List<PageData>> queryTaskOverDetail() throws Exception {
        List<PageData> pageDataList = statisticsService.queryTaskOverDetail(this.getPageData());
        return ResultModel.success(pageDataList);
    }

    /**
     * 工作量计算
     * @throws Exception
     */
    @RequestMapping(value = "/queryWorkload", method = RequestMethod.POST)
    @ApiOperation("工作量计算")
    public ResultModel<List<PageData>> queryWorkload() throws Exception {
        List<PageData> pageDataList = statisticsService.queryWorkload(this.getPageData());
        return ResultModel.success(pageDataList);
    }

    /**
     * 查询单位列表
     *
     * @throws Exception
     */
    @RequestMapping(value = "/queryUnitList", method = RequestMethod.POST)
    @ApiOperation("查询单位列表")
    public ResultModel<List<PageData>> queryUnitList() throws Exception {
        return ResultModel.success(statisticsService.queryUnitList(this.getPageData()));
    }

}
