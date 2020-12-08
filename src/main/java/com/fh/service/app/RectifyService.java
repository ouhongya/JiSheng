package com.fh.service.app;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fh.dao.DaoSupport;
import com.fh.entity.Page;
import com.fh.entity.app.*;
import com.fh.entity.requestVo.TaskDetailUserVo;
import com.fh.service.app.unit.UnitService;
import com.fh.service.system.user.UserService;
import com.fh.util.ObjectExcelViewAllCheck;
import com.fh.util.PageData;
import com.fh.util.UuidUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("RectifyService")
public class RectifyService {

    @Resource(name = "daoSupport")
    private DaoSupport dao;

    @Resource(name = "unitService")
    private UnitService unitService;

    @Resource(name = "userService")
    private UserService userService;


    @Autowired
    TaskService taskService;

    /**
     * 查询待处理接口
     *
     * @param pd
     * @return
     */
    public List<PageData> queryUserPending(PageData pd) throws Exception {
        Page page = new Page();
        //查询用户的权限
        Integer roleId = (Integer) dao.findForObject("RectifyMapper.queryUserByRoue", pd);
        page.setCurrentPage(Integer.parseInt(pd.get("page").toString()));
        page.setShowCount(Integer.parseInt(pd.get("size").toString()));
        page.setPd(pd);
        List<PageData> forList = (List<PageData>) dao.findForList("RectifyMapper.datalistPage1", page);
        if (forList.size() != 0) {
            for (PageData pageData : forList) {
                pageData.put("viewName", (String) dao.findForObject("RectifyMapper.queryUserName", (String) pageData.get("view_id")));
                pageData.put("groupName", (String) dao.findForObject("RectifyMapper.queryUserName", (String) pageData.get("group_id")));
                pageData.put("unitName", (String) dao.findForObject("RectifyMapper.queryUnitName", (String) pageData.get("unit_id")));
                pageData.put("siteName", (String) dao.findForObject("RectifyMapper.queryUnitName", (String) pageData.get("seit_id")));
                pageData.put("censorName", (String) dao.findForObject("RectifyMapper.queryCensorName", (String) pageData.get("id")));
                List<PageData> task_id = (List<PageData>) dao.findForList("RectifyMapper.queryExcelName", (String) pageData.get("task_id"));
                String str = "";
                for (PageData data : task_id) {
                    str += data.get("new_name") + ",";
                }
                pageData.put("excelName", str.substring(0, str.length() - 1));
                pageData.put("uid", pd.get("uid"));
                pageData.put("numList", personnel(pageData));
            }
            forList.get(0).put("total", page.getTotalPage());
        }
        return forList;
    }


    /**
     * 查询已处理接口
     *
     * @param pd
     * @return
     */
    public List<PageData> queryUserProcessed(PageData pd) throws Exception {
        Page page = new Page();
        //查询用户的权限
        Integer roleId = (Integer) dao.findForObject("RectifyMapper.queryUserByRoue", pd);
        page.setCurrentPage(Integer.parseInt(pd.get("page").toString()));
        page.setShowCount(Integer.parseInt(pd.get("size").toString()));
        page.setPd(pd);
        List<PageData> forList = (List<PageData>) dao.findForList("RectifyMapper.datalistPage2", page);
        if (forList.size() != 0) {
            for (PageData pageData : forList) {
                pageData.put("viewName", (String) dao.findForObject("RectifyMapper.queryUserName", (String) pageData.get("view_id")));
                pageData.put("groupName", (String) dao.findForObject("RectifyMapper.queryUserName", (String) pageData.get("group_id")));
                pageData.put("unitName", (String) dao.findForObject("RectifyMapper.queryUnitName", (String) pageData.get("unit_id")));
                pageData.put("siteName", (String) dao.findForObject("RectifyMapper.queryUnitName", (String) pageData.get("seit_id")));
                pageData.put("censorName", (String) dao.findForObject("RectifyMapper.queryCensorName", (String) pageData.get("id")));
                List<PageData> task_id = (List<PageData>) dao.findForList("RectifyMapper.queryExcelName", (String) pageData.get("task_id"));
                String str = "";
                for (PageData data : task_id) {
                    str += data.get("new_name") + ",";
                }
                pageData.put("excelName", str.substring(0, str.length() - 1));
                pageData.put("uid", pd.get("uid"));
                pageData.put("numList", personnel(pageData));
            }
            forList.get(0).put("total", page.getTotalPage());
        }
        return forList;
    }


    /**
     * 审核消息已读操作
     *
     * @param pd
     * @return
     */
    public String queryReportMessage(PageData pd) throws Exception {
        dao.update("RectifyMapper.queryReportMessage", pd);
        return "操作成功";
    }

    /**
     * 通过操作
     *
     * @return
     */
    public String pass(PageData pd) throws Exception {
        //查询当前的代办表数据
        PageData data = (PageData) dao.findForObject("RectifyMapper.queryTaskReport", pd);
        if (data != null) {
            //查询当前的检查者的id
            data.put("uid", pd.get("uid"));
            String censorId = (String) dao.findForObject("RectifyMapper.queryTaskCensorIds", data);
            //当前用户的权限
            Integer function = (Integer) dao.findForObject("TaskMapper.queryUserByRoue", pd.get("uid"));
            int flag = Integer.parseInt(String.valueOf(data.get("flag")));
            if (function == 1) {
                if (flag == 1) {
                    pd.put("status", 10);
                    pd.put("task_id", data.get("task_id"));
                    //组长的通过
                    dao.update("RectifyMapper.updateTaskStatus", pd);
                } else if (flag == 2) {
                    ////查看项目是否已全部整改完成
                    pd.put("status", 9);
                    pd.put("censorId", censorId);
                    //组员的通过
                    dao.update("RectifyMapper.updateTaskCensorStatus", pd);
                }
            } else if (function == 2) {
                String userId = (String) dao.findForObject("RectifyMapper.queryTaskCensorIds1", data);
                //查看项目是否已全部整改完成
                pd.put("status", 9);
                pd.put("censorId", userId);
                //组员的通过
                dao.update("RectifyMapper.updateTaskCensorStatus", pd);
            }
            pd.put("status", 2);
            pd.put("remark", null);
            dao.update("RectifyMapper.updateTaskReportRemarkAndStatus", pd);
        }
        return "操作成功";
    }

