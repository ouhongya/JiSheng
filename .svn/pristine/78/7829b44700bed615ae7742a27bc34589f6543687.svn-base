package com.fh.service.app;

import com.alibaba.fastjson.JSON;
import com.fh.dao.DaoSupport;
import com.fh.entity.app.Statistics;
import com.fh.util.PageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Service("StatisticsService")
public class StatisticsService {

    @Resource(name = "daoSupport")
    private DaoSupport dao;

    @Autowired
    private RectifyService rectifyService;

    /**
     * 查询统计图列表
     * unitId
     * userId
     * excelId
     *
     * @return
     */
    public synchronized PageData queryStatisticsList(PageData pageData) throws Exception {
        PageData pd = new PageData();
        Statistics param = JSON.parseObject(String.valueOf(pageData.get("param")), Statistics.class);
        if (param.getUserId().size() == 0) {
            List<PageData> dataList = rectifyService.queryUserByTaskToUser(pageData);
            List<String> userIds = new ArrayList<>();
            for (PageData data : dataList) {
                userIds.add(String.valueOf(data.get("USER_ID")));
            }
            List<String> strings = new ArrayList<String>(new LinkedHashSet<>(userIds));
            param.setUserId(strings);
        }
        if (param.getUnitId().size() == 0) {
            List<PageData> dataList = queryUnitList(pageData);
            List<String> unitIds = new ArrayList<>();
            for (PageData data : dataList) {
                unitIds.add(String.valueOf(data.get("id")));
            }
            List<String> strings = new ArrayList<String>(new LinkedHashSet<>(unitIds));
            param.setUnitId(strings);
        }
        if (param.getExcelId().size() == 0) {
            List<PageData> dataList = rectifyService.queryUserNormList(pageData);
            List<String> excelIds = new ArrayList<>();
            for (PageData data : dataList) {
                excelIds.add(String.valueOf(data.get("id")));
            }
            List<String> strings = new ArrayList<String>(new LinkedHashSet<>(excelIds));
            param.setExcelId(strings);
        }
        //项目id
        if (param.getTaskId() == null) {
            List<PageData> dataList = rectifyService.queryUserByTask(pageData);
            List<String> taskIds = new ArrayList<>();
            for (PageData data : dataList) {
                taskIds.add(String.valueOf(data.get("id")));
            }
            List<String> strings = new ArrayList<String>(new LinkedHashSet<>(taskIds));
            param.setTaskId(strings);
        }
        //查看角色权限
        Integer function = (Integer) dao.findForObject("StatisticsMapper.queryUserByRole", pageData);
        if (function == 1) {
            param.setType(1);
        }
        //查询项目进度
        PageData taskSchedule = (PageData) dao.findForObject("StatisticsMapper.queryTaskSchedule", param);
        if(taskSchedule==null){
            return null;
        }
        //拿到项目的进度
        String speed = speed(String.valueOf(taskSchedule.get("censor")), String.valueOf(taskSchedule.get("total_censor")));
        pd.put("speed", speed);
        pd.put("total_censor", taskSchedule.get("total_censor"));
        pd.put("censor", taskSchedule.get("censor"));
        //单位问题统计图
        //获取所有的站点儿
        List<Object> list = new ArrayList<>();
        List<PageData> siteList = (List<PageData>) dao.findForList("StatisticsMapper.queryTaskSiteList", param);
        List<String> siteId = new ArrayList<>();
        for (PageData data : siteList) {
            siteId.add(String.valueOf(data.get("unit_id")));
        }
        List<String> strings = new ArrayList<String>(new LinkedHashSet<>(siteId));
        for (String string : strings) {
            param.setSiteId(string);
            PageData sitePageDate = null;
            if (function == 1) {
                sitePageDate = (PageData) dao.findForObject("StatisticsMapper.queryTaskSiteToIssueList", param);
            } else {
                sitePageDate = (PageData) dao.findForObject("StatisticsMapper.queryTaskSiteToIssueList1", param);
            }
            if (sitePageDate != null) {
                Map<String, Object> map = new HashMap<>();
                List<Map> maps = new ArrayList<>();
                Map<String, String> issueScore = new HashMap<>();
                issueScore.put("totalIssue", String.valueOf(sitePageDate.get("totalIssue")));
                issueScore.put("totalScore", String.valueOf(sitePageDate.get("totalScore")));
                maps.add(issueScore);
                map.put("value", maps);
                for (PageData data : siteList) {
                    String id = String.valueOf(data.get("unit_id"));
                    if (id.equals(string)) {
                        map.put("key", data.get("name"));
                    }
                }

                list.add(map);
            }
        }
        pd.put("siteList", list);
        //相同问题数量统计图
        //查询全部问题的id以及名
        List<PageData> issueCountIds = (List<PageData>) dao.findForList("StatisticsMapper.issueCountIds", param);
        if (issueCountIds.size() != 0) {
            List<String> moduleIds = new ArrayList<>();
            for (PageData issueCountId : issueCountIds) {
                String id = (String) issueCountId.get("id");
                moduleIds.add(id);
            }
            Map<String, Integer> maps = NumOfEle(moduleIds);
            List<Object> objectList = new ArrayList<>();
            for (String integer : maps.keySet()) {
                Map<String, String> map = new HashMap<>();
                Integer integer1 = maps.get(integer);
                if (integer1 >= param.getNum()) {
                    String name = (String) dao.findForObject("StatisticsMapper.queryNormDetailName", integer);
                    map.put("name", name);
                    map.put("num", String.valueOf(integer1));
                    objectList.add(map);
                } else {
                    String name = (String) dao.findForObject("StatisticsMapper.queryNormDetailName", integer);
                    map.put("name", name);
                    map.put("num", String.valueOf(integer1));
                    objectList.add(map);
                }
            }
            pd.put("issueCountIds", objectList);
        }
        //检查模块问题数统计
        //拿出所有的问题模块
        List<PageData> issueModuleIds = (List<PageData>) dao.findForList("StatisticsMapper.issueModuleIds", param);
        if (issueModuleIds.size() != 0) {
            pd.put("issueModule", issueModuleIds);
        }
        return pd;
    }


