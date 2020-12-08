package com.fh.service.app;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fh.config.DelayMessageSender;
import com.fh.dao.DaoSupport;
import com.fh.entity.Page;
import com.fh.entity.app.*;
import com.fh.entity.requestVo.TaskDetailUserVo;
import com.fh.entity.requestVo.TaskVo;
import com.fh.util.Const;
import com.fh.util.PageData;
import com.fh.util.UuidUtil;
import io.swagger.models.auth.In;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("NormService")
@Transactional()
public class TaskService {

    @Resource(name = "daoSupport")
    private DaoSupport dao;

//    @Resource(name = "delayMessageSender")
//    private DelayMessageSender sender;

    @Resource(name = "redisTemplate")
    private RedisTemplate redisTemplate;


    private SimpleDateFormat sdf = new SimpleDateFormat(" yyyy-MM-dd HH:mm:ss ");

    /**
     * 查询任务列表
     *
     * @param pd
     * @return
     */
    public List<PageData> queryTaskList(PageData pd) throws Exception {
        Page page = new Page();
        page.setCurrentPage(Integer.parseInt(pd.get("page").toString()));
        page.setShowCount(Integer.parseInt(pd.get("size").toString()));
        page.setPd(pd);
        Integer function = (Integer) dao.findForObject("TaskMapper.queryUserByRoue", pd.get("uid"));
        int type = Integer.parseInt(pd.get("type").toString());
        List<PageData> forList = new ArrayList<PageData>();
        if (function == 2) {
            if (type == 1) {
                //单个用户下的任务
                List<PageData> list = (List<PageData>) dao.findForList("TaskMapper.datalistPage113", page);
                if (list.size() != 0) {
                    for (PageData pageData : list) {
                        pageData.put("uid", pd.get("uid"));
                        int type1 = Integer.parseInt(String.valueOf(pageData.get("type")));
                        if (type1 == 2) {
                            forList.add((PageData) dao.findForObject("TaskMapper.groupTaskOn", pageData));
                        } else {
                            PageData daoForObject = (PageData) dao.findForObject("TaskMapper.groupTaskOne1", pageData);
                            if (daoForObject == null) {
                                forList.add((PageData) dao.findForObject("TaskMapper.groupTaskOne12", pageData));
                            } else {
                                forList.add(daoForObject);
                            }
                        }
                    }
                }
            } else {
                //单个用户下的任务
                List<PageData> list = (List<PageData>) dao.findForList("TaskMapper.datalistPage1133", page);
                if (list.size() != 0) {
                    for (PageData pageData : list) {
                        pageData.put("uid", pd.get("uid"));
                        int type1 = Integer.parseInt(String.valueOf(pageData.get("type")));
                        if (type1 == 2) {
                            forList.add((PageData) dao.findForObject("TaskMapper.groupTaskOn", pageData));
                        } else {
                            PageData daoForObject = (PageData) dao.findForObject("TaskMapper.groupTaskOne1", pageData);
                            if (daoForObject == null) {
                                forList.add((PageData) dao.findForObject("TaskMapper.groupTaskOne12", pageData));
                            } else {
                                forList.add(daoForObject);
                            }
                        }
                    }
                }
            }
        } else if (function == 3) {
            if (type == 1) {
                //单个用户下的任务
                List<PageData> list = (List<PageData>) dao.findForList("TaskMapper.datalistPageType", page);
                if (list.size() != 0) {
                    for (PageData pageData : list) {
                        int type1 = Integer.parseInt(String.valueOf(pageData.get("type")));
                        pageData.put("uid", pd.get("uid"));
                        if (type1 == 1) {
                            forList.add((PageData) dao.findForObject("TaskMapper.userTaskOn", pageData));
                        }
                        if (type1 == 2) {
                            forList.add((PageData) dao.findForObject("TaskMapper.userTaskOn", pageData));
                        }
                        if (type1 == 3) {
                            forList.add((PageData) dao.findForObject("TaskMapper.userTaskOn22", pageData));
                        }
                    }
                }
            } else {
                //单个用户下的任务
                List<PageData> list = (List<PageData>) dao.findForList("TaskMapper.datalistPageTypeOn", page);
                if (list.size() != 0) {
                    for (PageData pageData : list) {
                        int type1 = Integer.parseInt(String.valueOf(pageData.get("type")));
                        pageData.put("uid", pd.get("uid"));
                        if (type1 == 1) {
                            forList.add((PageData) dao.findForObject("TaskMapper.userTaskOn", pageData));
                        }
                        if (type1 == 2) {
                            forList.add((PageData) dao.findForObject("TaskMapper.userTaskOn", pageData));
                        }
                        if (type1 == 3) {
                            forList.add((PageData) dao.findForObject("TaskMapper.userTaskOn22", pageData));
                        }
                    }
                }
            }
        }
        if (forList.size() != 0) {
            for (PageData pageData : forList) {
                List<PageData> forObject = (List<PageData>) dao.findForList("TaskMapper.queryNormName", pageData);
                if (forObject.size() != 0) {
                    pageData.put("normName", forObject);
                    // 创建一个数值格式化对象
                    NumberFormat numberFormat = NumberFormat.getInstance();
                    numberFormat.setMaximumFractionDigits(2);
                    int general_inspection = new Double(String.valueOf(pageData.get("general_inspection"))).intValue();
                    int checked = new Double(String.valueOf(pageData.get("checked"))).intValue();
                    String result = numberFormat.format((float) checked / (float) general_inspection * 100);
                    pageData.put("result", result);
                }
            }
            forList.removeAll(Collections.singleton(null));
            if (forList.size() != 0) {
                forList.get(0).put("total", page.getTotalPage());
                forList.get(0).put("currentPage", page.getCurrentPage());
            }
        }
        return forList;
    }

    /**
     * 检查任务详情
     *
     * @param pd
     * @return
     */
    public List<TaskCensorRes> checkingTaskList(PageData pd) throws Exception {
        List<TaskCensorRes> pageData = (List<TaskCensorRes>) dao.findForList("TaskMapper.checkingTaskList", pd);
        for (TaskCensorRes pageDatum : pageData) {
            for (CensorRow censorRow : pageDatum.getCensorRowList()) {
                if (censorRow.getCensorRowIssueList().size() != 0) {
                    for (CensorRowIssue censorRowIssue : censorRow.getCensorRowIssueList()) {
                        String id = censorRowIssue.getId();
                        List<CensorRowIssueImage> id1 = (List<CensorRowIssueImage>) dao.findForList("TaskMapper.censorRowIssueImageList", id);
                        censorRowIssue.setCensorRowIssueImageList(id1);
                    }
                }
            }
        }
        return pageData;
    }

    /**
     * 整改任务组员点击上报
     * @param pd
     * @return
     */
    public PageData queryCheckReportUser(PageData pd) throws Exception {
       return (PageData) dao.findForObject("TaskMapper.queryCheckReportUser", pd);
    }

    /**
     * 查询单个任务下面评分项
     *
     * @param pd
     * @return
     */
    public List<CensorRowIssue> queryTaskIssueList(PageData pd) throws Exception {
        String id = (String) pd.get("id");
        List<CensorRowIssue> pageData = (List<CensorRowIssue>) dao.findForList("TaskMapper.censorRowIssueList", id);
        return pageData;
    }

