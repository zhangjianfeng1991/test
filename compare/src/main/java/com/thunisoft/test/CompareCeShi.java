package com.thunisoft.test;

/**
 * 比对数据库与excel人员信息
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.print.DocFlavor.STRING;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.format.CellFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CompareCeShi {

	private static ArrayList<String> filelist = new ArrayList<String>();

	// 主方法入口
	public static void main(String[] args) throws Exception {

		String filePath = "F:\\全国法院统一用户管理系统维护人员信息表201709";

		getFiles(filePath);
	}

	static void getFiles(String filePath) throws Exception {
		File root = new File(filePath);
		File[] files = root.listFiles();
		List<DbBean> dbList = Shujuku.getInfo();
		Map<String, DbBean> dbMap = new HashMap<String, DbBean>();
		// int iid=3000;
		for (DbBean bean : dbList) {
			String key = bean.getCorp() + bean.getDept() + bean.getUser();
			dbMap.put(key, bean);
		}
		int notNullCount = 0;
		int nullCount = 0;
		int iid = 5000;
		Map<String, ExcelBean> excelMap = new TreeMap<String, ExcelBean>();
		ShenFenComparator bvc = new ShenFenComparator(excelMap);
		TreeMap<String, ExcelBean> sorted_map = new TreeMap<String, ExcelBean>(bvc);
		for (File file : files) {
			List<ExcelBean> excelList = readExcel(file);
			for (ExcelBean exBean : excelList) {
				String key = exBean.getFayuan().replaceAll(" ", "") + exBean.getBumen().replaceAll(" ", "") + exBean.getXinmin().replaceAll(" ", "");
				excelMap.put(key, exBean);
			}
		}
		sorted_map.putAll(excelMap);
		List<String> list1 = new ArrayList<String>(sorted_map.keySet());
		for (String key : list1) {
			if (dbMap.containsKey(key)) {
				iid++;
				DbBean dbBean = dbMap.get(key);
				String sql = "INSERT INTO \"db_uim\".\"t_ywgy_qx_ryjs\""
						+ " (\"c_id\",\"c_ryid\",\"c_jsid\",\"c_sysid\",\"dt_updatetime\")" + "VALUES" + "('" + iid
						+ "','" + dbBean.getId() + "','5','thunisoft-uim',now());";
				dbBean.setSql(sql);
				System.out.println(excelMap.get(key).getShenfen().replaceAll(" ", "") + "\t" + dbBean.getCorp() +"\t"+ dbBean.getDept()
						+ "\t"+ dbBean.getUser() + "\t" + dbBean.getId() + "\t" + dbBean.getSql());
				notNullCount++;
			} else {
				System.out.println(excelMap.get(key).getShenfen().replaceAll(" ", "") + "\t" + excelMap.get(key).getFayuan().replaceAll(" ", "") + "\t"
						+ excelMap.get(key).getBumen().replaceAll(" ", "") + "\t" + excelMap.get(key).getXinmin().replaceAll(" ", "") );
				nullCount++;
			}
		}
		System.out.println("记录数" + list1.size());
		System.out.println("有记录的" + notNullCount);
		System.out.println("无记录的" + nullCount);
	}

	public static List<ExcelBean> readExcel(File file) throws Exception {
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
		}
		ExcelBean a = null;
		// System.out.println("888");
		List<ExcelBean> aList = new ArrayList<ExcelBean>();
		// Read the Sheet
		Sheet Sheet = xssfWorkbook.getSheetAt(0);
		// Read the Row 从0开始
		for (int rowNum = 1; rowNum <= Sheet.getLastRowNum(); rowNum++) {
			Row Row = Sheet.getRow(rowNum);
			if (Row != null) {
				// 判断这行记录是否存在
				/*
				 * if (Row.getLastCellNum() < 1 ||
				 * "".equals(getValue(Row.getCell(1)))) { continue; }
				 */
				// 获取每一行
				a = new ExcelBean();
				a.setFayuan(getValue(Row.getCell(1)));
				a.setBumen(getValue(Row.getCell(2)));
				a.setXinmin(getValue(Row.getCell(3)));
				a.setShenfen(getValue(Row.getCell(4)));
				aList.add(a);
				// System.out.println("66");
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

	static class ShenFenComparator implements Comparator<String> {

		Map<String, ExcelBean> base;

		public ShenFenComparator(Map<String, ExcelBean> base) {
			this.base = base;
		}

		// Note: this comparator imposes orderings that are inconsistent with
		// equals.
		public int compare(String a, String b) {
			if (base.get(a).getShenfen().compareTo(base.get(b).getShenfen()) > 0) {
				return -1;
			} else {
				return 1;
			}
		}
	}
}