package com.fh.util;


import org.apache.poi.hssf.usermodel.*;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 导入到EXCEL
 * 类名称：ObjectExcelView.java
 * 类描述：
 * @author FH
 * 作者单位：
 * 联系方式：
 * @version 1.0
 */
public class ObjectExcelViewAllCheck extends AbstractExcelView{

	@Override
	protected void buildExcelDocument(Map<String, Object> model,
									  HSSFWorkbook workbook, HttpServletRequest request,
									  HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		Date date = new Date();
		String filename = Tools.date2Str(date, "yyyyMMddHHmmss");

		HSSFSheet sheet2;

		HSSFCell cell2;
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment;filename="+filename+".xls");
		sheet2=workbook.createSheet("任务数据");


		List<String> titles2 = (List<String>) model.get("titles2");
		int len2 = titles2.size();
		HSSFCellStyle headerStyle2 = workbook.createCellStyle(); //标题样式
		HSSFFont headerFont = workbook.createFont();	//标题字体
		headerStyle2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		headerStyle2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		HSSFFont headerFont2 = workbook.createFont();	//标题字体
		headerFont2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		headerFont2.setFontHeightInPoints((short)11);
		headerStyle2.setFont(headerFont);
		short width2 = 20,height=25*20;
		sheet2.setDefaultColumnWidth(width2);
		sheet2.setDefaultColumnWidth(width2);
		for(int i=0; i<len2; i++){ //设置标题
			String title = titles2.get(i);
			cell2 = getCell(sheet2, 0, i);
			cell2.setCellStyle(headerStyle2);
			setText(cell2,title);
		}


		sheet2.getRow(0).setHeight(height);
		HSSFCellStyle contentStyle = workbook.createCellStyle(); //内容样式
		contentStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
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