    /**
     * 创建任务
     *
     * @param data
     * @return
     * @throws Exception
     */
    public synchronized String createdTask(PageData data) throws Exception {
        String param = (String) data.get("param");
        TaskVo requestParam = JSONObject.parseObject(String.valueOf(param), TaskVo.class);
        //用户集
        List<String> list = new ArrayList<String>();
        for (TaskDetailUserVo detail : requestParam.getTaskDetails()) {
            String userId = detail.getUserId();
            list.add(userId);
        }
        list.add(requestParam.getUserId());
        list.add(requestParam.getGroupId());
        Integer totalScore = 0;
        Integer totalCensor = 0;
        //任务表id
        String taskId = UuidUtil.get32UUID();
        //查询到当前Excel数据集
        List<Norm> normList = (List<Norm>) dao.findForList("TaskMapper.queryUnitAndSite", null);
        for (TaskDetailUserVo requestParamTaskDetail : requestParam.getTaskDetails()) {
            //检查表id
            String censorId = UuidUtil.get32UUID();
            //单个用户检查总分
            Integer userTotalScore = 0;
            //单个用户检查项目数
            Integer userTotalCensor = 0;
            for (String moduleId : requestParamTaskDetail.getModule()) {
                String taskDetailsId = UuidUtil.get32UUID();
                Map<Integer, List<String>> listMap = ExcelModelByTotalScoreAndCheckItem(normList, moduleId);
                for (Integer integer : listMap.keySet()) {
                    //单个模块下的分数
                    totalScore += integer;
                    //单个模块下需要检查的项目
                    List<String> strings = listMap.get(integer);
                    //检查的数量
                    totalCensor += strings.size();
                    //单个用户所需要检查的总分
                    userTotalScore += integer;
                    //单个用户的检查数量
                    userTotalCensor += strings.size();
                    //模块详情数据添加
                    TaskDetail taskDetail = new TaskDetail();
                    taskDetail.setId(taskDetailsId);
                    taskDetail.setTask_id(taskId);
                    taskDetail.setCensor_id(censorId);
                    String normId = ModuleByNorm(normList, moduleId);
                    taskDetail.setNorm_id(normId);
                    taskDetail.setNorm_detail_id(moduleId);
                    taskDetail.setCreated_time(new Date());
                    taskDetail.setTotal_score(integer);
                    taskDetail.setScore(0);
                    taskDetail.setTotal_issue(0);
                    taskDetail.setSolved_issue(0);
                    taskDetail.setCheck_item(strings.size());
                    taskDetail.setTo_check(0);
                    //状态:1未检查,2已检查,3检查完成
                    taskDetail.setStatus(1);
                    dao.save("TaskMapper.createdTaskDetail", taskDetail);
                    //单个模块下的检查项目
                    for (String projectId : strings) {
                        TaskDetailRow taskDetailRow = new TaskDetailRow();
                        taskDetailRow.setId(UuidUtil.get32UUID());
                        //任务详情表id
                        taskDetailRow.setTask_detail_id(taskDetailsId);
                        //检查项目的id
                        taskDetailRow.setNorm_detail_id(projectId);
                        Integer score = ExcelModuleProjectTotalScore(normList, projectId);
                        //总分
                        taskDetailRow.setTotal_score(score);
                        //得分
                        taskDetailRow.setScore(0);
                        //问题数
                        taskDetailRow.setIssue(0);
                        //状态:1未检查,2检查中,3检查完成
                        taskDetailRow.setStatus(1);
                        dao.save("TaskMapper.createdTaskDetailRow", taskDetailRow);
                    }
                }
            }
            Censor censor = new Censor();
            censor.setId(censorId);
            //检查用户
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
            censor.setTotal_issue(0);
            //已解决问题
            censor.setSolved_issue(0);
            //状态:1未检查,2检查中,3检查完成,4已上报,5已延期,6延期完成,7未上报,8待审核,9审核通过,10审核驳回
            censor.setStatus(1);
            //类型:1检查任务,2整改任务,3个人任务
            switch (requestParam.getType()) {
                case 1:
                    censor.setType(1);
                    break;
                case 2:
                    censor.setType(3);
                    break;
                default:

            }
            //创建时间
            censor.setCreated_time(new Date());
            //检查表数据插入
            dao.save("TaskMapper.createdCensor", censor);
            //任务进度表数据插入
            PageData pageData = new PageData();
            pageData.put("task_id", taskId);
            pageData.put("censor_id", censorId);
            //状态:0未开始,1进行中,2未上报,3已上报,4整改中,5已完成
            pageData.put("status", 0);
            dao.save("TaskMapper.createdSpeed", pageData);
        }
        //任务数据
        Task task = new Task();
        //任务id
        task.setId(taskId);
        //名称
        task.setName(requestParam.getName());
        //单位id
        task.setUnit_id(requestParam.getUnitId());
        //站点id
        task.setSite_id(requestParam.getSiteId());
        //开始时间
        task.setStar_time(requestParam.getStarTime());
        //结束时间
        task.setEnd_time(requestParam.getEndTime());
        //检查地点
        task.setLocation(requestParam.getLocation());
        //组长
        task.setGroup_id(requestParam.getGroupId());
        //是否循环发放任务
        Integer frequency = requestParam.getFrequency();
        task.setFrequency(frequency);
        //状态:1未进行,2已删除,3进行中,4已完成,5已延期,6延期完成,7未上报,8待审核,9待分配,10已汇总
        task.setStatus(1);
        //类型:1检查任务,2整改任务,3个人任务
        switch (requestParam.getType()) {
            case 1:
                task.setType(1);
                break;
            case 2:
                task.setType(3);
                break;
            default:

        }
        //总检查项
        task.setTotal_censor(totalCensor);
        //已检查项
        task.setCensor(0);
        //总分
        task.setTotal_score(totalScore);
        //得分
        task.setScore(0);
        //问题总数
        task.setTotal_issue(0);
        //已解决问题数
        task.setSolved_issue(0);
        //操作人
        task.setUser_id(requestParam.getUserId());
        //存任务主表进数据库
        dao.save("TaskMapper.createdTask", task);
        //存Excel和任务关联表
        for (String s : requestParam.getExcelId()) {
            PageData pd1 = new PageData();
            pd1.put("excelId", s);
            pd1.put("taskId", taskId);
            dao.save("TaskMapper.createdTaskExcel", pd1);
        }
        //存任务和标准的中间表
        List<String> strings = ModuleByNormId(normList, requestParam.getTaskDetails());
        for (String string : strings) {
            PageData normPd = new PageData();
            normPd.put("taskId", taskId);
            normPd.put("normId", string);
            dao.save("TaskMapper.createdTaskNorm", normPd);
        }
        //发消息到rabbitmq
        if (frequency != 0) {
            int time = getTime(new Date(), frequency);
            //  sender.sendDelayMsg(taskId + "," + frequency, time);
        }
        //用户任务关联表
        List<String> userList = new ArrayList<String>(new LinkedHashSet<String>(list));
        for (String s : userList) {
            PageData pageData = new PageData();
            pageData.put("task_id", taskId);
            pageData.put("user_id", s);
            dao.save("TaskMapper.createdTaskByUser", pageData);
        }
        return "创建成功";
    }

    /**
     * 查询单个任务(编辑操作) 准则下面的详情
     *
     * @return
     */
    public PageData queryTaskToOne(PageData pd) throws Exception {
        String task_id = (String) pd.get("task_id");
        List<PageData> forList = (List<PageData>) dao.findForList("TaskMapper.queryTaskToOne", task_id);
        PageData clone = null;
        List<String> name = new ArrayList<String>();
        for (PageData pageData : forList) {
            clone = (PageData) pageData.clone();
            name.add((String) pageData.get("normName"));
        }
        clone.put("normName", name);
        return clone;
    }

    /**
     * 修改任务
     *
     * @param pd
     * @return
     */
    public String editTask(PageData pd) throws Exception {
        String param = (String) pd.get("param");
        TaskVo requestParam = JSONObject.parseObject(String.valueOf(param), TaskVo.class);
        dao.update("TaskMapper.editTask", requestParam);
        Integer frequency = requestParam.getFrequency();
        //发消息到rabbitmq
        if (frequency != 0) {
            int time = getTime(new Date(), frequency);
//            sender.sendDelayMsg(requestParam.getId() + "," + frequency, time);
        }
        return "操作成功";
    }

    /**
     * 删除任务
     *
     * @param pd
     * @return
     */
    public String deleteTask(PageData pd) throws Exception {
        Integer status = (Integer) dao.findForObject("TaskMapper.queryTaskId", pd.get("id"));
        //组长删除
        if (status == 3 && status == 5 && status == 7 && status == 8 && status == 9) {
            return "当前任务不能删除";
        } else {
            redisTemplate.boundValueOps(pd.get("id")).set(status);
            dao.update("TaskMapper.deleteTask", pd.get("id"));
        }
        return "操作成功";
    }


    /**
     * 正常检查任务
     * id:检查行id
     * detail_id:任务详情id
     * score:得分默认单行的总分数
     *
     * @param pd
     * @return
     */
    public String checkingTask(PageData pd) throws Exception {
        //判断是否是整改任务
        String detail_id = pd.getString("detail_id");
        Integer taskType = (Integer) dao.findForObject("TaskMapper.queryTaskIsReport", detail_id);
        if (taskType == 2) {
            return "不能直接操作";
        }
        Calculation(pd, true, false);
        return "操作成功";
    }

    /**
     * 取消id正常检查任务
     * :检查行id
     * detail_id:任务详情id
     *
     * @param pd
     * @return
     */
    public String cancelCheckingTask(PageData pd) throws Exception {
        IssueCheckingVo issueCheckingVo = JSONObject.parseObject(String.valueOf(pd.get("param")), IssueCheckingVo.class);
        //判断是否是整改任务
        String detail_id = pd.getString("detail_id");
        Integer taskType = (Integer) dao.findForObject("TaskMapper.queryTaskIsReport", detail_id);
        if (taskType == 2) {
            reportIssueList param = new reportIssueList();
            param.setFlag(false);
            param.setDetailId(pd.getString("detail_id"));
            param.setRowId(issueCheckingVo.getRow());
            this.taskReportEditIssue(param, pd);
            return "操作成功";
        }
        if (issueCheckingVo.getMark().size() != 0) {
            //查询上一次的数据然后进行删除
            List<PageData> pageData = (List<PageData>) dao.findForList("TaskMapper.queryTaskIssueScore", issueCheckingVo.getRow());
            //删除问题行
            dao.delete("TaskMapper.deleteIssueRow", issueCheckingVo.getRow());
            Integer num = 0;
            Integer score = 0;
            for (PageData pageDatum : pageData) {
                String id = (String) pageDatum.get("id");
                int type = Integer.parseInt(String.valueOf(pageDatum.get("type")));
                if (type == 2) {
                    score += new Double(String.valueOf(pageDatum.get("score"))).intValue();
                } else {
                    num += 1;
                    score = score - new Double(String.valueOf(pageDatum.get("score"))).intValue();
                }
                //删除问题记录
                dao.delete("TaskMapper.deleteTaskIssue", issueCheckingVo.getRow());
                //删除中间表
                dao.delete("TaskMapper.deleteTaskIssueByImage", id);
            }
            //其他表的数据
            PageData callback = new PageData();
            callback.put("id", issueCheckingVo.getRow());
            callback.put("detail_id", issueCheckingVo.getDetail_id());
            callback.put("new_score", issueCheckingVo.getNew_score());
            callback.put("score", score);
            callback.put("issueTotal", num);
            Calculation(callback, false);
        }
        //调用正常检查的方法;进行加分计算
        Calculation(pd, false, false);
        return "操作成功";
    }