    /**
     * 审核驳回
     *
     * @param pd
     * @return
     */
    public String reject(PageData pd) throws Exception {
        //查询当前的代办表数据
        PageData data = (PageData) dao.findForObject("RectifyMapper.queryTaskReport", pd);
        if (data != null) {
            //当前用户的权限
            Integer function = (Integer) dao.findForObject("TaskMapper.queryUserByRoue", pd.get("uid"));
            int flag = Integer.parseInt(String.valueOf(data.get("flag")));
            if (function == 1) {
                if (flag == 1) {
                    pd.put("status", 10);
                    //组长的通过
                    dao.update("RectifyMapper.updateTaskStatus", pd);
                } else if (flag == 2) {
                    //查看项目是否已全部整改完成
                    pd.put("status", 9);
                    //组员的通过
                    dao.update("RectifyMapper.updateTaskCensorStatus", pd);
                }
            } else if (function == 2) {
                //查看项目是否已全部整改完成
                pd.put("status", 10);
                //查询检查表id
                String userId = (String) dao.findForObject("RectifyMapper.queryTaskCensorIds1", data);
                pd.put("censorId", userId);
                //组员的通过
                dao.update("RectifyMapper.updateTaskCensorStatus", pd);
            }
            pd.put("status", 3);
            pd.put("remark", pd.get("content"));

            dao.update("RectifyMapper.updateTaskReportRemarkAndStatus", pd);
        }
        return "驳回成功";
    }

    /**
     * 用户问题列表
     * uid 用户id
     * taskIds: 任务id集合
     * type: 状态
     * endTime: 开始时间
     * starTime: 结束时间
     * userIds: 用户列表
     * normIds: 标准列表
     *
     * @param pd
     * @return
     */
    public synchronized List<PageData> questionsTaskList(PageData pd) throws Exception {
        IssueVo params = JSON.parseObject(String.valueOf(pd.get("params")), IssueVo.class);
        Integer functionId = (Integer) dao.findForObject("RectifyMapper.queryUserByRoue", pd.get("uid"));
        if (functionId == 1) {
            params.setType(1);
        }
        if (params.getTaskId().size() == 0) {
            List list = new ArrayList<>();
            List<PageData> dataList = queryUserByTask(pd);
            for (PageData datum : dataList) {
                list.add(datum.get("id"));
            }
            params.setTaskId(list);
        }
        if (params.getUserId().size() == 0) {
            List<String> list = new ArrayList<>();
            List<PageData> dataList = queryUserByTaskToUser(pd);
            for (PageData datum : dataList) {
                list.add((String) datum.get("USER_ID"));
            }
            List<String> strings = new ArrayList<String>(new LinkedHashSet<>(list));
            params.setUserId(strings);
        }
        if (params.getNormId().size() == 0) {
            List<String> list = new ArrayList<>();
            List<PageData> dataList = queryUserNormList(pd);
            for (PageData datum : dataList) {
                list.add((String) datum.get("id"));
            }
            List<String> strings = new ArrayList<String>(new LinkedHashSet<>(list));
            params.setNormId(strings);
        }
        PageHelper.startPage(Integer.valueOf(pd.getString("pageNo")), Integer.valueOf(pd.getString("pageSize")));
        List<PageData> forList = (List<PageData>) dao.findForList("RectifyMapper.questionsTaskList", params);
        PageInfo<PageData> pageInfo = new PageInfo<PageData>(forList);
        if (forList.size() != 0) {
            for (PageData data : forList) {
                //准则列表
                data.put("normName", dao.findForList("RectifyMapper.queryTaskToNorm", data.get("id")));
                //问题详情列表
                List<PageData> pageDataList = new ArrayList<PageData>();
                data.put("issueList", pageDataList);
                List<CensorRow> censorRows = null;
                if (functionId == 1) {
                    censorRows = (List<CensorRow>) dao.findForList("RectifyMapper.censorRowList", data.get("id"));
                } else {
                    PageData pageData = new PageData();
                    pageData.put("uid", pd.get("uid"));
                    pageData.put("id", data.get("id"));
                    List<String> objects = new ArrayList<>();
                    List<PageData> daoForObject = (List<PageData>) dao.findForList("RectifyMapper.queryIssueCensorList", pageData);
                    for (PageData pageData1 : daoForObject) {
                        objects.add((String) pageData1.get("id"));
                    }
                    List<String> pageData1 = new ArrayList<String>(new LinkedHashSet<>(objects));
                    censorRows = (List<CensorRow>) dao.findForList("RectifyMapper.censorRowList1", pageData1);
                    censorRows.stream().filter(distinctByKey(b -> b.getId()));
                }
                if (censorRows.size() != 0) {
                    for (CensorRow row : censorRows) {
                        List<CensorRowIssue> rowIssueList = row.getCensorRowIssueList();
                        for (CensorRowIssue censorRowIssue : rowIssueList) {
                            PageData issueDate = new PageData();
                            issueDate.put("serial", censorRowIssue.getSerial());
                            issueDate.put("status", data.get("STATUS"));
                            issueDate.put("item", row.getRow_item());
                            issueDate.put("issueContent", censorRowIssue.getContent());
                            issueDate.put("issueUserName", dao.findForObject("RectifyMapper.RowUserName", row.getTask_detail_id()));
                            issueDate.put("normName", censorRowIssue.getName());
                            issueDate.put("rectifyName", null);
                            issueDate.put("score", censorRowIssue.getScore());
                            issueDate.put("issueImage", censorRowIssue.getCensorRowIssueImageList());
                            issueDate.put("remark", censorRowIssue.getRemark());
                            issueDate.put("rectifyImage", null);
                            issueDate.put("rectifyRemark", null);
                            issueDate.put("operatingTime", row.getOperating_time());
                            issueDate.put("unitName", dao.findForObject("RectifyMapper.queryTaskToUnit", data.get("unit_id")));
                            pageDataList.add(issueDate);
                        }
                        data.put("issueList", pageDataList);
                    }
                }
            }
            forList.get(0).put("pages", pageInfo.getPages());
            forList.get(0).put("pageNum", pageInfo.getPageNum());
            forList.get(0).put("pageSize", pageInfo.getPageSize());
        }
        return forList;
    }