    /**
     * 总体完成明细查询
     *
     * @param pageData
     * @return
     */
    public List<PageData> queryTaskOverDetail(PageData pageData) throws Exception {
        Statistics param = JSON.parseObject(String.valueOf(pageData.get("param")), Statistics.class);
        if (param.getUserId().size() == 0) {
            List<PageData> dataList = rectifyService.queryUserByTaskToUser(pageData);
            List<String> userIds = new ArrayList<>();
            for (PageData data : dataList) {
                userIds.add(String.valueOf(data.get("USER_ID")));
            }
            List<String> strings = new ArrayList<String>(new LinkedHashSet<>(userIds));
            param.setUserId(strings);
        }
        if (param.getUnitId().size() == 0) {
            List<PageData> dataList = queryUnitList(pageData);
            List<String> unitIds = new ArrayList<>();
            for (PageData data : dataList) {
                unitIds.add(String.valueOf(data.get("id")));
            }
            List<String> strings = new ArrayList<String>(new LinkedHashSet<>(unitIds));
            param.setUnitId(strings);
        }
        if (param.getExcelId().size() == 0) {
            List<PageData> dataList = rectifyService.queryUserNormList(pageData);
            List<String> excelIds = new ArrayList<>();
            for (PageData data : dataList) {
                excelIds.add(String.valueOf(data.get("id")));
            }
            List<String> strings = new ArrayList<String>(new LinkedHashSet<>(excelIds));
            param.setExcelId(strings);
        }
        //项目id
        if (param.getTaskId() == null) {
            List<PageData> dataList = rectifyService.queryUserByTask(pageData);
            List<String> taskIds = new ArrayList<>();
            for (PageData data : dataList) {
                taskIds.add(String.valueOf(data.get("id")));
            }
            List<String> strings = new ArrayList<String>(new LinkedHashSet<>(taskIds));
            param.setTaskId(strings);
        }
        //查看角色权限
        Integer function = (Integer) dao.findForObject("StatisticsMapper.queryUserByRole", pageData);
        if (function == 1) {
            param.setType(1);
            List<PageData> taskSchedule = (List<PageData>) dao.findForList("StatisticsMapper.queryTaskOverDetail", param);
            for (PageData data : taskSchedule) {
                String id = (String) data.get("user_id");
                data.put("name", dao.findForObject("StatisticsMapper.queryUserName", id));
            }
            return taskSchedule;
        } else {
            List<PageData> taskSchedule = (List<PageData>) dao.findForList("StatisticsMapper.queryTaskOverDetail2", param);
            for (PageData data : taskSchedule) {
                String id = (String) data.get("user_id");
                data.put("name", dao.findForObject("StatisticsMapper.queryUserName", id));
            }
            return taskSchedule;
        }
    }