    /**
     * 问题检查任务
     *
     * @param pd
     * @return
     */
    public synchronized String IssueCheckingTask(PageData pd) throws Exception {
        IssueCheckingVo issueCheckingVo = JSONObject.parseObject(String.valueOf(pd.get("data")), IssueCheckingVo.class);
        if (issueCheckingVo.isStatus()) {
            Calculation(pd, false, false);
        }
        List<MarkVo> mark = issueCheckingVo.getMark();
        Integer score = 0;
        Integer num = 0;
        for (MarkVo item : mark) {
            PageData pageData = new PageData();
            String uuid = UuidUtil.get32UUID();
            pageData.put("id", uuid);
            pageData.put("content", item.getContent());
            Integer type = item.getType();
            if (type == 2) {
                score = score + item.getScore();
            } else {
                score = score - item.getScore();
                num += 1;
            }
            pageData.put("type", type);
            pageData.put("score", item.getScore());
            pageData.put("status", item.getStatus());
            pageData.put("row_id", issueCheckingVo.getRow());
            pageData.put("remark", item.getRemark());
            dao.save("TaskMapper.IssueCheckingTask", pageData);
            for (String image : item.getImages()) {
                PageData pdImage = new PageData();
                pdImage.put("issue_id", uuid);
                pdImage.put("image_id", image);
                dao.save("TaskMapper.IssueCheckingTaskImg", pdImage);
            }
        }
        PageData callback = new PageData();
        callback.put("id", issueCheckingVo.getRow());
        callback.put("detail_id", issueCheckingVo.getDetail_id());
        callback.put("score", score);
        callback.put("new_score", issueCheckingVo.getNew_score());
        callback.put("issueTotal", num);
        callback.put("issueNum", mark.size());
        //调用正常检查的方法;进行加分计算
        Calculation(callback, true);
        return "操作成功";
    }

    /**
     * 编辑问题检查任务
     * 删了重新插入
     *
     * @param pd
     * @return
     */
    public String cancelIssueCheckingTask(PageData pd) throws Exception {
        IssueCheckingVo issueCheckingVo = JSONObject.parseObject(String.valueOf(pd.get("data")), IssueCheckingVo.class);
        List<MarkVo> mark = issueCheckingVo.getMark();
        //查询上一次的数据然后进行删除
        List<PageData> pageData = (List<PageData>) dao.findForList("TaskMapper.queryTaskIssueScore", issueCheckingVo.getRow());
        //删除问题行
        dao.delete("TaskMapper.deleteIssueRow", issueCheckingVo.getRow());
        Integer num = 0;
        Integer score = 0;
        for (PageData pageDatum : pageData) {
            String id = (String) pageDatum.get("id");
            int type = Integer.parseInt(String.valueOf(pageDatum.get("type")));
            if (type == 2) {
                score += new Double(String.valueOf(pageDatum.get("score"))).intValue();
            } else {
                num += 1;
                score -= new Double(String.valueOf(pageDatum.get("score"))).intValue();
            }
            //删除问题记录
            dao.delete("TaskMapper.deleteTaskIssue", issueCheckingVo.getRow());
            //删除中间表
            dao.delete("TaskMapper.deleteTaskIssueByImage", id);
        }
        //其他表的数据
        PageData callback = new PageData();
        callback.put("id", issueCheckingVo.getRow());
        callback.put("detail_id", issueCheckingVo.getDetail_id());
        callback.put("new_score", issueCheckingVo.getNew_score());
        callback.put("score", score);
        callback.put("issueTotal", num);
        Calculation(callback, false);
        IssueCheckingTask(pd);
        return "操作成功";
    }

    /**
     * 问题整改检查任务详情
     *
     * @param pd
     * @return
     */
    public String createdTaskIssue(PageData pd) throws Exception {
        //拿到前端传过来的参数
        reportIssueList param = JSON.parseObject(String.valueOf(pd.get("param")), reportIssueList.class);
        if (param.isFlag()) {
            this.taskReportIssue(param, pd);
        } else {
            this.taskReportEditIssue(param, pd);
            this.taskReportIssue(param, pd);
        }
        return "操作成功";
    }

    /**
     * 查询当前行的检查状态
     * @param pd
     * @return
     */
    public PageData queryTaskDetailRowCheck(PageData pd) throws Exception {
        PageData pageData = (PageData) dao.findForObject("TaskMapper.queryTaskDetailRowCheck",pd.get("id"));
        PageData data = new PageData();
        data.put("status",pageData.getString("STATUS"));
        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(2);
        int general_inspection = new Double(String.valueOf(pageData.get("check_item"))).intValue();
        int checked = new Double(String.valueOf(pageData.get("to_check"))).intValue();
        String result = numberFormat.format((float) checked / (float) general_inspection * 100);
        data.put("sleep", result);
        data.put("issueNum", pageData.get("total_issue"));
        int totalScore = new Double(String.valueOf(pageData.get("total_score"))).intValue();
        int score = new Double(String.valueOf(pageData.get("score"))).intValue();
        int totalScoreRow =0;
        if(score<0){
            totalScoreRow += totalScore+Math.abs(score);
        }else{
            totalScoreRow += totalScore-score;
        }
        data.put("points",totalScoreRow);
        data.put("score",score);
        return data;
    }