    /**
     * 任务问题明细
     *
     * @param pd
     * @return
     * @throws Exception
     */
    public synchronized List<PageData> questionsTaskOne(PageData pd) throws Exception {
        Integer functionId = (Integer) dao.findForObject("RectifyMapper.queryUserByRoue", pd.get("uid"));
        List<PageData> forList = (List<PageData>) dao.findForList("RectifyMapper.questionsTaskOne", pd);
        if (forList.size() != 0) {
            for (PageData data : forList) {
                //准则列表
                data.put("normName", dao.findForList("RectifyMapper.queryTaskToNorm", data.get("id")));
                //问题详情列表
                List<PageData> pageDataList = new ArrayList<PageData>();
                List<CensorRow> censorRows = null;
                if (functionId == 1) {
                    censorRows = (List<CensorRow>) dao.findForList("RectifyMapper.censorRowList", data.get("id"));
                } else {
                    PageData pageData = new PageData();
                    pageData.put("uid", pd.get("uid"));
                    pageData.put("id", data.get("id"));
                    List<String> objects = new ArrayList<>();
                    List<PageData> daoForObject = (List<PageData>) dao.findForList("RectifyMapper.queryIssueCensorList", pageData);
                    for (PageData pageData1 : daoForObject) {
                        objects.add((String) pageData1.get("id"));
                    }
                    List<String> pageData1 = new ArrayList<String>(new LinkedHashSet<>(objects));
                    censorRows = (List<CensorRow>) dao.findForList("RectifyMapper.censorRowList1", pageData1);
                    censorRows.stream().filter(distinctByKey(b -> b.getId()));
                }
                if (censorRows.size() != 0) {
                    for (CensorRow row : censorRows) {
                        List<CensorRowIssue> rowIssueList = row.getCensorRowIssueList();
                        for (CensorRowIssue censorRowIssue : rowIssueList) {
                            PageData issueDate = new PageData();
                            issueDate.put("serial", censorRowIssue.getSerial());
                            issueDate.put("status", data.get("STATUS"));
                            issueDate.put("item", row.getRow_item());
                            issueDate.put("issueContent", censorRowIssue.getContent());
                            issueDate.put("issueUserName", dao.findForObject("RectifyMapper.RowUserName", row.getTask_detail_id()));
                            issueDate.put("normName", censorRowIssue.getName());
                            issueDate.put("rectifyName", null);
                            issueDate.put("score", censorRowIssue.getScore());
                            issueDate.put("issueImage", censorRowIssue.getCensorRowIssueImageList());
                            issueDate.put("remark", censorRowIssue.getRemark());
                            issueDate.put("rectifyImage", null);
                            issueDate.put("rectifyRemark", null);
                            issueDate.put("operatingTime", row.getOperating_time());
                            issueDate.put("unitName", dao.findForObject("RectifyMapper.queryTaskToUnit", data.get("unit_id")));
                            pageDataList.add(issueDate);
                        }
                        data.put("issueList", pageDataList);
                    }
                }
            }
        }
        return forList;
    }

    /**
     * 任务问题列表
     *
     * @param pd
     * @return
     */
    public PageData questionsList(PageData pd) throws Exception {
        //查询当前任务下的所有的问题
        List<PageData> issueData = (List<PageData>) dao.findForList("RectifyMapper.queryOneTaskIssueList", pd);
        //组装数据
        List<Norm> normList = (List<Norm>) dao.findForList("TaskMapper.queryUnitAndSite", null);
        List<String> normIds = new ArrayList<>();
        List<String> normDetailIds = new ArrayList<>();
        for (PageData issueDatum : issueData) {
            normIds.add((String) issueDatum.get("norm_id"));
            normDetailIds.add((String) issueDatum.get("norm_detail_id"));
        }
        List<String> list = removeDuplicate(normIds);
        List<String> duplicate = removeDuplicate(normDetailIds);
        PageData data = new PageData();
        PageData normPd = new PageData();
        List<Map<String, String>> pageDataList = new ArrayList<Map<String, String>>();
        for (Norm norm : normList) {
            if (list.contains(norm.getId())) {
                Map<String, Object> normMap = new HashMap<>();
                normMap.put("id", norm.getId());
                normMap.put("normName", norm.getName());
                List<Map<String, String>> listMap = new ArrayList<Map<String, String>>();
                for (NormDetail normDetail : norm.getNormDetails()) {
                    if (duplicate.contains(normDetail.getId())) {
                        Map<String, String> map = new HashMap<>();
                        map.put("id", normDetail.getId());
                        map.put("normDetailName", normDetail.getItem());
                        listMap.add(map);
                    }
                }
                normMap.put("normDetail", listMap);
                pageDataList.add(normPd);
            }
        }
        data.put("normList", pageDataList);
        PageData unitPage = (PageData) dao.findForObject("RectifyMapper.queryOneTaskIssueList", pd.get("unit_id").toString());
        data.put("unitName", unitPage.get("NAME"));
        return data;
    }


    /**
     * 人员关联
     * report_id 上报表id
     * title: '整改人员',
     * name: '张三',
     * date: '1581170184'
     *
     * @param pd
     * @return
     */
    public List<PageData> personnel(PageData pd) throws Exception {
        List<PageData> pageData = new ArrayList<>();
        String censorName = "";
        String censorDate = "";
        String reportName = "";
        String reportDate = "";
        String rectifyName = "";
        String rectifyDate = "";
        String reviewName = "";
        String reviewDate = "";
        //正常上报
        if (new Double(pd.get("flag").toString()).intValue() == 1) {
            //组员上报
            if (new Double(pd.get("type").toString()).intValue() == 1) {
                String userName = (String) dao.findForObject("RectifyMapper.queryReportCensorUser", pd.get("group_id"));
                censorName = userName;
                censorDate = (String) dao.findForObject("RectifyMapper.queryCensorDateTime2", pd);
                reportName = userName;
                reportDate = (String) dao.findForObject("RectifyMapper.queryCensorDateTime4", pd);
                rectifyName = null;
                rectifyDate = null;
                reviewName = (String) dao.findForObject("RectifyMapper.queryReportCensorUser", pd.get("user_id"));
                reviewDate = (String) dao.findForObject("RectifyMapper.queryCensorDateTime1", pd.get("id"));
            } else {
                List<PageData> forList = (List<PageData>) dao.findForList("RectifyMapper.queryReportCensorUser3", pd);
                String name = "";
                for (PageData data : forList) {
                    if (data != null) {
                        name += data.get("NAME") + ",";
                    }
                }
                censorName = name.substring(0, name.length() - 1);
                censorDate = (String) dao.findForObject("RectifyMapper.queryCensorDateTime3", pd);
                reportName = (String) dao.findForObject("RectifyMapper.queryReportCensorUser", pd.get("group_id"));
                reportDate = (String) dao.findForObject("RectifyMapper.queryCensorDateTime1", pd.get("id"));
                rectifyName = null;
                rectifyDate = null;
                reviewName = (String) dao.findForObject("RectifyMapper.queryReportCensorUser", pd.get("user_id"));
                reviewDate = (String) dao.findForObject("RectifyMapper.queryCensorDateTime11", pd.get("id"));
            }
        } else {
            //组员上报
            if (new Double(pd.get("type").toString()).intValue() == 1) {
                String userName = (String) dao.findForObject("RectifyMapper.queryReportCensorUser", pd.get("group_id"));
                censorName = userName;
                censorDate = (String) dao.findForObject("RectifyMapper.queryCensorDateTime2", pd);
                reportName = userName;
                reportDate = (String) dao.findForObject("RectifyMapper.queryCensorDateTime4", pd);
                rectifyName = userName;
                rectifyDate = (String) dao.findForObject("RectifyMapper.queryCensorDateTime2", pd);
                if (new Double(pd.get("status").toString()).intValue() != 2) {
                    reviewName = (String) dao.findForObject("RectifyMapper.queryReportCensorUser", pd.get("user_id"));
                    reviewDate = (String) dao.findForObject("RectifyMapper.queryCensorDateTime1", pd.get("id"));
                }
            } else {
                List<PageData> forList = (List<PageData>) dao.findForList("RectifyMapper.queryReportCensorUser3", pd);
                String name = "";
                for (PageData data : forList) {
                    name += data.get("NAME") + ",";
                }
                censorName = name.substring(0, name.length() - 1);
                censorDate = (String) dao.findForObject("RectifyMapper.queryCensorDateTime3", pd);
                reportName = (String) dao.findForObject("RectifyMapper.queryReportCensorUser", pd.get("group_id"));
                reportDate = (String) dao.findForObject("RectifyMapper.queryCensorDateTime1", pd.get("id"));
                rectifyName = (String) dao.findForObject("RectifyMapper.queryReportCensorUser", pd.get("group_id"));
                rectifyDate = (String) dao.findForObject("RectifyMapper.queryCensorDateTime3", pd);
                if (new Double(pd.get("status").toString()).intValue() != 2) {
                    reviewName = (String) dao.findForObject("RectifyMapper.queryReportCensorUser", pd.get("user_id"));
                    reviewDate = (String) dao.findForObject("RectifyMapper.queryCensorDateTime1", pd.get("id"));
                }
            }
        }
        PageData map1 = new PageData();
        map1.put("title", "检查人员");
        map1.put("name", censorName);
        map1.put("date", censorDate);
        pageData.add(map1);
        PageData map2 = new PageData();
        map2.put("title", "上报人员");
        map2.put("name", reportName);
        map2.put("date", reportDate);
        pageData.add(map2);
        PageData map3 = new PageData();
        map3.put("title", "分配整改");
        map3.put("name", rectifyName);
        map3.put("date", rectifyDate);
        pageData.add(map3);
        PageData map4 = new PageData();
        map4.put("title", "审核人员");
        map4.put("name", reviewName);
        map4.put("date", reviewDate);
        pageData.add(map4);
        return pageData;
    }