    /**
     * 工作量计算
     *
     * @param pageData
     * @return
     */
    public List<PageData> queryWorkload(PageData pageData) throws Exception {
        Statistics param = JSON.parseObject(String.valueOf(pageData.get("param")), Statistics.class);
        if (param.getUserId().size() == 0) {
            List<PageData> dataList = rectifyService.queryUserByTaskToUser(pageData);
            List<String> userIds = new ArrayList<>();
            for (PageData data : dataList) {
                userIds.add(String.valueOf(data.get("USER_ID")));
            }
            List<String> strings = new ArrayList<String>(new LinkedHashSet<>(userIds));
            param.setUserId(strings);
        }
        if (param.getUnitId().size() == 0) {
            List<PageData> dataList = queryUnitList(pageData);
            List<String> unitIds = new ArrayList<>();
            for (PageData data : dataList) {
                unitIds.add(String.valueOf(data.get("id")));
            }
            List<String> strings = new ArrayList<String>(new LinkedHashSet<>(unitIds));
            param.setUnitId(strings);
        }
        if (param.getExcelId().size() == 0) {
            List<PageData> dataList = rectifyService.queryUserNormList(pageData);
            List<String> excelIds = new ArrayList<>();
            for (PageData data : dataList) {
                excelIds.add(String.valueOf(data.get("id")));
            }
            List<String> strings = new ArrayList<String>(new LinkedHashSet<>(excelIds));
            param.setExcelId(strings);
        }
        //项目id
        if (param.getTaskId() == null) {
            List<PageData> dataList = rectifyService.queryUserByTask(pageData);
            List<String> taskIds = new ArrayList<>();
            for (PageData data : dataList) {
                taskIds.add(String.valueOf(data.get("id")));
            }
            List<String> strings = new ArrayList<String>(new LinkedHashSet<>(taskIds));
            param.setTaskId(strings);
        }
        //查看角色权限
        Integer function = (Integer) dao.findForObject("StatisticsMapper.queryUserByRole", pageData);
        if (function == 1) {
            param.setType(1);
            List<PageData> taskSchedule = (List<PageData>) dao.findForList("StatisticsMapper.queryWorkload", param);
            for (PageData data : taskSchedule) {
                String id = (String) data.get("user_id");
                data.put("name", dao.findForObject("StatisticsMapper.queryUserName", id));
            }
            return taskSchedule;
        }
        return null;
    }


    /**
     * 查询单位列表
     *
     * @return
     */
    public List<PageData> queryUnitList(PageData pageData) throws Exception {
        List<PageData> pageDataList = rectifyService.queryUserByTask(pageData);
        List<PageData> dataList = new ArrayList<PageData>();
        for (PageData data : pageDataList) {
            PageData data1 = new PageData();
            PageData pd = (PageData) dao.findForObject("StatisticsMapper.queryUnitList", String.valueOf(data.get("id")));
            data1.put("id", pd.get("id"));
            data1.put("name", pd.get("name"));
            dataList.add(data1);
        }
        dataList.stream().filter(distinctByKey(b -> b.get("id")));
        return dataList;
    }

    /**
     * 计算两个值得百分比
     *
     * @param num1
     * @param num2
     * @return
     */
    public String speed(String num1, String num2) {
        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(2);
        String result = numberFormat.format(new Float(num1) / new Float(num2) * 100);
        return result;
    }

    /**
     * 数组对象去重
     *
     * @param keyExtractor
     * @param <T>
     * @return
     */
    public <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }


    /**
     * 判断元素出现的次数
     *
     * @param list
     * @return
     */
    public Map<String, Integer> NumOfEle(List<String> list) {
        Map<String, Integer> integerMap = new HashMap<>();
        Map<String, Integer> map = new HashMap<>();
        for (String str : list) {
            Integer num = map.get(str);
            map.put(str, num == null ? 1 : num + 1);
        }
        Iterator it01 = map.keySet().iterator();
        while (it01.hasNext()) {
            String key = (String) it01.next();
            integerMap.put(key, map.get(key));
        }
        Map<String, Integer> stringIntegerMap = sortByValue(integerMap);
        return stringIntegerMap;
    }

    /**
     * 对数据进行排序
     *
     * @param map
     * @param <K>
     * @param <V>
     * @return
     */
    public <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        Map<K, V> result = new LinkedHashMap<>();
        Stream<Map.Entry<K, V>> st = map.entrySet().stream();
        st.sorted(Comparator.comparing(e -> e.getValue())).forEach(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }
}