    /**
     * 整改检查任务
     *
     * @param param
     * @param pd
     * @throws Exception
     */
    public void taskReportIssue(reportIssueList param, PageData pd) throws Exception {
        pd.put("detail_id", param.getDetailId());
        //插入数据
        List<reportIssueListDetail> reportList = param.getReportList();
        for (reportIssueListDetail issueListDetail : reportList) {
            dao.update("TaskMapper.createdTaskIssue", issueListDetail);
            //插入图片中间表的值
            List<String> img = issueListDetail.getImg();
            for (String s : img) {
                PageData data = new PageData();
                data.put("issue_id", issueListDetail.getId());
                data.put("image_id", s);
                dao.save("TaskMapper.createdImageIssue", data);
            }
        }
        //查询之前需要加分的数据
        PageData forList = (PageData) dao.findForObject("TaskMapper.queryTaskStatusList", pd);
        //模块的总分
        int detailScore = new Double(String.valueOf(forList.get("task_detail_score"))).intValue();
        //模块当前检查数
        int detailCheck = new Double(String.valueOf(forList.get("task_detail_check"))).intValue();
        //模块总检查项
        int detailTotalCheck = new Double(String.valueOf(forList.get("task_detail_item"))).intValue();
        //模块问题总数
        int detailTotalIssue = new Double(String.valueOf(forList.get("task_detail_issue"))).intValue();
        //模块已解决总数
        int detailCheckTotalIssue = new Double(String.valueOf(forList.get("task_detail_solved_issue"))).intValue();
        //模块的id
        String detailId = String.valueOf(forList.get("task_detail_id"));
        //模块的状态
        int detailStatus = new Double(String.valueOf(forList.get("task_detail_status"))).intValue();
        //任务问题总数
        //任务的结束时间
        Date endTime = (Date) forList.get("end_time");

        //检察员的分数
        int censorScore = new Double(String.valueOf(forList.get("task_censor_score"))).intValue();
        //检察员当前检查数
        int censorCheck = new Double(String.valueOf(forList.get("task_censor_checked"))).intValue();
        //检查员总检查数
        int censorTotalCheck = new Double(String.valueOf(forList.get("general_inspection"))).intValue();
        //检查员问题总数
        int censorTotalIssue = new Double(String.valueOf(forList.get("task_censor_issue"))).intValue();
        //检查员已解决问题总数
        int censorCheckTotalIssue = new Double(String.valueOf(forList.get("task_censor_solved_issue"))).intValue();
        //检查员id
        String censorId = String.valueOf(forList.get("task_censor_id"));
        //检查员状态
        int censorStatus = new Double(String.valueOf(forList.get("task_censor_status"))).intValue();

        //任务的分数
        int taskScore = new Double(String.valueOf(forList.get("score"))).intValue();
        //任务当前的检查项
        int taskCheck = new Double(String.valueOf(forList.get("censor"))).intValue();
        //任务当前的总检查项
        int taskTotalCheck = new Double(String.valueOf(forList.get("total_censor"))).intValue();
        //任务id
        String taskId = String.valueOf(forList.get("id"));
        //任务的问题总数
        int taskTotalIssue = new Double(String.valueOf(forList.get("total_issue"))).intValue();
        //任务问题已解决总数
        int taskCheckTotalIssue = new Double(String.valueOf(forList.get("solved_issue"))).intValue();
        //查询当前行下面有多少条检查数据以及总数据
        PageData row = (PageData) dao.findForObject("TaskMapper.queryCensorByRowStatus", param.getRowId());
        int num1 = new Double(row.get("num1").toString()).intValue();
        int num2 = new Double(row.get("num2").toString()).intValue();
        //检查未开始
        if (num1 == 0) {
            PageData data = new PageData();
            data.put("status", 1);
            data.put("operating_time", new Date());
            data.put("id", param.getRowId());
            dao.update("TaskMapper.updateRowStatus", data);
        }
        //检查中
        if (num1 < num2) {
            PageData data = new PageData();
            data.put("status", 2);
            data.put("operating_time", null);
            data.put("id", param.getRowId());
            dao.update("TaskMapper.updateRowStatus", data);
        }
        if (num1 == num2) {
            PageData data = new PageData();
            data.put("status", 3);
            data.put("x", null);
            data.put("id", param.getRowId());
            dao.update("TaskMapper.updateRowStatus", data);
        }
        PageData detailPageDate = new PageData();
        PageData censorPageDate = new PageData();
        PageData taskPageDate = new PageData();
        int totalCheck = param.getReportList().size();
        //当前检查值加减
        detailCheck += totalCheck;
        censorCheck += totalCheck;
        taskCheck += totalCheck;
        //刚开始检查
        if (taskCheck == 1) {
            detailPageDate.put("updateTime", new Date());
            censorPageDate.put("updateTime", new Date());
            taskPageDate.put("updateTime", new Date());
        }
        //模块判断是否检查完成
        if (detailCheck == detailTotalCheck) {
            detailPageDate.put("status", 3);
        } else {
            detailPageDate.put("status", 2);
        }
        //检察员判断是否检查完成
        if (censorCheck == censorTotalCheck) {
            censorPageDate.put("status", 3);
        } else {
            censorPageDate.put("status", 2);
        }
        //任务判断是否延期
        if (taskCheck == taskTotalCheck) {
            if (endTime.getTime() <= System.currentTimeMillis()) {
                censorPageDate.put("status", 6);
                taskPageDate.put("status", 6);
            } else {
                censorPageDate.put("status", 3);
                taskPageDate.put("status", 4);
            }
        } else {
            if (endTime.getTime() <= System.currentTimeMillis()) {
                censorPageDate.put("status", 5);
                taskPageDate.put("status", 5);
            } else {
                censorPageDate.put("status", 2);
                taskPageDate.put("status", 3);
            }
        }
        //问题总数
        detailPageDate.put("total_issue", detailTotalIssue - totalCheck);
        censorPageDate.put("total_issue", censorTotalIssue - totalCheck);
        taskPageDate.put("total_issue", taskTotalIssue - totalCheck);

        detailPageDate.put("solved_issue", detailCheckTotalIssue + totalCheck);
        censorPageDate.put("solved_issue", censorCheckTotalIssue + totalCheck);
        taskPageDate.put("solved_issue", taskCheckTotalIssue + totalCheck);
        detailPageDate.put("censor", detailCheck);
        detailPageDate.put("id", detailId);
        detailPageDate.put("updateTime", null);
        dao.update("TaskMapper.updateTaskDetailStatus", detailPageDate);

        censorPageDate.put("censor", censorCheck);
        censorPageDate.put("id", censorId);
        dao.update("TaskMapper.updateTaskDetailCensorStatus", censorPageDate);

        taskPageDate.put("censor", taskCheck);
        taskPageDate.put("id", taskId);
        dao.update("TaskMapper.updateTaskStatus", taskPageDate);
        //判断是否是组员在检查
        Integer functionId = (Integer) dao.findForObject("TaskMapper.queryUserByRoue", pd);
        if (functionId == 3) {
            //查询检查员儿id
            PageData censorId1 = (PageData) dao.findForObject("TaskMapper.queryCensorId", param.getDetailId());
            //查询组长的检查项目
            PageData censorChild = (PageData) dao.findForObject("TaskMapper.queryTaskCensorStatusChild", param.getDetailId());
            Integer censorCheckOutNum = (Integer) dao.findForObject("TaskMapper.queryGroupUserCheckOutNum", param.getDetailId());
            //检查完成
            int censorChildNum1 = new Double(censorChild.get("num1").toString()).intValue();
            int censorChildNum2 = new Double(censorChild.get("num2").toString()).intValue();
            //检查完成
            if (censorChildNum1 == censorChildNum2) {
                PageData data1 = new PageData();
                data1.put("checked", censorCheckOutNum + totalCheck);
                if (endTime.getTime() <= System.currentTimeMillis()) {
                    data1.put("status", 6);
                } else {
                    data1.put("status", 3);
                }
                data1.put("id", censorId1.getString("child_id"));
                data1.put("total_issue", censorTotalIssue - totalCheck);
                //还没开始检查
                dao.update("TaskMapper.updateCensorStatus", data1);
            }
            //检查中
            if (censorChildNum1 < censorChildNum2) {
                PageData data1 = new PageData();
                data1.put("checked", censorCheckOutNum + totalCheck);
                if (endTime.getTime() <= System.currentTimeMillis()) {
                    data1.put("status", 5);
                } else {
                    data1.put("status", 2);
                }
                data1.put("id", censorId1.getString("child_id"));
                data1.put("total_issue", censorTotalIssue - totalCheck);
                //还没开始检查
                dao.update("TaskMapper.updateCensorStatus", data1);
            }
        }

    }

