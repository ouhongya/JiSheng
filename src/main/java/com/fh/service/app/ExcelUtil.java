package com.fh.service.app;

import com.fh.util.ObjectExcelRead;
import com.fh.util.UuidUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * excel 工具类
 */
public class ExcelUtil {

    /**
     * excel解析并
     *
     * @param file
     * @param suffix
     * @return
     */
    public static   Object readData(File file, String suffix) {
        Map<String, List<Map<Integer, String>>> result = new LinkedHashMap<String, List<Map<Integer, String>>>();
        if (suffix.equals("xls")) {
            HSSFWorkbook wb = null;
            FileInputStream fis = null;
            //excel开始解析过滤
            try {
                fis = new FileInputStream(file);
                wb = new HSSFWorkbook(fis);
                //获取有多少个sheet
                int numberOfSheets = wb.getNumberOfSheets();
                String str = "";
                for (int x = 0; x < numberOfSheets; x++) {
                    //获取sheet的名字
                    String sheetName = wb.getSheetName(x);
                    //过滤无关的sheet名字
                    if (isSheetTile(sheetName)) {
                        List<Map<Integer, String>> list = new ArrayList<Map<Integer, String>>();
                        HSSFSheet sheet = wb.getSheetAt(x);
                        int columnNum = 0;
                        if (sheet.getRow(0) != null) {
                            columnNum = sheet.getRow(0).getLastCellNum() - sheet.getRow(0).getFirstCellNum();
                        }
                        if (columnNum > 0) {
                            for (Row row : sheet) {
                                Object[] singleRow = new String[columnNum];
                                int n = 0;
                                Map<Integer, String> map = new HashMap<Integer, String>();
                                for (int i = 0; i < columnNum; i++) {
                                    Cell cell = row.getCell(i, Row.CREATE_NULL_AS_BLANK);
                                    switch (cell.getCellType()) {
                                        case HSSFCell.CELL_TYPE_FORMULA:
                                            String value = null;
                                            try {
                                                value = String.valueOf(cell.getNumericCellValue());
                                            } catch (IllegalStateException e) {
                                                value = String.valueOf(cell.getRichStringCellValue());
                                            }
                                            map.put(i, value);
                                            break;
                                        case 3:
                                            singleRow[n] = "";
                                            map.put(i, singleRow[n].toString());
                                            break;
                                        case 4:
                                            singleRow[n] = Boolean.toString(cell.getBooleanCellValue());
                                            map.put(i, singleRow[n].toString());
                                            break;
                                        // 数值
                                        case 0:
                                            if (DateUtil.isCellDateFormatted(cell)) {
                                                SimpleDateFormat sdf = null;
                                                if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("h:mm")) {
                                                    sdf = new SimpleDateFormat("HH:mm");
                                                } else {// 日期
                                                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                                                }
                                                Date date = cell.getDateCellValue();
                                                System.out.print(sdf.format(date) + "|");
                                                map.put(i, sdf.format(date));
                                            } else {
                                                cell.setCellType(1);
                                                String temp = cell.getStringCellValue();
                                                // 判断是否包含小数点，如果不含小数点，则以字符串读取，如果含小数点，则转换为Double类型的字符串
                                                if (!StringUtils.isBlank(str)) {
                                                    boolean matches = temp.matches("[+-]?[+-]?[0-9]+(\\\\.[0-9]+)?");
                                                    singleRow[n] = matches;
                                                } else {
                                                    singleRow[n] = temp.trim();
                                                }
                                                map.put(i, singleRow[n].toString());
                                            }
                                            break;
                                        case 1:
                                            singleRow[n] = cell.getStringCellValue().trim();
                                            map.put(i, singleRow[n].toString());
                                            break;
                                        case 5:
                                            singleRow[n] = "";
                                            map.put(i, singleRow[n].toString());
                                            break;
                                        case 7:
                                            cell.setCellType(1);
                                            String temp = cell.getStringCellValue();
                                            // 判断是否包含小数点，如果不含小数点，则以字符串读取，如果含小数点，则转换为Double类型的字符串
                                            if (temp.indexOf(".") > -1) {
                                                temp = String.valueOf(new Double(temp)).trim();
                                                Double cny = Double.parseDouble(temp);//6.2041
                                                DecimalFormat df = new DecimalFormat("0.00");
                                                String CNY = df.format(cny);
                                                System.out.print(CNY + "|");
                                                map.put(i, CNY);
                                            } else {
                                                singleRow[n] = temp.trim();
                                                map.put(i, singleRow[n].toString());
                                            }
                                            break;
                                        default:
                                            singleRow[n] = "";
                                            map.put(i, singleRow[n].toString());
                                            break;
                                    }
                                    n++;
                                }
                                list.add(map);
                            }
                            result.put(filterSheetTile(sheetName), list);
                        }
                    } else {
                        return "excel的sheet格式不对,请重新编辑";
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (Exception e) {
                    }
                }
                if (wb != null) {
                    try {
                        wb.close();
                    } catch (Exception e) {
                    }
                }
            }
        } else {
            XSSFWorkbook wb = null;
            FileInputStream fis = null;
            //excel开始解析过滤
            try {
                fis = new FileInputStream(file);
                wb = new XSSFWorkbook(fis);
                //获取有多少个sheet
                int numberOfSheets = wb.getNumberOfSheets();
                String str = "";
                for (int x = 0; x < numberOfSheets; x++) {
                    //获取sheet的名字
                    String sheetName = wb.getSheetName(x);
                    //过滤无关的sheet名字
                    if (isSheetTile(sheetName)) {
                        List<Map<Integer, String>> list = new ArrayList<Map<Integer, String>>();
                        XSSFSheet sheet = wb.getSheetAt(x);
                        int columnNum = 0;
                        if (sheet.getRow(0) != null) {
                            columnNum = sheet.getRow(0).getLastCellNum() - sheet.getRow(0).getFirstCellNum();
                        }
                        if (columnNum > 0) {
                            for (Row row : sheet) {
                                Object[] singleRow = new String[columnNum];
                                int n = 0;
                                Map<Integer, String> map = new HashMap<Integer, String>();
                                for (int i = 0; i < columnNum; i++) {
                                    Cell cell = row.getCell(i, Row.CREATE_NULL_AS_BLANK);
                                    switch (cell.getCellType()) {
                                        case HSSFCell.CELL_TYPE_FORMULA:
                                            String value = null;
                                            try {
                                                value = String.valueOf(cell.getNumericCellValue());
                                            } catch (IllegalStateException e) {
                                                value = String.valueOf(cell.getRichStringCellValue());
                                            }
                                            map.put(i, value);
                                            break;
                                        case 3:
                                            singleRow[n] = "";
                                            map.put(i, singleRow[n].toString());
                                            break;
                                        case 4:
                                            singleRow[n] = Boolean.toString(cell.getBooleanCellValue());
                                            map.put(i, singleRow[n].toString());
                                            break;
                                        // 数值
                                        case 0:
                                            if (DateUtil.isCellDateFormatted(cell)) {
                                                SimpleDateFormat sdf = null;
                                                if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("h:mm")) {
                                                    sdf = new SimpleDateFormat("HH:mm");
                                                } else {// 日期
                                                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                                                }
                                                Date date = cell.getDateCellValue();
                                                System.out.print(sdf.format(date) + "|");
                                                map.put(i, sdf.format(date));
                                            } else {
                                                cell.setCellType(1);
                                                String temp = cell.getStringCellValue();
                                                // 判断是否包含小数点，如果不含小数点，则以字符串读取，如果含小数点，则转换为Double类型的字符串
                                                if (!StringUtils.isBlank(str)) {
                                                    boolean matches = temp.matches("[+-]?[+-]?[0-9]+(\\\\.[0-9]+)?");
                                                    singleRow[n] = matches;
                                                } else {
                                                    singleRow[n] = temp.trim();
                                                }
                                                map.put(i, singleRow[n].toString());
                                            }
                                            break;
                                        case 1:
                                            singleRow[n] = cell.getStringCellValue().trim();
                                            map.put(i, singleRow[n].toString());
                                            break;
                                        case 5:
                                            singleRow[n] = "";
                                            map.put(i, singleRow[n].toString());
                                            break;
                                        case 7:
                                            cell.setCellType(1);
                                            String temp = cell.getStringCellValue();
                                            // 判断是否包含小数点，如果不含小数点，则以字符串读取，如果含小数点，则转换为Double类型的字符串
                                            if (temp.indexOf(".") > -1) {
                                                temp = String.valueOf(new Double(temp)).trim();
                                                Double cny = Double.parseDouble(temp);//6.2041
                                                DecimalFormat df = new DecimalFormat("0.00");
                                                String CNY = df.format(cny);
                                                System.out.print(CNY + "|");
                                                map.put(i, CNY);
                                            } else {
                                                singleRow[n] = temp.trim();
                                                map.put(i, singleRow[n].toString());
                                            }
                                            break;
                                        default:
                                            singleRow[n] = "";
                                            map.put(i, singleRow[n].toString());
                                            break;
                                    }
                                    n++;
                                }
                                list.add(map);
                            }
                            result.put(filterSheetTile(sheetName), list);
                        }
                    } else {
                        return "解析格式错误";
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (Exception e) {
                    }
                }
                if (wb != null) {
                    try {
                        wb.close();
                    } catch (Exception e) {
                    }
                }
            }
        }
        //解析完成组装数据结构
        Map<String, List<Map<String, String>>> resultRes = new LinkedHashMap<String, List<Map<String, String>>>();
        for (String key : result.keySet()) {
            List<Map<String, String>> listList = new LinkedList<>();
            List<Map<Integer, String>> maps = result.get(key);
            //标题
            Map<Integer, String> map = maps.get(10);
            List<String> map1 = new LinkedList<>();
            for (Integer integer : map.keySet()) {
                String s = map.get(integer);
                map1.add(s);
            }
            Integer index = -1;
            for (Map<Integer, String> stringMap : maps) {
                index+=1;
                //头
                if (index <= 9) {
                    Map<String, String> map2 = new LinkedHashMap<>();
                    for (Integer integer : stringMap.keySet()) {
                        String s = stringMap.get(integer);
                        map2.put(integer.toString(), s);
                    }
                    listList.add(map2);
                }
                //标题
                if (index == 10) {
                    Map<String, String> map2 = new LinkedHashMap<>();
                    for (Integer integer : stringMap.keySet()) {
                        String s = stringMap.get(integer);
                        map2.put(s, s);
                    }
                    //removeNullKey(map2);
                    listList.add(map2);
                }
                //类容
                if (index >= 11) {
                    Map<String, String> map2 = new LinkedHashMap<>();
                    for (Map.Entry<Integer, String> entry : stringMap.entrySet()) {
                        map2.put(entry.getKey().toString(),entry.getValue());
                    }
                    //removeNullKey(map2);
                    listList.add(map2);
                }
                resultRes.put(key, listList);
            }
        }
        return resultRes;
    }

