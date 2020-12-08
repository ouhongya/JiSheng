package com.fh.service.app;

import com.fh.dao.DaoSupport;
import com.fh.entity.Page;
import com.fh.entity.app.Excel;
import com.fh.entity.app.Norm;
import com.fh.entity.app.NormDetail;
import com.fh.util.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.*;

import static com.fh.service.app.ExcelUtil.readData;


/**
 * @author OH
 */
@Service("ExcelSettingService")
public class ExcelSettingService {

    @Resource(name = "daoSupport")
    private DaoSupport dao;


    /**
     * 上传Excel导入数据库
     *
     * @return
     * @throws Exception
     */
    public  ResultModel<String> uploadExcel(HttpServletRequest res, MultipartFile fileStream, String uid) throws Exception {
        if (!fileStream.isEmpty()) {
            String path = res.getSession().getServletContext().getRealPath(Const.EXCELLIST);
            // 如果文件夹不存在则创建该文件夹
            File uploadDir = new File(path);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            String originalFilename = fileStream.getOriginalFilename();
            String filename = UuidUtil.get32UUID();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1, originalFilename.length());
            switch (suffix) {
                case "xls":
                    break;
                case "xlsx":
                    break;
                default:
                   return   ResultModel.failure("文件格式不正确");
            }
            String excelPath = path + filename+"."+suffix;
            //判断文件是否重复
            if (getFiles(path, filename)) {
                return   ResultModel.failure("该文件名称已存在");
            }
            //把文件写到项目里边
            FileOutputStream out = new FileOutputStream(excelPath);
            byte[] bytes = fileStream.getBytes();
            out.write(bytes);
            out.flush();
            out.close();
            //解析Excel
            if (readData(new File(excelPath), suffix) instanceof String) {
                return   ResultModel.failure("解析格式错误请重新上传");

            }
            Map<String, LinkedList<Map<String, String>>> map = (Map<String, LinkedList<Map<String, String>>>) readData(new File(excelPath), suffix);
            String user_id = uid;
            Integer function = (Integer) dao.findForObject("ExcelSettingMapper.queryUserByRoue", user_id);
            //存到数据库
            PageData excel = new PageData();
            String size = fileSize(excelPath);
            String excelId = UuidUtil.get32UUID();
            excel.put("id", excelId);
            excel.put("original_name", originalFilename);
            excel.put("new_name", originalFilename.substring(0, originalFilename.indexOf(".")));
            excel.put("url", Const.EXCELLIST + filename);
            excel.put("magnitude", size);
            excel.put("user_id", uid);
            excel.put("parent_id", 0);
            excel.put("status", 1);
            excel.put("created_time", new Date());
            if (function != 1) {
                excel.put("type", 2);
            } else {
                excel.put("type", 1);
            }
            excel.put("view", 2);
            dao.save("ExcelSettingMapper.saveExcel", excel);
            for (String key : map.keySet()) {
                //每个sheet的title存入数据库
                LinkedList<Map<String, String>> mapList = map.get(key);
                PageData norm = new PageData();
                String str = null;
                String normId = UuidUtil.get32UUID();
                int index = -1;
                for (Map<String, String> stringMap : mapList) {
                    index+=1;
                    //每个sheet的内容存入数据库
                    PageData normDetail = new PageData();
                    String other = "";
                    int MapIndex = -1;
                    int in = 0;
                    for (String s : stringMap.keySet()) {
                        MapIndex+=1;
                        String value = stringMap.get(s);
                        if (index <= 9) {
                            switch (MapIndex) {
                                case 0:
                                    norm.put("name", key);
                                    break;
                                case 1:
                                    norm.put("unit", value);
                                    break;
                                case 2:
                                    norm.put("content", value);
                                    break;
                                case 3:
                                    norm.put("total_score", value);
                                    break;
                                case 4:
                                    norm.put("score_time", value);
                                    break;
                                default:
                                    String trim = value.trim();
                                    if (trim != null && !trim.equals("")) {
                                        str += value + "$";
                                    }
                                    break;
                            }
                        }
                        if (index >= 11) {
                            ++in;
                            switch (in) {
                                case 1:
                                    String serial = String.valueOf(value);
                                    String val = null;
                                    if (!serial.matches("[\u4E00-\u9FA5]+")) {
                                        if (serial.length() == 3) {
                                            String substring = serial.substring(2, 3);
                                            if (substring.equals("0")) {
                                                int i = new Double(serial).intValue();
                                                val = String.valueOf(i);
                                            } else {
                                                val = serial;
                                            }
                                        } else {
                                            val = serial;
                                        }
                                    } else {
                                        val = serial;
                                    }
                                    normDetail.put("serial", val);
                                    break;
                                case 2:
                                    normDetail.put("item", value);
                                    break;
                                case 3:
                                    normDetail.put("content", value);
                                    break;
                                case 4:
                                    normDetail.put("total_score", value);
                                    break;
                                case 5:
                                    normDetail.put("score", value);
                                    break;
                                case 6:
                                    normDetail.put("untitled", value);
                                    break;
                                case 7:
                                    normDetail.put("mode", value);
                                    break;
                                case 8:
                                    normDetail.put("standard", value);
                                    break;
                                default:
                                    other+=value+"$";
                                    break;
                            }
                        }
                    }
                    //存准则主表
                    if (index == 10) {
                        norm.put("other", str);
                        norm.put("id", normId);
                        norm.put("excel_id", excelId);
                        norm.put("user_id", uid);
                        norm.put("status", 1);
                        norm.put("type", 1);
                        norm.put("view", 2);
                        norm.put("created_time", new Date());
                        dao.save("ExcelSettingMapper.saveNorm", norm);
                    }
                    if (index >= 11) {
                        normDetail.put("id", UuidUtil.get32UUID());
                        normDetail.put("norm_id", normId);
                        normDetail.put("user_id", uid);
                        normDetail.put("other", other);
                        normDetail.put("status", 1);
                        normDetail.put("type", 1);
                        normDetail.put("view", 2);
                        normDetail.put("created_time", new Date());
                        dao.save("ExcelSettingMapper.saveNormDetail", normDetail);
                    }
                }
            }

            return   ResultModel.success("上传成功");
        } else {

            return   ResultModel.failure("请重新上传,获取文件失败~");
        }

    }

    /**
     * 查询Excel列表
     *
     * @return
     */
    public List<PageData> queryExcelList(Page page) throws Exception {
        return (List<PageData>)dao.findForList("ExcelSettingMapper.datalistPage", page);
    }



    /**
     * Excel下发
     * @param pd
     * @return
     * @throws Exception
     */
    public String allot(PageData pd) throws Exception {
        String ids = pd.get("user_id").toString();
        String replace = ids.replace("[", "");
        String str = replace.replace("]", "").trim();
        String[] split = str.split(",");
        //下发的用户
        for (String s : split) {
            if(s!=null&&!s.equals("")){
                PageData pd1 = new PageData();
                String id = s.substring(1, s.length() - 1);
                pd1.put("excel_id", pd.get("id"));
                pd1.put("user_id", id);
                dao.save("ExcelSettingMapper.creatUserToNorm", pd1);
            }
        }
        if (!split[0].equals("")) {
            //修改状态
            PageData pageData = new PageData();
            pageData.put("id", pd.get("id"));
            dao.update("ExcelSettingMapper.updateNormAllotLeader", pageData);
            dao.update("ExcelSettingMapper.updateNormByExcel", pageData);

        }else{
            PageData pd1 = new PageData();
            pd1.put("excel_id", pd.get("id"));
            dao.save("ExcelSettingMapper.updateExcelView", pd1);
            dao.delete("ExcelSettingMapper.deleteExcelByUser", pd1);
        }
        return "操作成功";
    }

    /**
     * 查询下发
     *
     * @param pd
     * @return
     */
    public List<String> queryAllot(PageData pd) throws Exception {
        List<String> data = (List<String>) dao.findForList("ExcelSettingMapper.queryAllot", pd.get("id"));
        return data;
    }

    /**
     * 修改下发
     * @param pd
     * @return
     */
    public String updateAllot(PageData pd) throws Exception {
        String ids = pd.get("user_id").toString();
        String replace = ids.replace("[", "");
        String str = replace.replace("]", "").trim();
        String[] split = str.split(",");
        PageData pd1 = new PageData();
        pd1.put("excel_id", pd.get("id"));
        dao.delete("ExcelSettingMapper.deleteExcelByUser", pd1);
        pd1.put("id", pd.get("id"));
        pd1.put("status", 2);
        dao.update("ExcelSettingMapper.updateExcelView", pd1);
        String s = split[0];
        if(!s.equals("")){
            this.allot(pd);
        }
        return "操作成功";
    }

    /**
     * 批量启用Excel 1启用
     *
     * @param pd
     * @return
     */
    public String disable(PageData pd) throws Exception {
        String ids = pd.get("id").toString();
        String replace = ids.replace("[", "");
        String str = replace.replace("]", "").trim();
        String[] split = str.split(",");
        for (String s : split) {
            PageData pd1 = new PageData();
            String id = s.substring(1, s.length() - 1);
            pd1.put("id", id);
            pd1.put("status", 1);
            dao.update("ExcelSettingMapper.updateExcel", pd1);
            dao.update("ExcelSettingMapper.updateNorm", pd1);
        }
        return "操作成功";
    }

    /**
     * 停用Excel 2停用
     *
     * @param pd
     * @return
     * @throws Exception
     */
    public String enable(PageData pd) throws Exception {
        String ids = pd.get("id").toString();
        String replace = ids.replace("[", "");
        String str = replace.replace("]", "").trim();
        String[] split = str.split(",");
        for (String s : split) {
            PageData pd1 = new PageData();
            String id = s.substring(1, s.length() - 1);
            pd1.put("id", id);
            pd1.put("status", 2);
            dao.update("ExcelSettingMapper.updateExcel", pd1);
            dao.update("ExcelSettingMapper.updateNorm", pd1);
        }
        return "操作成功";
    }

    /**
     * 批量删除Excel 3删除
     *
     * @param pd
     * @return
     * @throws Exception
     */
    public String deleteExcel(PageData pd) throws Exception {
        String ids = pd.get("id").toString();
        String replace = ids.replace("[", "");
        String str = replace.replace("]", "").trim();
        String[] split = str.split(",");
        for (String s : split) {
            PageData pd1 = new PageData();
            String id = s.substring(1, s.length() - 1);
            String userId = (String) dao.findForObject("ExcelSettingMapper.queryExcelByUser", id);
            if(userId.equals(String.valueOf(pd.get("uid")))){
                pd1.put("id", id);
                pd1.put("status", 3);
                dao.update("ExcelSettingMapper.updateExcel", pd1);
                dao.update("ExcelSettingMapper.updateNorm", pd1);
            }else{
                return "不能删除该准则";
            }
        }
        return "操作成功";
    }

    /**
     * 重命名excel
     *
     * @param pd
     * @return
     * @throws Exception
     */
    public String rename(PageData pd) throws Exception {
        dao.update("ExcelSettingMapper.rename", pd);
        return "操作成功";
    }

    /**
     * 查询Excel一二三级
     *
     * @return
     */
    public List<Excel> queryExcelOneAndTwo(PageData pd) throws Exception {
        List<Excel> data = (List<Excel>) dao.findForList("ExcelSettingMapper.queryExcelOneAndTwo", pd);
        for (Excel excel : data) {
            List<Norm> forList = (List<Norm>) dao.findForList("ExcelSettingMapper.queryExcelByNorm", excel.getId());
            if (forList.size() != 0) {
                for (Norm norm : forList) {
                    List<NormDetail> normDetails = (List<NormDetail>) dao.findForList("ExcelSettingMapper.queryExcelByNormDetail", norm.getId());
                    if (normDetails.size() != 0) {
                        norm.setNormDetails(normDetails);
                    }
                }
                excel.setNormList(forList);
            }
        }
        return data;
    }

    /**
     * 查询专责和组长列表
     *
     * @return
     * @throws Exception
     */
    public PageData queryUserBySpecialty(PageData pageData) throws Exception {
        PageData pd = new PageData();
        String uid = (String) pageData.get("uid");
        //专责
        List<PageData> specialty = (List<PageData>) dao.findForList("ExcelSettingMapper.queryUserBySpecialty", 1);
        //组长
        List<PageData> group = (List<PageData>) dao.findForList("ExcelSettingMapper.queryUserBySpecialty", 2);
        List<PageData> user = (List<PageData>) dao.findForList("ExcelSettingMapper.queryUserBySpecialty", 3);
        group.forEach(item -> {
            if (item.get("USER_ID").equals(uid)) {
                PageData data = new PageData();
                data.put("USER_ID", item.get("USER_ID"));
                data.put("NAME", item.get("NAME"));
                user.add(data);
            }
        });

        //excel列表
        List<PageData> excel = (List<PageData>) dao.findForList("ExcelSettingMapper.queryExcelList", 2);
        pd.put("specialty", specialty);
        pd.put("group", group);
        pd.put("excel", excel);
        pd.put("user", user);
        return pd;
    }

    /**
     * 模板下载
     *
     * @return
     */
    public HttpServletResponse downloadTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = request.getSession().getServletContext().getRealPath(Const.EXCELTEMPLATEPATH);
        FileDownload.fileDownload(response, path + "一、标准的模板.xlsx", "一、标准的模板.xlsx");
        return response;
    }

    /**
     * 模板上传路径
     *
     * @param multipartFile
     * @return
     */
    public String uploadTemplate(MultipartFile multipartFile) {
        String filename = multipartFile.getOriginalFilename();
        String url = Const.EXCELTEMPLATEPATH + FileUpload.fileUp(multipartFile, Const.EXCELTEMPLATEPATH, filename);
        return url;
    }

    /**
     * 读取本地文件
     *
     * @param fileName
     */
    public static void readFileByLines(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读一行，读入null时文件结束
            while ((tempString = reader.readLine()) != null) {
                Thread.sleep(500);
                System.out.println(tempString);
                line++;
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

    }

    /**
     * FileInputStream转换InputStream
     *
     * @param fileInput
     * @return
     */
    public InputStream getInputStream(FileInputStream fileInput) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = -1;
        InputStream inputStream = null;
        try {
            while ((n = fileInput.read(buffer)) != -1) {
                baos.write(buffer, 0, n);

            }
            byte[] byteArray = baos.toByteArray();
            inputStream = new ByteArrayInputStream(byteArray);
            return inputStream;


        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 读取判断文件是否重复
     *
     * @param filepath
     * @param name
     * @return
     */
    public boolean getFiles(String filepath, String name) {
        ArrayList<String> files = new ArrayList<String>();
        File file = new File(filepath);
        File[] tempLists = file.listFiles();
        for (int i = 0; i < tempLists.length; i++) {
            if (tempLists[i].isFile()) {
                String filePath = tempLists[i].toString();
                String[] split = filePath.split("\\\\");
                files.add(split[split.length - 1]);
            }
        }
        for (int i = 0; i < files.size(); i++) {
            System.out.println(files.get(i));
        }
        if (files.contains(name)) {
            return true;
        }
        return false;
    }

    /**
     * 获取文件大小
     *
     * @param path
     * @return
     */
    public String fileSize(String path) {
        FileChannel fc = null;
        try {
            File f = new File(path);
            if (f.exists() && f.isFile()) {
                FileInputStream fis = new FileInputStream(f);
                fc = fis.getChannel();
                String netFileSizeDescription = getNetFileSizeDescription(fc.size());
                return netFileSizeDescription;
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
            if (null != fc) {
                try {
                    fc.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    /**
     * 字节转换
     *
     * @param size
     * @return
     */
    public static String getNetFileSizeDescription(long size) {
        StringBuffer bytes = new StringBuffer();
        DecimalFormat format = new DecimalFormat("###.0");
        if (size >= 1024 * 1024 * 1024) {
            double i = (size / (1024.0 * 1024.0 * 1024.0));
            bytes.append(format.format(i)).append("GB");
        } else if (size >= 1024 * 1024) {
            double i = (size / (1024.0 * 1024.0));
            bytes.append(format.format(i)).append("MB");
        } else if (size >= 1024) {
            double i = (size / (1024.0));
            bytes.append(format.format(i)).append("KB");
        } else if (size < 1024) {
            if (size <= 0) {
                bytes.append("0B");
            } else {
                bytes.append((int) size).append("B");
            }
        }
        return bytes.toString();
    }
}