    /**
     * 整改编辑任务
     *
     * @param param
     * @param pd
     * @throws Exception
     */
    public void taskReportEditIssue(reportIssueList param, PageData pd) throws Exception {
        pd.put("detail_id", param.getDetailId());
        //编辑问题项目，先删除之前的数据
        //查询之前的数据图片进行删除
        List<PageData> pageData1 = (List<PageData>) dao.findForList("TaskMapper.queryTaskIssueScore1", param.getRowId());
        //重置问题行的数据
        dao.update("TaskMapper.updateIssueList", param.getRowId());
        List<String> stringList = new ArrayList<>();
        for (PageData pageDatum : pageData1) {
            //删除图片表数据
            stringList.add(pageDatum.getString("issue_id"));
            dao.delete("TaskMapper.deleteTaskIssueByImageKeyValue", pageDatum);
        }
        List<String> stringArrayList = new ArrayList<>(new LinkedHashSet<>(stringList));
        //查询当前行下面有多少条检查数据以及总数据
        PageData row = (PageData) dao.findForObject("TaskMapper.queryCensorByRowStatus", param.getRowId());
        int num1 = new Double(row.get("num1").toString()).intValue();
        int num2 = new Double(row.get("num2").toString()).intValue();
        //检查未开始
        if (num1 == 1) {
            PageData data = new PageData();
            data.put("status", 1);
            data.put("operating_time", null);
            data.put("id", param.getRowId());
            dao.update("TaskMapper.updateRowStatus", data);
        }
        //检查中
        if (num1 < num2) {
            PageData data = new PageData();
            data.put("status", 2);
            data.put("operating_time", null);
            data.put("id", param.getRowId());
            dao.update("TaskMapper.updateRowStatus", data);
        }
        //查询之前需要加分的数据
        PageData forList = (PageData) dao.findForObject("TaskMapper.queryTaskStatusList", pd);
        //模块当前检查数
        int detailCheck = new Double(String.valueOf(forList.get("task_detail_check"))).intValue();
        //模块问题总数
        int detailTotalIssue = new Double(String.valueOf(forList.get("task_detail_issue"))).intValue();
        //模块已解决总数
        int detailCheckTotalIssue = new Double(String.valueOf(forList.get("task_detail_solved_issue"))).intValue();
        //模块的id
        String detailId = String.valueOf(forList.get("task_detail_id"));
        //任务问题总数
        //任务的结束时间
        Date endTime = (Date) forList.get("end_time");

        //检察员当前检查数
        int censorCheck = new Double(String.valueOf(forList.get("task_censor_checked"))).intValue();
        //检查员问题总数
        int censorTotalIssue = new Double(String.valueOf(forList.get("task_censor_issue"))).intValue();
        //检查员已解决问题总数
        int censorCheckTotalIssue = new Double(String.valueOf(forList.get("task_censor_solved_issue"))).intValue();
        //检查员id
        String censorId = String.valueOf(forList.get("task_censor_id"));

        //任务的分数
        int taskScore = new Double(String.valueOf(forList.get("score"))).intValue();
        //任务当前的检查项
        int taskCheck = new Double(String.valueOf(forList.get("censor"))).intValue();
        //任务当前的总检查项
        int taskTotalCheck = new Double(String.valueOf(forList.get("total_censor"))).intValue();
        //任务id
        String taskId = String.valueOf(forList.get("id"));
        //任务的问题总数
        int taskTotalIssue = new Double(String.valueOf(forList.get("total_issue"))).intValue();
        //任务问题已解决总数
        int taskCheckTotalIssue = new Double(String.valueOf(forList.get("solved_issue"))).intValue();
        PageData detailPageDate = new PageData();
        PageData censorPageDate = new PageData();
        PageData taskPageDate = new PageData();
        int totalCheck = stringArrayList.size();
        //当前检查值加减
        detailCheck -= totalCheck;
        censorCheck -= totalCheck;
        taskCheck -= totalCheck;
        PageData detailStatusNum = (PageData) dao.findForObject("TaskMapper.queryTaskDetailStatus1", pd.get("detail_id"));
        //判断模块是否未检查
        if (new Double(String.valueOf(detailStatusNum.get("num1"))).intValue() == 1) {
            detailPageDate.put("status", 1);
            detailPageDate.put("updateTime", null);
        } else {
            detailPageDate.put("status", 2);
        }
        detailPageDate.put("total_issue", detailTotalIssue + totalCheck);
        censorPageDate.put("total_issue", censorTotalIssue + totalCheck);
        taskPageDate.put("total_issue", taskTotalIssue + totalCheck);

        detailPageDate.put("solved_issue", detailCheckTotalIssue - totalCheck);
        censorPageDate.put("solved_issue", censorCheckTotalIssue - totalCheck);
        taskPageDate.put("solved_issue", taskCheckTotalIssue - totalCheck);

        detailPageDate.put("censor", detailCheck);
        detailPageDate.put("id", detailId);
        dao.update("TaskMapper.updateTaskDetailStatus", detailPageDate);
        PageData taskStatusNum = (PageData) dao.findForObject("TaskMapper.queryTaskStatus1", pd.get("detail_id"));
        PageData censorStatusNum = (PageData) dao.findForObject("TaskMapper.queryTaskCensorStatus1", forList);
        //判断检查任务是否未开始
        if (new Double(String.valueOf(taskStatusNum.get("num1"))).intValue() == new Double(String.valueOf(taskStatusNum.get("num2"))).intValue()) {
            taskPageDate.put("status", 1);
            taskPageDate.put("updateTime", null);
        } else {
            taskPageDate.put("status", 3);
        }
        //判断检查员的任务是否未开始
        if (new Double(String.valueOf(censorStatusNum.get("num1"))).intValue() == new Double(String.valueOf(censorStatusNum.get("num2"))).intValue()) {
            censorPageDate.put("status", 1);
            censorPageDate.put("updateTime", null);
        } else {
            censorPageDate.put("status", 2);
        }
        if (endTime.getTime() <= System.currentTimeMillis()) {
            taskPageDate.put("status", 5);
            censorPageDate.put("status", 5);
        }
        //问题总数
        detailPageDate.put("id", detailId);
        detailPageDate.put("updateTime", null);
        dao.update("TaskMapper.updateTaskDetailStatus", detailPageDate);

        censorPageDate.put("censor", censorCheck);
        censorPageDate.put("id", censorId);
        dao.update("TaskMapper.updateTaskDetailCensorStatus", censorPageDate);

        taskPageDate.put("censor", taskCheck);
        taskPageDate.put("id", taskId);
        dao.update("TaskMapper.updateTaskStatus", taskPageDate);
        //判断是否是组员在检查
        Integer functionId = (Integer) dao.findForObject("TaskMapper.queryUserByRoue", pd);
        if (functionId == 3) {
            //查询检查员儿id
            PageData censorId1 = (PageData) dao.findForObject("TaskMapper.queryCensorId", param.getDetailId());
            //查询组长的检查项目
            PageData censorChild = (PageData) dao.findForObject("TaskMapper.queryTaskCensorStatusChild", param.getDetailId());
            Integer censorCheckOutNum = (Integer) dao.findForObject("TaskMapper.queryGroupUserCheckOutNum", param.getDetailId());
            //检查完成
            int censorChildNum1 = new Double(censorChild.get("num1").toString()).intValue();
            int censorChildNum2 = new Double(censorChild.get("num2").toString()).intValue();
            //检查完成
            if (censorChildNum1 == censorChildNum2) {
                PageData data1 = new PageData();
                data1.put("checked", censorCheckOutNum - totalCheck);
                if (endTime.getTime() <= System.currentTimeMillis()) {
                    data1.put("status", 6);
                } else {
                    data1.put("status", 3);
                }
                data1.put("id", censorId1.getString("child_id"));
                data1.put("total_issue", censorTotalIssue + totalCheck);
                //还没开始检查
                dao.update("TaskMapper.updateCensorStatus", data1);
            }
            //检查中
            if (censorChildNum1 < censorChildNum2) {
                PageData data1 = new PageData();
                data1.put("checked", censorCheckOutNum - totalCheck);
                if (endTime.getTime() <= System.currentTimeMillis()) {
                    data1.put("status", 5);
                } else {
                    data1.put("status", 2);
                }
                data1.put("id", censorId1.getString("child_id"));
                data1.put("total_issue", censorTotalIssue + totalCheck);
                //还没开始检查
                dao.update("TaskMapper.updateCensorStatus", data1);
            }
        }

    }

    /**
     * 问题整改检查任务详情删除
     *
     * @param pd
     * @return
     */
    public String deleteTaskIssue(PageData pd) throws Exception {
        //删除中间表
        dao.delete("TaskMapper.deleteTaskIssueByImage", pd.get("id"));
        return "操作成功";
    }