    /**
     * 判断是否是正整数
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            int chr = str.charAt(i);
            if (chr < 48 || chr > 57) {
                return false;
            }
        }
        return true;
    }

    /**
     * 去除map中的null
     * @param map
     */
    public static void removeNullKey(Map map){
        Set set = map.keySet();
        for (Iterator iterator = set.iterator(); iterator.hasNext();) {
            Object obj = (Object) iterator.next();
            remove(obj, iterator);
        }
    }
    /**
     * Iterator 是工作在一个独立的线程中，并且拥有一个 mutex 锁。
     * Iterator 被创建之后会建立一个指向原来对象的单链索引表，当原来的对象数量发生变化时，这个索引表的内容不会同步改变，
     * 所以当索引指针往后移动的时候就找不到要迭代的对象，所以按照 fail-fast 原则 Iterator 会马上抛出 java.util.ConcurrentModificationException 异常。
     * 所以 Iterator 在工作的时候是不允许被迭代的对象被改变的。
     * 但你可以使用 Iterator 本身的方法 remove() 来删除对象， Iterator.remove() 方法会在删除当前迭代对象的同时维护索引的一致性。
     * @param obj
     * @param iterator
     */
    private static void remove(Object obj, Iterator iterator){
        if(obj instanceof String){
            String str = (String)obj;
            if(str==null|| str.length()==0){
                iterator.remove();
            }
        }else if(obj instanceof Collection){
            Collection col = (Collection)obj;
            if(col==null||col.isEmpty()){
                iterator.remove();
            }

        }else if(obj instanceof Map){
            Map temp = (Map)obj;
            if(temp==null||temp.isEmpty()){
                iterator.remove();
            }

        }else if(obj instanceof Object[]){
            Object[] array =(Object[])obj;
            if(array==null||array.length<=0){
                iterator.remove();
            }
        }else{
            if(obj==null){
                iterator.remove();
            }
        }
    }

    /**
     * 判断是否包含大写的阿拉伯数字
     *
     * @param title
     * @return
     */
    public static boolean isSheetTile(String title) {
        List<String> filterTitle = new ArrayList<String>() {
            {
                this.add("一、");
                this.add("二、");
                this.add("三、");
                this.add("四、");
                this.add("五、");
                this.add("六、");
                this.add("七、");
                this.add("八、");
                this.add("九、");
                this.add("十、");
                this.add("1、");
                this.add("2、");
                this.add("3、");
                this.add("4、");
                this.add("5、");
                this.add("6、");
                this.add("7、");
                this.add("8、");
                this.add("9、");
                this.add("10、");
            }
        };
        for (String s : filterTitle) {
            if (title.contains(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * sheet过滤处理
     *
     * @param title
     * @return
     */
    public static String filterSheetTile(String title) {
        return title.substring(2, title.length());
    }
}