    /**
     * 查询单个任务驳回列表
     *
     * @param pd
     * @return id:任务id
     * @throws Exception
     */
    public List<PageData> queryTaskIssue(PageData pd) throws Exception {
        //驳回专责才有的功能
        //查询当前任务下面有问题的模块
        List<PageData> issueModelList = (List<PageData>) dao.findForList("RectifyMapper.queryTaskIssueModel", pd.get("id"));
        List<String> normIds = new ArrayList<>();
        List<String> normDetailIds = new ArrayList<>();
        for (PageData data : issueModelList) {
            normIds.add(String.valueOf(data.get("norm_id")));
            normDetailIds.add(String.valueOf(data.get("norm_detail_id")));
        }
        //去重
        List<String> normId = new ArrayList<>(new LinkedHashSet<>(normIds));
        List<String> normDetailId = new ArrayList<>(new LinkedHashSet<>(normDetailIds));
        //拿到当前数据库所有的标准判断取值
        List<Norm> normList = (List<Norm>) dao.findForList("TaskMapper.queryUnitAndSite", null);
        //去查询准则列表
        List<PageData> excelList = (List<PageData>) dao.findForList("RectifyMapper.queryExcelList", null);
        List<PageData> normListDerail = new ArrayList<>();
        List<PageData> pageDataVo = new ArrayList<>();
        for (Norm norm : normList) {
            if (normId.contains(norm.getId())) {
                PageData normPageData = new PageData();
                normPageData.put("normName", norm.getName());
                normPageData.put("id", norm.getId());
                normPageData.put("excelId", norm.getExcel_id());
                List<PageData> normDetailData = new ArrayList<>();
                for (NormDetail normDetail : norm.getNormDetails()) {
                    if (normDetailId.contains(normDetail.getId())) {
                        PageData normDetailPage = new PageData();
                        normDetailPage.put("id", normDetail.getId());
                        normDetailPage.put("normDetailName", normDetail.getItem());
                        normDetailData.add(normDetailPage);
                    }
                }
                normPageData.put("normDetail", normDetailData);
                normListDerail.add(normPageData);
            }
        }
        for (PageData data : excelList) {
            for (PageData normDetail : normListDerail) {
                if (String.valueOf(data.get("id")).equals(String.valueOf(normDetail.get("excelId")))) {
                    PageData data1 = new PageData();
                    data1.put("excelName", data.get("new_name"));
                    data1.put("id", data.get("id"));
                    List<PageData> list = new ArrayList<>();
                    list.add(normDetail);
                    data1.put("normList", list);
                    pageDataVo.add(data1);
                }
            }
        }
        //处理重复结果
        removeDuplicate(pageDataVo);
        return pageDataVo;
    }

    /**
     * 查询多个任务驳回列表
     *
     * @param pd uid
     * @return id:任务id
     * @throws Exception
     */
    public List<PageData> queryTaskIssueList(PageData pd) throws Exception {
        //查询专责下面为下发的准则
        List<PageData> forList = (List<PageData>) dao.findForList("RectifyMapper.queryUserTaskIssueList", pd.get("uid"));
        List<String> taskIds = new ArrayList<>();
        for (PageData data : forList) {
            taskIds.add(data.get("id").toString());
        }
        //查询当前任务下面有问题的模块
        List<PageData> issueModelList = (List<PageData>) dao.findForList("RectifyMapper.queryTaskIssueModelList", taskIds);
        List<String> normIds = new ArrayList<>();
        List<String> normDetailIds = new ArrayList<>();
        for (PageData data : issueModelList) {
            normIds.add(String.valueOf(data.get("norm_id")));
            normDetailIds.add(String.valueOf(data.get("norm_detail_id")));
        }
        //去重
        List<String> normId = new ArrayList<>(new LinkedHashSet<>(normIds));
        List<String> normDetailId = new ArrayList<>(new LinkedHashSet<>(normDetailIds));
        //拿到当前数据库所有的标准判断取值
        List<Norm> normList = (List<Norm>) dao.findForList("TaskMapper.queryUnitAndSite", null);
        //去查询准则列表
        List<PageData> excelList = (List<PageData>) dao.findForList("TaskMapper.queryExcelList", null);
        List<PageData> normListDerail = new ArrayList<>();
        List<PageData> pageDataVo = new ArrayList<>();
        for (Norm norm : normList) {
            if (normId.contains(norm.getId())) {
                PageData normPageData = new PageData();
                normPageData.put("normName", norm.getName());
                normPageData.put("id", norm.getId());
                List<PageData> normDetailData = new ArrayList<>();
                for (NormDetail normDetail : norm.getNormDetails()) {
                    if (normDetailId.contains(normDetail.getId())) {
                        PageData normDetailPage = new PageData();
                        normDetailPage.put("id", normDetail.getId());
                        normDetailPage.put("normDetailName", normDetail.getItem());
                        normDetailData.add(normDetailPage);
                    }
                }
                normPageData.put("normDetail", normDetailData);
                normListDerail.add(normPageData);
            }
        }
        for (PageData data : excelList) {
            for (PageData normDetail : normListDerail) {
                if (String.valueOf(data.get("id")).equals(String.valueOf(normDetail.get("excelId")))) {
                    PageData data1 = new PageData();
                    data1.put("excelName", data.get("new_name"));
                    data1.put("id", data.get("id"));
                    data1.put("normList", normDetail);
                    pageDataVo.add(data1);
                }
            }
        }
        //处理重复结果
        List<String> excelIds = new ArrayList<>();
        for (PageData data : pageDataVo) {
            excelIds.add(String.valueOf(data.get("id")));
        }
        List<PageData> list = new ArrayList<>();
        List<String> arrayList = new ArrayList<>(new LinkedHashSet<>(excelIds));
        for (PageData data : pageDataVo) {
            if (arrayList.contains(String.valueOf(data.get("id")))) {
                PageData pageData1 = new PageData();
                pageData1.put("excelId", data.get("id"));
                pageData1.put("normList", data.get("normList"));
                list.add(pageData1);
            }
        }
        for (PageData data : excelList) {
            for (PageData pageData1 : list) {
                if (data.get("id").equals(pageData1.get("excelId"))) {
                    pageData1.put("excelName", data.get("new_name"));
                }
            }
        }
        return list;
    }