    /**
     * 进行任务的一些状态的计算
     *
     * @param pd
     * @param flag  加分/减分
     * @param issue 问题/没问题
     * @throws Exception
     */
    public synchronized void Calculation(PageData pd, boolean flag, boolean issue) throws Exception {
        //查询之前需要加分的数据
        PageData forList = (PageData) dao.findForObject("TaskMapper.queryTaskStatusList", pd);
        //拿到页面输入的分数
        int score = new Double(pd.get("score").toString()).intValue();
        //当前行的分数
        //int rowScore = new Double(String.valueOf(pd.get("rowScore"))).intValue();
        //模块的总分
        int detailScore = new Double(String.valueOf(forList.get("task_detail_score"))).intValue();
        //模块当前检查数
        int detailCheck = new Double(String.valueOf(forList.get("task_detail_check"))).intValue();
        //模块总检查项
        int detailTotalCheck = new Double(String.valueOf(forList.get("task_detail_item"))).intValue();
        //模块问题总数
        int detailTotalIssue = new Double(String.valueOf(forList.get("task_detail_issue"))).intValue();
        //模块已解决总数
        int detailCheckTotalIssue = new Double(String.valueOf(forList.get("task_detail_solved_issue"))).intValue();
        //模块的id
        String detailId = String.valueOf(forList.get("task_detail_id"));
        //模块的状态
        int detailStatus = new Double(String.valueOf(forList.get("task_detail_status"))).intValue();
        //任务问题总数
        //任务的结束时间
        Date endTime = (Date) forList.get("end_time");

        //检察员的分数
        int censorScore = new Double(String.valueOf(forList.get("task_censor_score"))).intValue();
        //检察员当前检查数
        int censorCheck = new Double(String.valueOf(forList.get("task_censor_checked"))).intValue();
        //检查员总检查数
        int censorTotalCheck = new Double(String.valueOf(forList.get("general_inspection"))).intValue();
        //检查员问题总数
        int censorTotalIssue = new Double(String.valueOf(forList.get("task_censor_issue"))).intValue();
        //检查员已解决问题总数
        int censorCheckTotalIssue = new Double(String.valueOf(forList.get("task_censor_solved_issue"))).intValue();
        //检查员id
        String censorId = String.valueOf(forList.get("task_censor_id"));
        //检查员状态
        int censorStatus = new Double(String.valueOf(forList.get("task_censor_status"))).intValue();

        //任务的分数
        int taskScore = new Double(String.valueOf(forList.get("score"))).intValue();
        //任务当前的检查项
        int taskCheck = new Double(String.valueOf(forList.get("censor"))).intValue();
        //任务当前的总检查项
        int taskTotalCheck = new Double(String.valueOf(forList.get("total_censor"))).intValue();
        //任务id
        String taskId = String.valueOf(forList.get("id"));
        //任务的问题总数
        int taskTotalIssue = new Double(String.valueOf(forList.get("total_issue"))).intValue();
        //任务问题已解决总数
        int taskCheckTotalIssue = new Double(String.valueOf(forList.get("solved_issue"))).intValue();

        PageData rowDate = new PageData();
        PageData detailPageDate = new PageData();
        PageData censorPageDate = new PageData();
        PageData taskPageDate = new PageData();
        //页面给的问题总数
        int totalCheck = 0;

        //true是添加问题
        if (flag) {
            rowDate.put("id", pd.get("id"));
            rowDate.put("status", 3);
            rowDate.put("score", score);
            rowDate.put("issue", totalCheck);
            rowDate.put("time", new Date());
            dao.update("TaskMapper.updateTaskDetailRow", rowDate);
            //当前检查值加减
            detailCheck += 1;
            censorCheck += 1;
            taskCheck += 1;
            //刚开始检查
            if (taskCheck == 1) {
                detailPageDate.put("updateTime", new Date());
                censorPageDate.put("updateTime", new Date());
                taskPageDate.put("updateTime", new Date());
            }
            //模块判断是否检查完成
            if (detailCheck == detailTotalCheck) {
                detailPageDate.put("status", 3);
            } else {
                detailPageDate.put("status", 2);
            }
            //检察员判断是否检查完成
            if (censorCheck == censorTotalCheck) {
                censorPageDate.put("status", 3);
            } else {
                censorPageDate.put("status", 2);
            }
            //任务判断是否延期
            if (taskCheck == taskTotalCheck) {
                if (endTime.getTime() <= System.currentTimeMillis()) {
                    censorPageDate.put("status", 6);
                    taskPageDate.put("status", 6);
                } else {
                    censorPageDate.put("status", 3);
                    taskPageDate.put("status", 4);
                }
            } else {
                if (endTime.getTime() <= System.currentTimeMillis()) {
                    censorPageDate.put("status", 5);
                    taskPageDate.put("status", 5);
                } else {
                    censorPageDate.put("status", 2);
                    taskPageDate.put("status", 3);
                }
            }
            //问题总数
            if (!issue) {
                detailPageDate.put("total_issue", detailTotalIssue);
                censorPageDate.put("total_issue", censorTotalIssue);
                taskPageDate.put("total_issue", taskTotalIssue);

                detailPageDate.put("solved_issue", detailCheckTotalIssue);
                censorPageDate.put("solved_issue", censorCheckTotalIssue);
                taskPageDate.put("solved_issue", taskCheckTotalIssue);
            }
            detailPageDate.put("censor", detailCheck);
            detailPageDate.put("score", detailScore);
            detailPageDate.put("id", detailId);
            detailPageDate.put("updateTime", null);
            dao.update("TaskMapper.updateTaskDetailStatus", detailPageDate);
        } else {
            rowDate.put("id", pd.get("id"));
            rowDate.put("score", 0);
            rowDate.put("status", 1);
            rowDate.put("issue", totalCheck);
            rowDate.put("time", null);
            dao.update("TaskMapper.updateTaskDetailRow", rowDate);
            //当前检查值加减
            detailCheck -= 1;
            censorCheck -= 1;
            taskCheck -= 1;
            //结算问题项目
            if (issue) {
                detailPageDate.put("total_issue", detailTotalIssue + 1);
                censorPageDate.put("total_issue", censorTotalIssue + 1);
                taskPageDate.put("total_issue", taskTotalIssue + 1);
            } else {
                detailPageDate.put("total_issue", detailTotalIssue);
                censorPageDate.put("total_issue", censorTotalIssue);
                taskPageDate.put("total_issue", taskTotalIssue);
            }
            detailPageDate.put("solved_issue", detailCheckTotalIssue);
            censorPageDate.put("solved_issue", censorCheckTotalIssue);
            taskPageDate.put("solved_issue", taskCheckTotalIssue);
            PageData detailStatusNum = (PageData) dao.findForObject("TaskMapper.queryTaskDetailStatus1", pd.get("detail_id"));

            //判断模块是否未检查
            if (new Double(String.valueOf(detailStatusNum.get("num1"))).intValue() == new Double(String.valueOf(detailStatusNum.get("num2"))).intValue()) {
                detailPageDate.put("status", 1);
                detailPageDate.put("updateTime", null);
            } else {
                detailPageDate.put("status", 2);
            }
            detailPageDate.put("censor", detailCheck);
            detailPageDate.put("score", detailScore);
            detailPageDate.put("id", detailId);
            dao.update("TaskMapper.updateTaskDetailStatus", detailPageDate);
            PageData taskStatusNum = (PageData) dao.findForObject("TaskMapper.queryTaskStatus1", pd.get("detail_id"));
            PageData censorStatusNum = (PageData) dao.findForObject("TaskMapper.queryTaskCensorStatus1", forList);
            //判断检查任务是否未开始
            if (new Double(String.valueOf(taskStatusNum.get("num1"))).intValue() == new Double(String.valueOf(taskStatusNum.get("num2"))).intValue()) {
                taskPageDate.put("status", 1);
                taskPageDate.put("updateTime", null);
            } else {
                taskPageDate.put("status", 3);
            }
            //判断检查员的任务是否未开始
            if (new Double(String.valueOf(censorStatusNum.get("num1"))).intValue() == new Double(String.valueOf(censorStatusNum.get("num2"))).intValue()) {
                censorPageDate.put("status", 1);
                censorPageDate.put("updateTime", null);
            } else {
                censorPageDate.put("status", 2);
            }
            if (endTime.getTime() <= System.currentTimeMillis()) {
                taskPageDate.put("status", 5);
                censorPageDate.put("status", 5);
            }
        }


        censorPageDate.put("censor", censorCheck);
        censorPageDate.put("score", censorScore);
        censorPageDate.put("id", censorId);
        dao.update("TaskMapper.updateTaskDetailCensorStatus", censorPageDate);

        taskPageDate.put("censor", taskCheck);
        taskPageDate.put("score", taskScore);
        taskPageDate.put("id", taskId);
        dao.update("TaskMapper.updateTaskStatus", taskPageDate);
    }

