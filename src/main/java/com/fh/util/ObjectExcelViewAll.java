package com.fh.util;


import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.web.servlet.view.document.AbstractExcelView;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 导入到EXCEL
 * 类名称：ObjectExcelView.java
 * 类描述：
 * @author FH
 * 作者单位：
 * 联系方式：
 * @version 1.0
 */
public class ObjectExcelViewAll extends AbstractExcelView{

	@Override
	protected void buildExcelDocument(Map<String, Object> model,
									  HSSFWorkbook workbook, HttpServletRequest request,
									  HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		Date date = new Date();
		String filename = Tools.date2Str(date, "yyyyMMddHHmmss");
		HSSFSheet sheet;
		HSSFSheet sheet1;
		HSSFSheet sheet2;


		List<HSSFSheet> sheets = (List<HSSFSheet>) model.get("sheets");
		for(HSSFSheet hssfsheet:sheets){
			String a=hssfsheet.getSheetName();
			System.out.println(a);
			HSSFSheet sheetCreat = workbook.createSheet(hssfsheet.getSheetName());
			CopySheetUtil.copySheets(sheetCreat,hssfsheet);
		}


		HSSFCell cell;
		HSSFCell cell1;
		HSSFCell cell2;
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment;filename="+filename+".xls");
		sheet = workbook.createSheet("用户列表");
		sheet1=workbook.createSheet("单位列表");
		sheet2=workbook.createSheet("任务数据");

		List<String> titles = (List<String>) model.get("titles");
		int len = titles.size();
		HSSFCellStyle headerStyle = workbook.createCellStyle(); //标题样式
		headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		headerStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		HSSFFont headerFont = workbook.createFont();	//标题字体
		headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		headerFont.setFontHeightInPoints((short)11);
		headerStyle.setFont(headerFont);
		short width = 20,height=25*20;
		sheet.setDefaultColumnWidth(width);
		sheet1.setDefaultColumnWidth(width);
		for(int i=0; i<len; i++){ //设置标题
			String title = titles.get(i);
			cell = getCell(sheet, 0, i);
			cell.setCellStyle(headerStyle);
			setText(cell,title);
		}



		List<String> titles1 = (List<String>) model.get("titles1");
		int len1 = titles1.size();
		HSSFCellStyle headerStyle1 = workbook.createCellStyle(); //标题样式
		headerStyle1.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		headerStyle1.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		HSSFFont headerFont1 = workbook.createFont();	//标题字体
		headerFont1.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		headerFont1.setFontHeightInPoints((short)11);
		headerStyle1.setFont(headerFont);
		short width1 = 20,height1=25*20;
		sheet.setDefaultColumnWidth(width1);
		sheet1.setDefaultColumnWidth(width1);
		for(int i=0; i<len1; i++){ //设置标题
			String title = titles1.get(i);
			cell1 = getCell(sheet1, 0, i);
			cell1.setCellStyle(headerStyle1);
			setText(cell1,title);
		}



		List<String> titles2 = (List<String>) model.get("titles2");
		int len2 = titles2.size();
		HSSFCellStyle headerStyle2 = workbook.createCellStyle(); //标题样式
		headerStyle2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		headerStyle2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		HSSFFont headerFont2 = workbook.createFont();	//标题字体
		headerFont2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		headerFont2.setFontHeightInPoints((short)11);
		headerStyle2.setFont(headerFont);
		short width2 = 20,height2=25*20;
		sheet2.setDefaultColumnWidth(width2);
		sheet2.setDefaultColumnWidth(width2);
		for(int i=0; i<len2; i++){ //设置标题
			String title = titles2.get(i);
			cell2 = getCell(sheet2, 0, i);
			cell2.setCellStyle(headerStyle2);
			setText(cell2,title);
		}





		sheet.getRow(0).setHeight(height);
		sheet1.getRow(0).setHeight(height);
		sheet2.getRow(0).setHeight(height);
		HSSFCellStyle contentStyle = workbook.createCellStyle(); //内容样式
		contentStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		List<PageData> varList = (List<PageData>) model.get("varList");
		int varCount = varList.size();
		for(int i=0; i<varCount; i++){
			PageData vpd = varList.get(i);
			for(int j=0;j<len;j++){
				String varstr = vpd.getString("var"+(j+1)) != null ? vpd.getString("var"+(j+1)) : "";
				cell = getCell(sheet, i+1, j);
				cell.setCellStyle(contentStyle);
				setText(cell,varstr);
			}

		}

		List<PageData> varList1 = (List<PageData>) model.get("varList1");
		int varCount1 = varList1.size();
		for(int i=0; i<varCount1; i++){
			PageData vpd = varList1.get(i);
			for(int j=0;j<len1;j++){
				String varstr = vpd.getString("var"+(j+1)) != null ? vpd.getString("var"+(j+1)) : "";
				cell1 = getCell(sheet1, i+1, j);
				cell1.setCellStyle(contentStyle);
				setText(cell1,varstr);
			}

		}


		List<PageData> varList2 = (List<PageData>) model.get("varList2");
		int varCount2 = varList2.size();
		for(int i=0; i<varCount2; i++){
			PageData vpd = varList2.get(i);
			for(int j=0;j<len2;j++){
				String varstr = vpd.getString("var"+(j+1)) != null ? vpd.getString("var"+(j+1)) : "";
				cell2 = getCell(sheet2, i+1, j);
				cell2.setCellStyle(contentStyle);
				setText(cell2,varstr);
			}

		}




	}



}