    /**
     * 专责分配整改
     *
     * @param pd
     * @return
     */
    public synchronized String allocation(PageData pd) throws Exception {
        IssueListVo param = JSONObject.parseObject(String.valueOf(pd.get("param")), IssueListVo.class);
        //查询之前任务的记录
        List<String> taskId = param.getId();
        PageData task = null;
        if (taskId.size() == 1) {
            String s = taskId.get(0);
            task = (PageData) dao.findForObject("RectifyMapper.queryTaskToOne", s);
        }
        List<issueList> detail = param.getIssueDetail();
        Integer totalScore = 0;
        Integer totalCensor = 0;
        //任务表id
        //查询任务下的检查行和检查详情
        List<DetailRow> issueLists = (List<DetailRow>) dao.findForList("RectifyMapper.queryUserTaskIssueLists", taskId);
        //查询关联的excel
        List<String> normIds = new ArrayList<>();
        for (DetailRow issueList : issueLists) {
            normIds.add(issueList.getNorm_id());
        }
        List<String> strings1 = new ArrayList<>(new LinkedHashSet<>(normIds));
        //查询excel
        List<String> excelIds = new ArrayList<>();
        List<PageData> excelList = (List<PageData>) dao.findForList("RectifyMapper.queryTaskExcelList", strings1);
        for (PageData data : excelList) {
            excelIds.add(data.get("id").toString());
        }
        //用户列表
        List<String> userIds = new ArrayList<>();
        String task_id = UuidUtil.get32UUID();
        List<Norm> normList = (List<Norm>) dao.findForList("TaskMapper.queryUnitAndSite", null);
        //查询到当前Excel数据集
        for (issueList requestParamTaskDetail : detail) {
            userIds.add(requestParamTaskDetail.getUserId());
            //检查表id
            String censorId = UuidUtil.get32UUID();
            //单个用户检查总分
            Integer userTotalScore = 0;
            //单个用户检查项目数
            Integer userTotalCensor = 0;
            List<String> module = requestParamTaskDetail.getModule();
            for (String moduleId : module) {
                for (DetailRow issue : issueLists) {
                    if (issue.getNorm_detail_id().equals(moduleId)) {
                        String taskDetailsId = UuidUtil.get32UUID();
                        List<String> listMap = ExcelModelByTotalScoreAndCheckItem(issueLists, moduleId);
                        //模块详情数据添加
                        TaskDetail taskDetail = new TaskDetail();
                        Integer detailScores = 0;
                        Integer intValueScore = 0;
                        Integer num = 0;
                        for (String projectId : listMap) {
                            String id = projectId;
                            List<DetailList> forList = (List<DetailList>) dao.findForList("RectifyMapper.queryTaskDetailByRowAndIssue", id);
                            for (DetailList detailList : forList) {
                                String taskDetailRowId = UuidUtil.get32UUID();
                                for (DetailIssueList issueList : detailList.getIssueList()) {
                                    String issueId = UuidUtil.get32UUID();
                                    for (img img : issueList.getImg()) {
                                        String imgId = UuidUtil.get32UUID();
                                        PageData taskDetailRowIssueImg = new PageData();
                                        taskDetailRowIssueImg.put("id", imgId);
                                        taskDetailRowIssueImg.put("name", img.getName());
                                        taskDetailRowIssueImg.put("url", img.getUrl());
                                        taskDetailRowIssueImg.put("type", 1);
                                        taskDetailRowIssueImg.put("STATUS", 1);
                                        taskDetailRowIssueImg.put("created_time", new Date());
                                        taskDetailRowIssueImg.put("update_time", null);
                                        dao.save("RectifyMapper.createdTaskDetailRowIssueByImg", taskDetailRowIssueImg);
                                        PageData taskDetailRowIssueImgBy = new PageData();
                                        taskDetailRowIssueImgBy.put("issue_id", issueId);
                                        taskDetailRowIssueImgBy.put("image_id", imgId);
                                        dao.save("RectifyMapper.createdTaskDetailRowIssueBy", taskDetailRowIssueImgBy);
                                    }
                                    PageData taskDetailRowIssue = new PageData();
                                    taskDetailRowIssue.put("id", issueId);
                                    taskDetailRowIssue.put("row_id", taskDetailRowId);
                                    taskDetailRowIssue.put("conent", issueList.getConent());
                                    taskDetailRowIssue.put("type", issueList.getType());
                                    taskDetailRowIssue.put("score", issueList.getScore());
                                    taskDetailRowIssue.put("STATUS", issueList.getStatus());
                                    taskDetailRowIssue.put("remark", issueList.getRemark());
                                    dao.save("RectifyMapper.createdTaskDetailRowIssue", taskDetailRowIssue);
                                }
                                TaskDetailRow taskDetailRow = new TaskDetailRow();
                                taskDetailRow.setId(taskDetailRowId);
                                //任务详情表id
                                taskDetailRow.setTask_detail_id(taskDetailsId);
                                //检查项目的id
                                taskDetailRow.setNorm_detail_id(detailList.getNorm_detail_id());
                                //总分
                                int intValue = new Double(String.valueOf(detailList.getTotal_score())).intValue();
                                intValueScore += intValue;
                                taskDetailRow.setTotal_score(intValue);
                                //得分
                                String forObject = detailList.getScore();
                                int detailScore = new Double(forObject).intValue();
                                if (detailScore < 0) {
                                    detailScores = detailScores + Math.abs(detailScore);
                                } else {
                                    detailScores += detailScore;
                                }
                                taskDetailRow.setScore(detailScore);
                                //问题数
                                num += detailList.getIssueList().size();
                                taskDetailRow.setIssue(detailList.getIssueList().size());
                                //状态:1未检查,2检查中,3检查完成
                                taskDetailRow.setStatus(1);
                                dao.save("TaskMapper.createdTaskDetailRow", taskDetailRow);
                            }
                        }
                        taskDetail.setId(taskDetailsId);
                        taskDetail.setTask_id(task_id);
                        taskDetail.setCensor_id(censorId);
                        String normId = ModuleByNorm(normList, moduleId);
                        taskDetail.setNorm_id(normId);
                        taskDetail.setNorm_detail_id(moduleId);
                        taskDetail.setCreated_time(new Date());
                        taskDetail.setTotal_score(intValueScore);
                        taskDetail.setScore(detailScores);
                        taskDetail.setTotal_issue(num);
                        taskDetail.setSolved_issue(0);
                        taskDetail.setCheck_item(num);
                        taskDetail.setTo_check(0);
                        taskDetail.setChild_id(censorId);
                        //状态:1未检查,2已检查,3检查完成
                        taskDetail.setStatus(1);
                        dao.save("RectifyMapper.createdTaskDetail", taskDetail);
                        userTotalCensor += num;
                        totalCensor += num;
                        //单个用户所需要检查的总分
                        userTotalScore += intValueScore;
                    }
                }
            }
            Censor censor = new Censor();
            censor.setId(censorId);
            censor.setUser_id(requestParamTaskDetail.getUserId());
            //总检查项
            censor.setGeneral_inspection(userTotalCensor);
            //已检查项
            censor.setChecked(0);
            //总分
            censor.setTotal_score(userTotalScore);
            //得分
            censor.setScore(0);
            //问题数
            censor.setTotal_issue(userTotalCensor);
            //已解决问题
            censor.setSolved_issue(0);
            //状态:1未检查,2检查中,3检查完成,4已上报,5已延期,6延期完成,7未上报,8待审核,9审核通过,10审核驳回
            censor.setStatus(1);
            //类型:1检查任务,2整改任务,3个人任务
            censor.setType(2);
            //创建时间
            censor.setCreated_time(new Date());
            //检查表数据插入
            dao.save("TaskMapper.createdCensor", censor);
            //任务进度表数据插入
            //task_id, censor_id, STATUS, reporting
            PageData pageData = new PageData();
            pageData.put("task_id", task_id);
            pageData.put("censor_id", censorId);
            //状态:0未开始,1进行中,2未上报,3已上报,4整改中,5已完成
            pageData.put("status", 0);
            dao.save("TaskMapper.createdSpeed", pageData);
        }
        task.put("id", task_id);
        task.put("NAME", task.get("NAME") + "整改");
        task.put("unit_id", task.get("unit_id"));
        task.put("site_id", task.get("site_id"));
        task.put("star_time", param.getStarTime());
        task.put("end_time", param.getEndTime());
        task.put("location", task.get("location"));
        task.put("group_id", null);
        task.put("frequency", 0);
        task.put("STATUS", 1);
        task.put("type", 2);
        task.put("remark", param.getContent());
        //总检查项
        task.put("total_censor", totalCensor);
        //已检查项
        task.put("censor", 0);
        //总分
        task.put("total_score", totalScore);
        //得分
        task.put("score", 0);
        //问题总数
        task.put("total_issue", totalCensor);
        //已解决问题数
        task.put("solved_issue", 0);
        task.put("created_time", new Date());
        task.put("user_id", pd.get("uid"));
        dao.save("RectifyMapper.createdTask", task);
        //修改原任务的状态
        dao.update("RectifyMapper.updateTask", taskId.get(0));
        //修改上报表的状态
        PageData pageData = new PageData();
        pageData.put("remark", param.getContent());
        pageData.put("uid", pd.get("uid"));
        pageData.put("report_id", pd.get("id"));
        dao.update("RectifyMapper.updateReport", pageData);
        //加入整改父id
        for (String s : taskId) {
            PageData data = new PageData();
            data.put("task_id", task_id);
            data.put("parent_id", s);
            dao.save("RectifyMapper.insertTaskParam", data);
        }
        //存Excel和任务关联表
        for (String s : excelIds) {
            PageData pd1 = new PageData();
            pd1.put("excelId", s);
            pd1.put("taskId", task_id);
            dao.save("TaskMapper.createdTaskExcel", pd1);
        }
        //用户任务关联表
        for (String s : userIds) {
            PageData pageData1 = new PageData();
            pageData.put("task_id", task_id);
            pageData.put("user_id", s);
            dao.save("TaskMapper.createdTaskByUser", pageData1);
        }
        //存任务和标准的中间表
        List<String> strings = ModuleByNormId(normList, param.getIssueDetail());
        for (String string : strings) {
            PageData normPd = new PageData();
            normPd.put("taskId", task_id);
            normPd.put("normId", string);
            dao.save("TaskMapper.createdTaskNorm", normPd);
        }
        return "分配成功";
    }