    /**
     * 进行任务的一些状态的计算
     *
     * @param pd
     * @param flag 添加问题/编辑问题
     * @throws Exception
     */
    public synchronized void Calculation(PageData pd, boolean flag) throws Exception {
        //查询之前需要加分的数据
        PageData forList = (PageData) dao.findForObject("TaskMapper.queryTaskStatusList", pd);

        //拿到页面输入的分数
        int score = new Double(pd.get("score").toString()).intValue();
        //当前行的分数
        int rowScore = new Double(String.valueOf(pd.get("new_score"))).intValue();

        //模块的总分
        int detailScore = new Double(String.valueOf(forList.get("task_detail_score"))).intValue();
        //模块当前检查数
        int detailCheck = new Double(String.valueOf(forList.get("task_detail_check"))).intValue();
        //模块总检查项
        int detailTotalCheck = new Double(String.valueOf(forList.get("task_detail_item"))).intValue();
        //模块问题总数
        int detailTotalIssue = new Double(String.valueOf(forList.get("task_detail_issue"))).intValue();
        //模块已解决总数
        int detailCheckTotalIssue = new Double(String.valueOf(forList.get("task_detail_solved_issue"))).intValue();
        //模块的id
        String detailId = String.valueOf(forList.get("task_detail_id"));
        //模块的状态
        int detailStatus = new Double(String.valueOf(forList.get("task_detail_status"))).intValue();
        //任务问题总数
        //任务的结束时间
        Date endTime = (Date) forList.get("end_time");

        //检察员的分数
        int censorScore = new Double(String.valueOf(forList.get("task_censor_score"))).intValue();
        //检察员当前检查数
        int censorCheck = new Double(String.valueOf(forList.get("task_censor_checked"))).intValue();
        //检查员总检查数
        int censorTotalCheck = new Double(String.valueOf(forList.get("general_inspection"))).intValue();
        //检查员问题总数
        int censorTotalIssue = new Double(String.valueOf(forList.get("task_censor_issue"))).intValue();
        //检查员已解决问题总数
        int censorCheckTotalIssue = new Double(String.valueOf(forList.get("task_censor_solved_issue"))).intValue();
        //检查员id
        String censorId = String.valueOf(forList.get("task_censor_id"));
        //检查员状态
        int censorStatus = new Double(String.valueOf(forList.get("task_censor_status"))).intValue();

        //任务的分数
        int taskScore = new Double(String.valueOf(forList.get("score"))).intValue();
        //任务当前的检查项
        int taskCheck = new Double(String.valueOf(forList.get("censor"))).intValue();
        //任务当前的总检查项
        int taskTotalCheck = new Double(String.valueOf(forList.get("total_censor"))).intValue();
        //任务id
        String taskId = String.valueOf(forList.get("id"));
        //任务的问题总数
        int taskTotalIssue = new Double(String.valueOf(forList.get("total_issue"))).intValue();
        //任务问题已解决总数
        int taskCheckTotalIssue = new Double(String.valueOf(forList.get("solved_issue"))).intValue();

        PageData rowDate = new PageData();
        PageData detailPageDate = new PageData();
        PageData censorPageDate = new PageData();
        PageData taskPageDate = new PageData();
        //页面给的问题总数
        int totalCheck = new Double(String.valueOf(pd.get("issueTotal"))).intValue();

        //问题数
        //true是添加问题
        if (flag) {
            rowDate.put("id", pd.get("id"));
            rowDate.put("status", 3);
            rowDate.put("score", score);
            if (score > rowScore) {
                rowDate.put("score", score);
            } else {
                if (score < 0) {
                    rowDate.put("score", rowScore - Math.abs(score));
                } else {
                    rowDate.put("score", rowScore - score);
                }
            }
            rowDate.put("issue", totalCheck);
            rowDate.put("time", new Date());
            dao.update("TaskMapper.updateTaskDetailRow", rowDate);
            //当前检查值加减
            detailCheck += 1;
            censorCheck += 1;
            taskCheck += 1;
            //刚开始检查
            if (taskCheck == 1) {
                detailPageDate.put("updateTime", new Date());
                censorPageDate.put("updateTime", new Date());
                taskPageDate.put("updateTime", new Date());
            }
            //模块判断是否检查完成
            if (detailCheck == detailTotalCheck) {
                detailPageDate.put("status", 3);
            } else {
                detailPageDate.put("status", 2);
            }
            //检察员判断是否检查完成
            if (censorCheck == censorTotalCheck) {
                censorPageDate.put("status", 3);
            } else {
                censorPageDate.put("status", 2);
            }
            //任务判断是否延期
            if (taskCheck == taskTotalCheck) {
                if (endTime.getTime() <= System.currentTimeMillis()) {
                    censorPageDate.put("status", 6);
                    taskPageDate.put("status", 6);
                } else {
                    censorPageDate.put("status", 3);
                    taskPageDate.put("status", 4);
                }
            } else {
                if (endTime.getTime() <= System.currentTimeMillis()) {
                    censorPageDate.put("status", 5);
                    taskPageDate.put("status", 5);
                } else {
                    censorPageDate.put("status", 2);
                    taskPageDate.put("status", 3);
                }
            }
            //问题总数
            detailPageDate.put("total_issue", detailTotalIssue + totalCheck);
            censorPageDate.put("total_issue", censorTotalIssue + totalCheck);
            taskPageDate.put("total_issue", taskTotalIssue + totalCheck);

            detailPageDate.put("solved_issue", detailCheckTotalIssue);
            censorPageDate.put("solved_issue", censorCheckTotalIssue);
            taskPageDate.put("solved_issue", taskCheckTotalIssue);


            detailPageDate.put("censor", detailCheck);
            detailPageDate.put("score", detailScore);
            detailPageDate.put("id", detailId);
            detailPageDate.put("updateTime", null);
            dao.update("TaskMapper.updateTaskDetailStatus", detailPageDate);
        } else {
            rowDate.put("id", pd.get("id"));
            rowDate.put("score", score);
            if (score > rowScore) {
                rowDate.put("score", score - rowScore);
            } else {
                if (score < 0) {
                    rowDate.put("score", rowScore - Math.abs(score));
                } else {
                    rowDate.put("score", rowScore - score);
                }
            }
            rowDate.put("status", 1);
            rowDate.put("issue", totalCheck);
            rowDate.put("time", null);
            dao.update("TaskMapper.updateTaskDetailRow", rowDate);
            detailPageDate.put("total_issue", detailTotalIssue - totalCheck);
            censorPageDate.put("total_issue", censorTotalIssue - totalCheck);
            taskPageDate.put("total_issue", taskTotalIssue - totalCheck);

            detailPageDate.put("solved_issue", detailCheckTotalIssue + totalCheck);
            censorPageDate.put("solved_issue", censorCheckTotalIssue + totalCheck);
            taskPageDate.put("solved_issue", taskCheckTotalIssue + totalCheck);
            //当前检查值加减
            detailCheck -= 1;
            censorCheck -= 1;
            taskCheck -= 1;
            PageData detailStatusNum = (PageData) dao.findForObject("TaskMapper.queryTaskDetailStatus1", pd.get("detail_id"));

            //判断模块是否未检查
            if (new Double(String.valueOf(detailStatusNum.get("num1"))).intValue() == new Double(String.valueOf(detailStatusNum.get("num2"))).intValue()) {
                detailPageDate.put("status", 1);
                detailPageDate.put("updateTime", null);
            } else {
                detailPageDate.put("status", 2);
            }
            detailPageDate.put("censor", detailCheck);
            detailPageDate.put("score", detailScore);
            detailPageDate.put("id", detailId);
            dao.update("TaskMapper.updateTaskDetailStatus", detailPageDate);
            PageData taskStatusNum = (PageData) dao.findForObject("TaskMapper.queryTaskStatus1", pd.get("detail_id"));
            PageData censorStatusNum = (PageData) dao.findForObject("TaskMapper.queryTaskCensorStatus1", forList);
            //判断检查任务是否未开始
            if (new Double(String.valueOf(taskStatusNum.get("num1"))).intValue() == new Double(String.valueOf(taskStatusNum.get("num2"))).intValue()) {
                taskPageDate.put("status", 1);
                taskPageDate.put("updateTime", null);
            } else {
                taskPageDate.put("status", 3);
            }
            //判断检查员的任务是否未开始
            if (new Double(String.valueOf(censorStatusNum.get("num1"))).intValue() == new Double(String.valueOf(censorStatusNum.get("num2"))).intValue()) {
                censorPageDate.put("status", 1);
                censorPageDate.put("updateTime", null);
            } else {
                censorPageDate.put("status", 2);
            }
            if (endTime.getTime() <= System.currentTimeMillis()) {
                taskPageDate.put("status", 5);
                censorPageDate.put("status", 5);
            }
        }

        censorPageDate.put("censor", censorCheck);
        censorPageDate.put("score", censorScore);
        censorPageDate.put("id", censorId);
        dao.update("TaskMapper.updateTaskDetailCensorStatus", censorPageDate);

        taskPageDate.put("censor", taskCheck);
        taskPageDate.put("score", taskScore);
        taskPageDate.put("id", taskId);
        dao.update("TaskMapper.updateTaskStatus", taskPageDate);
    }

    /**
     * 上报任务
     * user_id
     * id 检查表id
     * task_id 任务id
     *
     * @param pd
     * @return
     */
    public String escalationTask(PageData pd) throws Exception {
        String ids = pd.get("userId").toString();
        String replace = ids.replace("[", "");
        String str = replace.replace("]", "").trim();
        String[] split = str.split(",");
        //上报人员
        List<String> list = new ArrayList<String>();
        for (String s : split) {
            String substring = s.replaceAll("\\\"", "");
            list.add(substring);
        }
        Integer functionId = (Integer) dao.findForObject("TaskMapper.queryUserByRole", pd.get("uid"));
        if (functionId == null) {
            return "您的账号存在问题请联系管理员";
        }
        if (functionId == 2) {
            //查询数据
            PageData data = (PageData) dao.findForObject("TaskMapper.queryGroupUserTask", pd);
            //判断是否检查完成
            int status = new Double(String.valueOf(data.get("status"))).intValue();
            //状态:1未进行,2已删除,3进行中,4已完成,5已延期,6延期完成,7未上报,8待审核,9待分配,10已汇总
            if (status == 1) {
                return "该暂时不能上报";
            }
            if (status == 2) {
                return "该暂时不能上报";
            }
            if (status == 3) {
                return "该暂时不能上报";
            }
            if (status == 8) {
                return "该暂时不能上报";
            }
            if (status == 9) {
                return "该暂时不能上报";
            }
            if (status == 10) {
                return "该暂时不能上报";
            }
            int totalIssue = new Double(String.valueOf(data.get("total_issue"))).intValue();
            //上报表插入数据
            if (totalIssue != 0) {
                PageData taskPaDa = new PageData();
                String id = UuidUtil.get32UUID();
                taskPaDa.put("id", id);
                taskPaDa.put("task_id", data.get("id"));
                taskPaDa.put("remark", null);
                taskPaDa.put("unit_id", data.get("unit_id"));
                taskPaDa.put("seit_id", data.get("site_id"));
                taskPaDa.put("total_issue", totalIssue);
                taskPaDa.put("group_id", pd.get("uid"));
                taskPaDa.put("user_id", null);
                taskPaDa.put("type", 2);
                taskPaDa.put("view_id", data.get("group_id"));
                taskPaDa.put("status", 1);
                taskPaDa.put("flag", data.get("type"));
                dao.save("TaskMapper.createdTaskUp", taskPaDa);

                //加入任务组长
//                if (!list.contains(data.get("group_id").toString())) {
//                    list.add(data.get("group_id").toString());
//                }
                for (String userId : list) {
                    PageData createdTaskByUp = new PageData();
                    createdTaskByUp.put("id", UuidUtil.get32UUID());
                    createdTaskByUp.put("group_reoprt_id", id);
                    createdTaskByUp.put("user_id", userId);
                    createdTaskByUp.put("status", 2);
                    dao.save("TaskMapper.createdTaskByUp", createdTaskByUp);
                }
                //插入检查表的id
                List<PageData> censorIds = (List<PageData>) dao.findForList("TaskMapper.queryTaskCensorList", pd.get("task_id"));
                for (PageData censorId : censorIds) {
                    PageData createdReport = new PageData();
                    createdReport.put("id", UuidUtil.get32UUID());
                    createdReport.put("user_id", censorId.get("user_id"));
                    createdReport.put("group_report_id", id);
                    dao.save("TaskMapper.createdReport", createdReport);
                }
            }
            //改变进度表的状态
            //   pd.put("status", 3);
            //dao.update("TaskMapper.updateTaskSeep", pd);
            //改变任务表的状态
            if(new Double(String.valueOf(pd.get("type"))).intValue()==2){
                dao.update("TaskMapper.updateGroupByTaskStatus3", pd.get("id"));
            }else{
                if (totalIssue == 0) {
                    dao.update("TaskMapper.updateGroupByTaskStatus", pd.get("task_id"));
                } else {
                    dao.update("TaskMapper.updateGroupByTaskStatus1", pd.get("task_id"));
                }
            }

        }
        if (functionId == 3) {
            //查询状态是否完成
            PageData data = (PageData) dao.findForObject("TaskMapper.queryTaskCensorStatus", pd);
            //判断是否检查完成
            int status = new Double(String.valueOf(data.get("status"))).intValue();
            //状态:1未检查,2检查中,3检查完成,4已上报,5已延期,6延期完成,7未上报,8待审核,9审核通过,10审核驳回
            if (status == 1) {
                return "该暂时不能上报";
            }
            if (status == 2) {
                return "该暂时不能上报";
            }
            if (status == 4) {
                return "该暂时不能上报";
            }
            if (status == 5) {
                return "该暂时不能上报";
            }
            if (status == 8) {
                return "该暂时不能上报";
            }
            if (status == 9) {
                return "该暂时不能上报";
            }
            String report_id = UuidUtil.get32UUID();
            //上报表插入数据
            String type = (String) data.get("type");
            PageData taskPaDa = new PageData();
            String id = UuidUtil.get32UUID();
            taskPaDa.put("id", id);
            taskPaDa.put("task_id", data.get("id"));
            taskPaDa.put("remark", null);
            taskPaDa.put("unit_id", data.get("unit_id"));
            taskPaDa.put("seit_id", data.get("site_id"));
            taskPaDa.put("total_issue", data.get("total_issue"));
            taskPaDa.put("group_id", pd.get("uid"));
            taskPaDa.put("report_id", report_id);
            taskPaDa.put("user_id", null);
            taskPaDa.put("type", 1);
            taskPaDa.put("flag", type);
            taskPaDa.put("view_id", data.get("group_id"));
            taskPaDa.put("status", 1);
            dao.save("TaskMapper.createdTaskUp", taskPaDa);
            for (String s : list) {
                PageData createdTaskByUp = new PageData();
                createdTaskByUp.put("id", UuidUtil.get32UUID());
                createdTaskByUp.put("group_reoprt_id", id);
                createdTaskByUp.put("user_id", s);
                createdTaskByUp.put("status", 2);
                dao.save("TaskMapper.createdTaskByUp", createdTaskByUp);
            }
            if(new Double(type).intValue()!=2){
                if (!data.get("group_id").toString().equals(data.get("task_user_id").toString())) {
                    PageData createdTaskByUp = new PageData();
                    createdTaskByUp.put("id", UuidUtil.get32UUID());
                    createdTaskByUp.put("group_reoprt_id", id);
                    createdTaskByUp.put("user_id", data.get("group_id"));
                    createdTaskByUp.put("status", 2);
                    dao.save("TaskMapper.createdTaskByUp", createdTaskByUp);
                }
            }
            //插入检查表的id
            PageData createdReport = new PageData();
            createdReport.put("id", report_id);
            createdReport.put("user_id", data.get("user_id"));
            createdReport.put("group_report_id", id);
            dao.save("TaskMapper.createdReport", createdReport);
            //改变进度表的状态
//                pd.put("status", 3);id -> f9444793ba3b467d9600633c0651c51b
//                dao.update("TaskMapper.updateTaskSeep", pd);
            dao.save("TaskMapper.updateGroupByCensorStatus", pd.get("id"));
        }
        return "上报成功";
    }

