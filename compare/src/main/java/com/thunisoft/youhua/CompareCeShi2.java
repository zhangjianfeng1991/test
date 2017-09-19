package com.thunisoft.youhua;

/**
 * 比对数据库与excel人员信息
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.format.CellFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompareCeShi2 {

	private static ArrayList<String> filelist = new ArrayList<String>();
	private final static Logger logger=LoggerFactory.getLogger(CompareCeShi2.class);

	// 主方法入口     
	public static void main(String[] args) {

		String filePath = "F:\\全国法院统一用户管理系统维护人员信息表201709";

		getFiles(filePath);
		logger.info("");
	}

	public static void getResult() {

	}

	static void getFiles(String filePath) {
		File root = new File(filePath);
		File[] files = root.listFiles();
		List<DbBean> dbList = DbDao.getData();
		Map<String, DbBean> dbMap = new HashMap<String, DbBean>();
		for (DbBean bean : dbList) {
			String key = bean.getCorp() + bean.getDept() + bean.getUser();
			dbMap.put(key, bean);
		}
		int notNullCount = 0;
		int nullCount = 0;
		int iid = 5000;

		for (File file : files) {
			try {
				// list-->set-->list
				List<DbBean> excelListold = readExcel(file);
				if (excelListold == null) {
					continue;
				}

				Set<DbBean> set = new LinkedHashSet<DbBean>();
				set.addAll(excelListold);
				List<DbBean> excelList = new ArrayList<DbBean>();
				excelList.addAll(set);

				for (DbBean excelBean : excelList) {
					String key = excelBean.getCorp() + excelBean.getDept() + excelBean.getUser();
					if (dbMap.containsKey(key)) {
						iid++;

						String sql = "INSERT INTO \"db_uim\".\"t_ywgy_qx_ryjs\""
								+ " (\"c_id\",\"c_ryid\",\"c_jsid\",\"c_sysid\",\"dt_updatetime\")" + "VALUES" + "('"
								+ iid + "','" + dbMap.get(key).getId() + "','5','thunisoft-uim',now());";
						dbMap.get(key).setSql(sql);
						logger.info(excelBean.getProvince().replaceAll(" ", "") + "\t" + excelBean.getCorp()
								+ "\t" + excelBean.getDept() + "\t" + excelBean.getUser() + "\t"
								+ dbMap.get(key).getId() + "\t" + dbMap.get(key).getSql());
						notNullCount++;
					} else {
						logger.info(excelBean.getProvince().replaceAll(" ", "") + "\t"
								+ excelBean.getCorp().replaceAll(" ", "") + "\t"
								+ excelBean.getDept().replaceAll(" ", "") + "\t"
								+ excelBean.getUser().replaceAll(" ", ""));
						nullCount++;
					}

				}
				System.out.println("记录数" + excelListold.size());
				System.out.println("有记录的" + notNullCount);
				System.out.println("无记录的" + nullCount);
			} catch (Exception e) {
				e.printStackTrace();

			}

		}

	}

	public static List<DbBean> readExcel(File file) throws Exception {
		// 获取文件名字
		String fileName = file.getName();
		// 获取文件类型
		String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
		System.out.println(" **** fileType:" + fileType);
		// 获取输入流
		InputStream stream = new FileInputStream(file);
		// 获取工作薄
		Workbook xssfWorkbook = null;
		if (fileType.equals("xls")) {
			xssfWorkbook = new HSSFWorkbook(stream);
		} else if (fileType.equals("xlsx")) {
			xssfWorkbook = new XSSFWorkbook(stream);
		} else {
			System.out.println("您输入的excel格式不正确");
			return null;
		}
		DbBean a = null;
		// System.out.println("888");
		List<DbBean> aList = new ArrayList<DbBean>();
		// Read the Sheet
		Sheet Sheet = xssfWorkbook.getSheetAt(0);
		// Read the Row 从0开始
		for (int rowNum = 1; rowNum <= Sheet.getLastRowNum(); rowNum++) {
			Row Row = Sheet.getRow(rowNum);
			if (Row != null) {
				a = new DbBean();
				a.setCorp(getValue(Row.getCell(1)).replaceAll(" ", ""));
				a.setDept(getValue(Row.getCell(2)).replaceAll(" ", ""));
				a.setUser(getValue(Row.getCell(3)).replaceAll(" ", ""));
				a.setProvince(getValue(Row.getCell(4)).replaceAll(" ", ""));
				aList.add(a);
			}
		}
		return aList;
	}

	private static String getValue(Cell cell) {
		int type = CellFormat.ultimateType(cell);
		if (type == Cell.CELL_TYPE_BOOLEAN) {
			return String.valueOf(cell.getBooleanCellValue());
		} else if (type == Cell.CELL_TYPE_NUMERIC) {
			return String.valueOf(cell.getNumericCellValue());
		} else if (type == Cell.CELL_TYPE_BLANK) {
			return "";
		} else {
			return cell.getStringCellValue().trim();
		}

	}

}