    /**
     * 组长分配整改数据列表
     *
     * @param pd id 检查id
     * @return
     */
    public List<PageData> groupAllocationList(PageData pd) throws Exception {
        //查询当前组长的分配整改列表
        List<PageData> censorList = (List<PageData>) dao.findForList("RectifyMapper.groupAllocationList", pd);
        List<String> normIds = new ArrayList<>();
        for (PageData pageData : censorList) {
            String norm_id = (String) pageData.get("norm_id");
            normIds.add(norm_id);
        }
        //去重数据
        List<String> list = new ArrayList<>(new LinkedHashSet<>(normIds));
        List<Norm> normList = (List<Norm>) dao.findForList("TaskMapper.queryUnitAndSite", null);
        List<PageData> list1 = new ArrayList<>();
        for (Norm norm : normList) {
            if (list.contains(norm.getId())) {
                PageData data = new PageData();
                data.put("id", norm.getId());
                data.put("normName", norm.getName());
                data.put("excelId", norm.getExcel_id());
                List<PageData> pageDataList = new ArrayList<>();
                for (NormDetail normDetail : norm.getNormDetails()) {
                    for (PageData pageData : censorList) {
                        if (normDetail.getId().equals(String.valueOf(pageData.get("norm_detail_id")))) {
                            PageData pageData1 = new PageData();
                            pageData1.put("id", normDetail.getId());
                            pageData1.put("normDetailName", normDetail.getItem());
                            pageDataList.add(pageData1);
                        }

                    }
                }
                data.put("normDetail", pageDataList);
                list1.add(data);
            }
        }
        removeDuplicate(list1);
        return list1;
    }