    /**
     * 图片上传 EXCELTEMPLATEPATH
     *
     * @param file
     * @return
     */
    public PageData uploadImage(HttpServletRequest request, MultipartFile file) throws Exception {
        //把文件写到项目里边
        String filename = file.getOriginalFilename();
        String url = uploadImage(request, file, Const.ISSUEIMAGEPATH);
        //存入数据库
        PageData pd = new PageData();
        String uuid = UuidUtil.get32UUID();
        pd.put("id", uuid);
        pd.put("name", filename);
        pd.put("url", url);
        pd.put("type", 1);
        pd.put("status", 1);
        dao.save("TaskMapper.createdImage", pd);
        PageData rePd = new PageData();
        rePd.put("url", url);
        rePd.put("name", filename);
        rePd.put("id", uuid);
        return rePd;
    }

    /**
     * 图片上传 EXCELTEMPLATEPATH
     *
     * @param file
     * @return uploadImageReport
     */
    public PageData uploadImageReport(HttpServletRequest request, MultipartFile file) throws Exception {
        //把文件写到项目里边
        String filename = file.getOriginalFilename();
        String url = uploadImage(request, file, Const.ISSUEIMAGEPATH);
        //存入数据库
        PageData pd = new PageData();
        String uuid = UuidUtil.get32UUID();
        pd.put("id", uuid);
        pd.put("name", filename);
        pd.put("url", url);
        pd.put("type", 2);
        pd.put("status", 1);
        dao.save("TaskMapper.createdImage", pd);
        PageData rePd = new PageData();
        rePd.put("url", url);
        rePd.put("name", filename);
        rePd.put("id", uuid);
        return rePd;
    }


    /**
     * 删除图片
     *
     * @param pd
     * @return
     */
    public String deleteImage(PageData pd) throws Exception {
        dao.update("TaskMapper.deleteImage", pd);
        String update = (String) dao.findForObject("TaskMapper.queryImageName", pd);
        String path = Const.ISSUEIMAGEPATH + update;
        deleteAnyone(path);
        return "删除成功";
    }

    public static boolean deleteDir(String dirName) {
        if (dirName.endsWith(File.separator)) {
            //dirName不以分隔符结尾则自动添加分隔符
            dirName = dirName + File.separator;
        }
        //根据指定的文件名创建File对象
        File file = new File(dirName);
        //目录不存在或者
        if (!file.exists() || (!file.isDirectory())) {
            System.out.println("目录删除失败" + dirName + "目录不存在！");
            return false;
        }
        //列出源文件下所有文件，包括子目录
        File[] fileArrays = file.listFiles();
        //将源文件下的所有文件逐个删除
        for (int i = 0; i < fileArrays.length; i++) {
            deleteAnyone(fileArrays[i].getAbsolutePath());
        }
        //删除当前目录
        if (file.delete()) {
            System.out.println("目录" + dirName + "删除成功！");
        }
        return true;

    }

    /**
     * 判断指定的文件或文件夹删除是否成功
     *
     * @param FileName 文件或文件夹的路径
     * @return true or false 成功返回true，失败返回false
     */
    public static boolean deleteAnyone(String FileName) {
        File file = new File(FileName);
        if (!file.exists()) {
            System.out.println("文件" + FileName + "不存在，删除失败！");
            return false;
        } else {
            if (file.isFile()) {
                return deleteFile(FileName);
            } else {
                return deleteDir(FileName);
            }
        }
    }

    /**
     * 判断指定的文件删除是否成功
     *
     * @param fileName 文件路径
     * @return true or false 成功返回true，失败返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("文件" + fileName + "删除成功！");
                return true;
            } else {
                System.out.println("文件" + fileName + "删除失败！");
                return false;
            }
        } else {
            System.out.println("文件" + fileName + "不存在，删除失败！");
            return false;
        }
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
    public Map<Integer, List<String>> ExcelModelByTotalScoreAndCheckItem(List<Norm> normList, String modelId) {
        Map<Integer, List<String>> map = new LinkedHashMap<Integer, List<String>>();
        List<String> list = new LinkedList<String>();
        Integer score = 0;
        for (Norm norm : normList) {
            for (NormDetail normDetail : norm.getNormDetails()) {
                if (normDetail.getId().equals(modelId)) {
                    String totalScore = normDetail.getTotal_score();
                    if (totalScore != null) {
                        double v = Double.parseDouble(totalScore);
                        int i = new Double(v).intValue();
                        score = i;
                    }
                    List<String> strings = ExcelModuleProject(norm.getNormDetails(), modelId);
                    list.addAll(strings);
                }
            }
        }
        list = new ArrayList<String>(new LinkedHashSet<String>(list));
        map.put(score, list);
        return map;
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
    public List<String> ModuleByNormId(List<Norm> normList, List<TaskDetailUserVo> taskDetailUserVoList) {
        List<String> list = new ArrayList<String>();
        for (Norm norm : normList) {
            for (NormDetail normDetail : norm.getNormDetails()) {
                for (TaskDetailUserVo taskDetailUserVo : taskDetailUserVoList) {
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


    /**
     * 图片上传
     *
     * @param file
     * @param path
     * @return
     */
    public String uploadImage(HttpServletRequest request, MultipartFile file, String path) {
        String uuid = UuidUtil.get32UUID();
        String url = request.getSession().getServletContext().getRealPath(path);
        String imagePath = path + uuid + ".png";
        try {
            File uploadDir = new File(url);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(url + uuid + ".png");
            byte[] bytes = file.getBytes();
            out.write(bytes);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagePath;
    }
}


