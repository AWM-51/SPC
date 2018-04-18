package com.wj.util;

import com.wj.domain.D_Group;
import com.wj.domain.SampleData;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class ReadExcel {
    /*
    读取.xls表格
    读取 抽检时间，样本1，2，3，4，5
    录入时间自动生成 格式如 2017-01-02
    如果某行有空值，则该列录制结束（可优化成空值设为0）
    * */
    public List<List<SampleData>> readXls(MultipartFile file,Date entryTime) throws IOException, ParseException,NullPointerException {
        InputStream is = file.getInputStream();
        XSSFWorkbook XSSFWorkbook = new XSSFWorkbook(is);
        SampleData SampleData = null;
        D_Group d_group=null;
        List<List<SampleData>> list = new ArrayList<List<com.wj.domain.SampleData>>();
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < XSSFWorkbook.getNumberOfSheets(); numSheet++) {
            XSSFSheet XSSFSheet = XSSFWorkbook.getSheetAt(numSheet);
           // d_group=new D_Group();//数据组定义
            if (XSSFSheet == null) {
                continue;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= XSSFSheet.getLastRowNum(); rowNum++) {
                XSSFRow XSSFRow = XSSFSheet.getRow(rowNum);
                List<SampleData> g_list = new ArrayList<SampleData>();//每一行（组）数据放到一个列表中
                if (XSSFRow != null) {

                   // try {

                        int i=2;
                        while(true){
                            SampleData = new SampleData();
                            XSSFCell D_dataCell = XSSFRow.getCell(1);
                            SampleData.setObtain_time(daysTodate(getValue(D_dataCell).toString()));//获取抽检时间

                            if (XSSFRow.getCell(i)==null){
                                break;
                            }
                            XSSFCell dataCell = XSSFRow.getCell(i);
                            SampleData.setValue(Double.valueOf(getValue(dataCell)));
                            g_list.add(SampleData);
                            ++i;
                        }

//                    }catch (NullPointerException ex){
//                        System.out.println(ex);
//                        continue;
//                    }
                }
                list.add(g_list);
            }


        }
        return list;
    }

    @SuppressWarnings("static-access")
    private String getValue(XSSFCell XSSFCell) {
//        if (XSSFCell == null) {
//            return null;
//        }
        if (XSSFCell.getCellType() == XSSFCell.CELL_TYPE_BOOLEAN) {
            // 返回布尔类型的值
            return String.valueOf(XSSFCell.getBooleanCellValue());
        } else if (XSSFCell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
            // 返回数值类型的值
            return String.valueOf(XSSFCell.getNumericCellValue());
        }
        else {
            // 返回字符串类型的值
            return String.valueOf(XSSFCell.getStringCellValue());
        }
    }


    public static String daysTodate(String days) throws ParseException {
        long dayMount = Integer.valueOf(days.substring(0,5));
        long addMill = (dayMount-25569)*24*3600;
        System.out.println("s:"+addMill);
        return stampToDate(addMill);

    }
    public static String stampToDate(long lt) throws ParseException {
        SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
        Long time=new Long(lt*1000);
        String d = format.format(time);
        Date date=format.parse(d);
        System.out.println("Format To String(Date):"+d);
        System.out.println("Format To Date:"+date);
        return d;
    }


}