    /**
     * 组长分配整改
     *
     * @param pd
     * @return
     */
    public synchronized String groupAllocation(PageData pd) throws Exception {
        Data param = JSON.parseObject(String.valueOf(pd.get("param")), Data.class);

        //查询当前组长的任务
        List<PageData> taskDetail = (List<PageData>) dao.findForList("RectifyMapper.queryTaskGroupUser", pd);
        for (Module module : param.getData()) {
            Censor censor = new Censor();
            String censorId = UuidUtil.get32UUID();
            Integer userTotalCensor = 0;
            Integer userTotalScore = 0;
            String taskId = "";
            for (PageData pageData : taskDetail) {
                if (module.getModule().contains(pageData.get("norm_detail_id").toString())) {
                    userTotalCensor += new Double(String.valueOf(pageData.get("check_item"))).intValue();
                    userTotalScore += new Double(String.valueOf(pageData.get("total_score"))).intValue();
                    taskId=String.valueOf(pageData.get("task_id"));
                    PageData pageDataDetail = new PageData();
                    pageDataDetail.put("id", censorId);
                    pageDataDetail.put("detailId", pageData.get("id"));
                    dao.update("RectifyMapper.updateTaskCensorChildId", pageDataDetail);
                }
            }
            censor.setId(censorId);
            //检查用户
            censor.setUser_id(module.getUserId());
            //总检查项
            censor.setGeneral_inspection(userTotalCensor);
            //已检查项
            censor.setChecked(0);
            //总分
            censor.setTotal_score(userTotalScore);
            //得分
            censor.setScore(0);
            //问题数
            censor.setTotal_issue(userTotalCensor);
            //已解决问题
            censor.setSolved_issue(0);
            //状态:1未检查,2检查中,3检查完成,4已上报,5已延期,6延期完成,7未上报,8待审核,9审核通过,10审核驳回
            censor.setStatus(1);
            //类型:1检查任务,2整改任务,3个人任务
            censor.setType(2);
            //创建时间
            censor.setCreated_time(new Date());
            //检查表数据插入
            dao.save("TaskMapper.createdCensor", censor);
            //任务进度表数据插入
            PageData pageData1 = new PageData();
            pageData1.put("task_id", taskId);
            pageData1.put("censor_id", censorId);
            //状态:0未开始,1进行中,2未上报,3已上报,4整改中,5已完成
            pageData1.put("status", 0);
            dao.save("TaskMapper.createdSpeed", pageData1);

        }
        return "分配成功";
    }


    /**
     * 查询单个用户下面所有的任务列表
     *
     * @param pd
     * @return
     */
    public List<PageData> queryUserByTask(PageData pd) throws Exception {
        //判断是否是检察员
        Integer functionId = (Integer) dao.findForObject("RectifyMapper.queryUserByRoue", pd);
        if (functionId == 1) {
            return (List<PageData>) dao.findForList("RectifyMapper.queryUserByTask1", pd);
        }
        if (functionId == 2) {
            return (List<PageData>) dao.findForList("RectifyMapper.queryUserByTask2", pd);
        }
        if (functionId == 3) {
            return (List<PageData>) dao.findForList("RectifyMapper.queryUserByTask3", pd);
        }
        return null;
    }

    /**
     * 查询单个用户下的组员
     *
     * @param pd
     * @return
     */
    public List<PageData> queryUserByTaskToUser(PageData pd) throws Exception {
        //判断是否是检察员
        Integer functionId = (Integer) dao.findForObject("RectifyMapper.queryUserByRoue", pd);
        if (functionId == 3) {
            PageData pageData = new PageData();
            pageData.put("USER_ID", pd.get("uid"));
            pageData.put("USER_NAME", dao.findForObject("RectifyMapper.queryUserByName", pd));
            List<PageData> list = new ArrayList<>();
            list.add(pageData);
            return list;
        }
        List<PageData> pageData = queryUserByTask(pd);
        List<PageData> pageDataList = new ArrayList<>();
        for (PageData data : pageData) {
            pd.put("id", data.get("id"));
            List<PageData> forList = (List<PageData>) dao.findForList("RectifyMapper.queryUserByTaskToUser", pd);
            if (forList.size() != 0) {
                for (PageData pageData1 : forList) {
                    pageDataList.add(pageData1);
                }
            }

        }
        return pageDataList;
    }

    /**
     * 查询单个用户的准则列表
     *
     * @param pd
     * @return
     */
    public List<PageData> queryUserNormList(PageData pd) throws Exception {
        List<PageData> pageDataList = (List<PageData>) dao.findForList("RectifyMapper.queryUserNormList", pd);
        return pageDataList;
    }

    /**
     * 导出任务问题excel
     *
     * @return
     */
    public ModelAndView exportTaskIssueList(PageData pd) {
        //用户
        try {
            //检索条件===
            Map<String, Object> dataMap = new HashMap<String, Object>();
            PageData pagedatatask = new PageData();
            pagedatatask.put("uid", pd.getString("USER_ID"));
            pagedatatask.put("page", 1);
            pagedatatask.put("size", 1000000);
            pagedatatask.put("type", 2);
            pagedatatask.put("task_id", pd.getString("id"));
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
                    pageDataDate.put("name", pageData.get("taskName"));
                    PageData pageDatacen = new PageData();
                    pageDatacen.put("UNIT_ID", pageData.get("unit_id"));
                    PageData byId = unitService.findById(pageDatacen);
                    pageDataDate.put("unitName", byId.getString("NAME"));
                    pageDatacen.put("UNIT_ID", pageData.get("site_id"));
                    PageData byId1 = unitService.findById(pageDatacen);
                    pageDataDate.put("siteName", byId1.getString("NAME"));
                    pageDataDate.put("normName", pageData.get("normName"));
                    pageDataDate.put("content", censorRow.getRow_content());
                    pageDataDate.put("serialParentName", unitService.queryNormDetailName(censorRow.getId()));
                    pageDataDate.put("serialName", censorRow.getRow_item());
                    pageDataDate.put("issueContent", content);
                    pageDataDate.put("serial", unitService.queryNormDetailSerial(censorRow.getId()));
                    pageDataDate.put("score", censorRow.getScore());
                    pageDataDate.put("issueRemark", remark);
                    if (StringUtils.isNotBlank(type)) {
                        if (type.equals("2")) {
                            pageDataDate.put("typeName", "严重");
                        } else {
                            pageDataDate.put("typeName", "一般");
                        }
                    }

                    String s = unitService.querytaskcheckId(censorRe.getCensor_id());
                    PageData checkuser = new PageData();
                    checkuser.put("USER_ID", s);
                    PageData byUiId = userService.findByUiId(checkuser);
                    if (byUiId != null) {
                        pageDataDate.put("userName", byUiId.getString("USERNAME"));
                    }
                    pageDataDate.put("startTime", pageData.get("star_time"));
                    pageDataDate.put("endTime", pageData.get("end_time"));
                    pageDataDate.put("location", pageData.get("location"));
                    pageDataDate.put("recordTime", unitService.queryIssueTime(censorRow.getId()));
                    data.add(pageDataDate);


                }
            }
            pagedatatask.put("taskCensorRes", data);
            List<String> titles2 = new ArrayList<String>();
            titles2.add("编号");             //1
            titles2.add("任务名称");          //2
            titles2.add("单位名称");             //3
            titles2.add("子单位");             //4
            titles2.add("评价准则");         //5
            titles2.add("评价内容");          //6
            titles2.add("所属大类");              //7
            titles2.add("所属小类");            //8
            titles2.add("问题描述");        //9
            titles2.add("序号");            //10
            titles2.add("评分");            //11
            titles2.add("问题备注");            //12
            titles2.add("问题性质");            //13
            titles2.add("检查人员");            //14
            titles2.add("开始时间");            //15
            titles2.add("结束时间");            //16
            titles2.add("检查地点");            //17
            titles2.add("记录时间");            //18
            dataMap.put("titles2", titles2);

