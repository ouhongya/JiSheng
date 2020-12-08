package com.fh.service.app;

import com.fh.dao.DaoSupport;
import com.fh.entity.Page;
import com.fh.util.PageData;
import com.fh.util.UuidUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;


@Service("SettingService")
public class SettingService {


    @Resource(name = "daoSupport")
    private DaoSupport dao;


    /**
     * 查询公司以及单位
     *
     * @param pd
     * @return
     * @throws Exception
     */
    public List<PageData> queryUnitAndSite(PageData pd) throws Exception {
        Page page = new Page();
        Integer size = Integer.parseInt(pd.get("size").toString());
        page.setShowCount(size);
        Integer currentPage = Integer.parseInt(pd.get("page").toString());
        page.setCurrentPage(currentPage);
        page.setPd(pd);
        List<PageData> forList = null;
        if (!StringUtils.isNotBlank(pd.get("search").toString())) {
            forList = (List<PageData>) dao.findForList("SettingMapper.queryUnitAndSite", page);
        } else {
            forList = (List<PageData>) dao.findForList("SettingMapper.queryUnitAndSiteSearch", page);
        }
        return forList;
    }

    /**
     * 查询系统初始化
     *
     * @return
     * @throws Exception
     */
    public List<PageData> findAllSystemSettings() throws Exception {
        return (List<PageData>) dao.findForList("SettingMapper.findAllSystemSettings", null);
    }

    /**
     * 修改MySQL配置
     *
     * @param pd
     * @return
     * @throws Exception
     */
    public String updateMySQLSetting(PageData pd) throws Exception {
        dao.update("SettingMapper.updateMySQLSetting", pd);
        return "操作成功";
    }

    /**
     * 修改Redis配置
     *
     * @param pd
     * @return
     * @throws Exception
     */
    public String updateRedisSetting(PageData pd) throws Exception {
        dao.update("SettingMapper.updateRedisSetting", pd);
        return "操作成功";
    }

    /**
     * 修改SMS配置
     *
     * @param pd
     * @return
     * @throws Exception
     */
    public String updateSmsSetting(PageData pd) throws Exception {
        dao.update("SettingMapper.updateSmsSetting", pd);
        return "操作成功";
    }

    /**
     * 查询单位列表
     *
     * @param pd
     * @return
     * @throws Exception
     */
    public List<PageData> queryUnitAll(PageData pd) throws Exception {
        return (List<PageData>) dao.findForList("SettingMapper.queryUnitAll", pd);
    }

    /**
     * 添加单位
     *
     * @param pd
     * @return
     * @throws Exception
     */
    public String addUnit(PageData pd) throws Exception {
        List<PageData> pageData = (List<PageData>) dao.findForList("SettingMapper.queryUnitExist", pd);
        if (pageData.size() != 0) {
            return "该单位已存在";
        }
        pd.put("id", UuidUtil.get32UUID());
        dao.save("SettingMapper.addUnit", pd);
        return "添加成功";
    }


    /**
     * 修改单位
     *
     * @param pd
     * @return
     * @throws Exception
     */
    public String updateUnit(PageData pd) throws Exception {
        List<PageData> pageData = (List<PageData>) dao.findForList("SettingMapper.queryUnitOne", pd);
        if (pageData.size() == 0) {
            return "没有该单位";
        }
        dao.update("SettingMapper.updateUnit", pd);
        return "修改成功";
    }

    /**
     * 删除单位
     *
     * @param pd
     * @return
     * @throws Exception
     */
    public String deleteUnit(PageData pd) throws Exception {
        dao.update("SettingMapper.deleteUnit", pd);
        return "删除成功";
    }

    /**
     * 查询站点列表
     *
     * @param pd
     * @return
     * @throws Exception
     */
    public List<PageData> querySiteAll(PageData pd) throws Exception {
        return (List<PageData>) dao.findForList("SettingMapper.querySiteAll", pd);
    }

    /**
     * 添加站点
     *
     * @param pd
     * @return
     * @throws Exception
     */
    public String addSite(PageData pd) throws Exception {
        List<PageData> pageData = (List<PageData>) dao.findForList("SettingMapper.querySiteOneExit", pd);
        if (pageData.size() != 0) {
            return "该站点已存在";
        }
        pd.put("id", UuidUtil.get32UUID());
        dao.save("SettingMapper.createdSite", pd);
        return "添加成功";
    }

    /**
     * 修改站点
     *
     * @param pd
     * @return
     * @throws Exception
     */
    public String updateSite(PageData pd) throws Exception {
        List<PageData> pageData = (List<PageData>) dao.findForList("SettingMapper.querySiteOne", pd);
        if (pageData.size() == 0) {
            return "该站点不存在";
        }
        dao.update("SettingMapper.updateUnit", pd);
        return "修改成功";
    }

    /**
     * 删除站点
     *
     * @param pd
     * @return
     * @throws Exception
     */
    public String deleteSite(PageData pd) throws Exception {
        dao.update("SettingMapper.deleteSite", pd);
        return "删除成功";
    }

    /**
     * 查询公告列表
     *
     * @param pd
     * @return
     */
    public List<PageData> queryBulletin(PageData pd) throws Exception {
        Page page = new Page();
        page.setCurrentPage(Integer.parseInt(pd.get("size").toString()));
        page.setShowCount(Integer.parseInt(pd.get("page").toString()));
        List<PageData> forList = (List<PageData>) dao.findForList("SettingMapper.datalistPage", page);
        int totalPage = page.getTotalPage();
        if (forList.size() != 0) {
            forList.get(0).put("total", totalPage);
        }
        return forList;
    }

    /**
     * 添加公告
     *
     * @param pd
     * @return
     */
    public String addBulletin(PageData pd) throws Exception {
        String messageId = UuidUtil.get32UUID();
        pd.put("id", messageId);
        pd.put("type", 1);
        pd.put("created_time", new Date());
        dao.save("SettingMapper.addBulletin", pd);
        List<String> userIds = (List<String>) dao.findForList("SettingMapper.queryUserList", null);
        for (String userId : userIds) {
            PageData pageData = new PageData();
            pageData.put("id", messageId);
            pageData.put("user_id", userId);
            dao.save("SettingMapper.addBulletinMQ", pageData);
        }
        return "发布成功";
    }

    /**
     * 删除公告
     *
     * @param pd
     * @return
     */
    public String deleteBulletin(PageData pd) throws Exception {
        String messageId = (String) pd.get("id");
        dao.update("SettingMapper.deleteBulletin", messageId);
        return "删除成功";
    }


    /**
     * 查询单个用户的消息
     * @param pd
     * @return
     */
    public List<PageData> queryUserByBulletin(PageData pd) throws Exception {
        Page page = new Page();
        page.setCurrentPage(Integer.parseInt(pd.get("page").toString()));
        page.setShowCount(Integer.parseInt(pd.get("size").toString()));
        page.setPd(pd);
        List<PageData> dataList = (List<PageData>) dao.findForList("SettingMapper.datalistPage1", page);
        if (dataList.size() != 0) {
            dataList.get(0).put("total", page.getTotalPage());
        }
        return dataList;
    }
    /**
     * 查询单个用户的消息数量
     * @param pd
     * @return
     */
    public Integer queryUserByBulletinNum(PageData pd) throws Exception {
        Integer num = (Integer) dao.findForObject("SettingMapper.queryUserByBulletinNum", pd);
        return num;
    }

    /**
     * 公告已读操作
     *
     * @param pd
     * @return
     */
    public String readByBulletin(PageData pd) throws Exception {
        dao.update("SettingMapper.readByBulletin", pd);
        return "操作成功";
    }
}