            List<PageData> varList2 = new ArrayList<PageData>();
            for (int i = 0; i < data.size(); i++) {
                PageData vpd = new PageData();
                vpd.put("var1", String.valueOf(i));        //1
                vpd.put("var2", data.get(i).getString("name"));    //2
                vpd.put("var3", data.get(i).getString("unitName"));        //3
                vpd.put("var4", data.get(i).getString("siteName"));        //4

                List<String> normlist = (ArrayList<String>) data.get(i).get("normName");
                String name = "";
                for (int j = 0; j <= normlist.size() - 1; j++) {
                    if (j < normlist.size() - 1) {
                        name += normlist.get(j) + ",";
                    } else {
                        name += normlist.get(j);
                    }
                }
                vpd.put("var5", name);        //5
                vpd.put("var6", data.get(i).getString("content"));        //6
                vpd.put("var7", data.get(i).getString("serialParentName"));        //7
                vpd.put("var8", data.get(i).getString("serialName"));        //8
                vpd.put("var9", data.get(i).getString("issueContent"));        //9
                vpd.put("var10", data.get(i).getString("serial"));        //10
                vpd.put("var11", data.get(i).get("score").toString());        //11
                vpd.put("var12", data.get(i).getString("issueRemark"));        //12
                vpd.put("var13", data.get(i).getString("typeName"));        //13
                vpd.put("var14", data.get(i).getString("userName"));        //14
                vpd.put("var15", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(data.get(i).get("startTime")));        //15
                vpd.put("var16", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(data.get(i).get("endTime")));        //16
                vpd.put("var17", data.get(i).getString("location"));        //17
                vpd.put("var18", data.get(i).getString("recordTime"));        //18
                varList2.add(vpd);
            }
            dataMap.put("varList2", varList2);
            ObjectExcelViewAllCheck erv = new ObjectExcelViewAllCheck();
            ModelAndView mv = new ModelAndView();
            mv = new ModelAndView(erv, dataMap);
            return mv;
        } catch (Exception e) {
            return null;
        }
    }

    private <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    /**
     * 数组去重
     *
     * @param list
     * @return
     */
    public List removeDuplicate(List list) {
        HashSet h = new HashSet(list);
        list.clear();
        list.addAll(h);
        return list;
    }

    /**
     * 获取两个时间相差分钟数
     *
     * @param data
     * @param frequency
     * @return
     * @throws ParseException
     */
    public int getTime(Date data, Integer frequency) throws ParseException {
        Calendar ca = Calendar.getInstance();
        ca.setTime(data);
        ca.add(Calendar.DATE, frequency);
        data = ca.getTime();
        int time = new Long(data.getTime() - System.nanoTime()).intValue();
        return time;
    }

    /**
     * 传入模块id返回该模块下的总分以及检查项;
     *
     * @param normList
     * @param modelId
     * @return
     */
    public List<String> ExcelModelByTotalScoreAndCheckItem(List<DetailRow> normList, String modelId) {
        List<String> list = new ArrayList<>();
        for (DetailRow norm : normList) {
            if (String.valueOf(norm.getNorm_detail_id()).equals(modelId)) {
                list.add(norm.getTask_detail_id());
            }
        }
        return new ArrayList<>(new HashSet<>(list));
    }

    /**
     * 传入模块id以及标准详情返回对应的检查项
     *
     * @param detailList
     * @return
     */
    public List<String> ExcelModuleProject(List<NormDetail> detailList, String moduleId) {
        List<String> list = new ArrayList<String>();
        String moduleSerial = null;

        for (NormDetail normDetail : detailList) {
            if (normDetail.getId().equals(moduleId)) {
                moduleSerial = normDetail.getSerial();
            }
        }
        int serial = 0;
        if (moduleSerial != null && moduleSerial != "") {
            serial = new Double(moduleSerial).intValue();
        }
        for (NormDetail normDetail : detailList) {
            if (normDetail.getSerial() != null) {
                String substring = normDetail.getSerial().substring(0, 1);
                if (!checkcountname(substring)) {
                    if (substring != null && substring != "") {
                        int i = new Double(substring).intValue();
                        if (i == serial && normDetail.getContent() != null) {
                            list.add(normDetail.getId());
                        }
                    }
                }
            }
        }
        return list;
    }


    public boolean checkcountname(String countname) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(countname);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 传入检查项目id返回该项目的分数
     *
     * @param normList
     * @param projectId
     * @return
     */
    public Integer ExcelModuleProjectTotalScore(List<Norm> normList, String projectId) {
        for (Norm norm : normList) {
            for (NormDetail normDetail : norm.getNormDetails()) {
                if (normDetail.getId().equals(projectId)) {
                    if (normDetail.getTotal_score() != null) {
                        double v = Double.parseDouble(normDetail.getTotal_score());
                        int score = new Double(v).intValue();
                        return score;
                    } else {
                        return 0;
                    }
                }
            }
        }
        return 0;
    }

    /**
     * 传入任务的详情返回当前任务所属的标准
     *
     * @param normList
     * @param taskDetailUserVoList
     * @return
     */
    public List<String> ModuleByNormId(List<Norm> normList, List<issueList> taskDetailUserVoList) {
        List<String> list = new ArrayList<String>();
        for (Norm norm : normList) {
            for (NormDetail normDetail : norm.getNormDetails()) {
                for (issueList taskDetailUserVo : taskDetailUserVoList) {
                    for (String s : taskDetailUserVo.getModule()) {
                        if (normDetail.getId().equals(s)) {
                            list.add(norm.getId());
                        }
                    }
                }

            }
        }
        return new ArrayList<String>(new LinkedHashSet<String>(list));
    }

    /**
     * 查询当前项目所属那个模块
     *
     * @param normList
     * @param moduleId
     * @return
     */
    public String ModuleByNorm(List<Norm> normList, String moduleId) {
        for (Norm norm : normList) {
            for (NormDetail normDetail : norm.getNormDetails()) {
                if (normDetail.getId().equals(moduleId)) {
                    return norm.getId();
                }
            }
        }
        return null;
    }
}
