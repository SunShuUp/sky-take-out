package com.sky.service.impl;


import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkSpaceService;
import com.sky.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    WorkSpaceService workSpaceService;

    /**
     * 指定时间区间的营业额
     * @param beginTime
     * @param endTime
     * @return
     */
    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate beginTime, LocalDate endTime) {

        //定义集合放begin到end每日的日期
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate current = beginTime;
        while (!current.isAfter(endTime)) {
            dateList.add(current);
            current = current.plusDays(1);
        }
        String dateString=StringUtils.join(dateList,",");


        Integer status= Orders.COMPLETED;

        List<Double> turnoverList=new ArrayList<>();

        for(LocalDate date:dateList){
           LocalDateTime begin= LocalDateTime.of(date, LocalTime.MIN);
           LocalDateTime end = LocalDateTime.of(date,LocalTime.MAX);
           Map<String,Object> map=new HashMap<>();
           map.put("begin",begin);
           map.put("end",end);
           map.put("status",status);
           Double turnover= orderMapper.sumByMap(map);
           turnoverList.add(turnover == null ? 0.0 : turnover);
        }
        String turnoverString=StringUtils.join(turnoverList,",");
        return TurnoverReportVO.builder().dateList(dateString).turnoverList(turnoverString).build();
    }

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        //日期，以逗号分隔，例如：2022-10-01,2022-10-02,2022-10-03
        List<LocalDate> dates = new ArrayList<>();
        LocalDate current = begin;
        while (!current.isAfter(end)) {
            dates.add(current);
            current = current.plusDays(1);
        }
        String dateList=StringUtils.join(dates,",");

        //新增用户，以逗号分隔，例如：20,21,10  select cout(*) from user where create_time <and crete_time>
        List<Integer> newUsers=new ArrayList<>();
        //用户总量，以逗号分隔，例如：200,210,220 slect count(*) from user where create_time
        List<Integer> totalUsers=new ArrayList<>();

        for(LocalDate date:dates){
            LocalDateTime beginTime= LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date.plusDays(1), LocalTime.MIN);
            Map<String,Object> map=new HashMap<>();
            map.put("end",endTime);
            Integer  totalUser=userMapper.countByMap(map);
            map.put("begin",beginTime);
            Integer  newUser=userMapper.countByMap(map);
            newUsers.add(newUser == null ? 0 : newUser);
            totalUsers.add(totalUser == null ? 0 : totalUser);

        }
        //用户总量，以逗号分隔，例如：200,210,220
        String totalUserList=StringUtils.join(totalUsers,",");
        //新增用户，以逗号分隔，例如：20,21,10
        String newUserList=StringUtils.join(newUsers,",");

        return UserReportVO.builder().dateList(dateList).totalUserList(totalUserList).newUserList(newUserList).build();
    }

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO OrdersStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate current = begin;
        while (!current.isAfter(end)){
            dates.add(current);
            current = current.plusDays(1);
        }
        //日期，以逗号分隔，例如：2022-10-01,2022-10-02,2022-10-03
        String dateList=StringUtils.join(dates,",");

        // 2. 准备存储每日数据的列表
        List<Integer> orderCountList = new ArrayList<>();      // 每日订单总数
        List<Integer> validOrderCountList = new ArrayList<>(); // 每日有效订单数

        for(LocalDate date:dates){
            LocalDateTime beginTime= LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date.plusDays(1), LocalTime.MIN);
            Integer orderCount=orderMapper.getOrderCount(beginTime,endTime,null);
            Integer validOrderCount=orderMapper.getOrderCount(beginTime,endTime,Orders.COMPLETED);
            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
        }
        Integer totalOrderCount = orderCountList.stream().reduce(0, Integer::sum);
        Integer validOrderCount = validOrderCountList.stream().reduce(0, Integer::sum);
        Double orderCompletionRate=0.0;
        if (totalOrderCount != 0) {
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }
        return OrderReportVO.builder().dateList(dateList)
                .orderCompletionRate(orderCompletionRate)
                .orderCountList(StringUtils.join(orderCountList,","))
                .validOrderCount(validOrderCount)
                .totalOrderCount(totalOrderCount)
                .validOrderCountList(StringUtils.join(validOrderCountList,",")).build();
    }

    @Override
    public SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end,Integer status) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end.plusDays(1), LocalTime.MIN);
        List<GoodsSalesDTO> goodsSalesDTOS=orderMapper.getSalesTop10(beginTime,endTime,status);
        List<String> nameList=new ArrayList<>();
        List<Integer> numberList=new ArrayList<>();
        for(GoodsSalesDTO goodsSalesDTO:goodsSalesDTOS){
            nameList.add(goodsSalesDTO.getName());
            numberList.add(goodsSalesDTO.getNumber());
        }
        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList,","))
                .numberList(StringUtils.join(numberList,",")).build();
    }

    @Override
    public void exportBusinessData(HttpServletResponse response) {
        //1.查询数据
        //1.1 计算时间范围：最近30天 。从三十天到昨天
        LocalDate begin=LocalDate.now().minusDays(30);
        LocalDate end=LocalDate.now().minusDays(1);
        BusinessDataVO businessDataVO=workSpaceService.businessData(
                LocalDateTime.of(begin,LocalTime.MIN),
                LocalDateTime.of(end,LocalTime.MAX)
        );

        //2.读取excel模板文件
        //2.1基于模板创建一个新的工作簿
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx")) {
            if (inputStream == null) {
                throw new RuntimeException("Excel模板文件不存在");
            }

            XSSFWorkbook excel=new XSSFWorkbook(inputStream);
            XSSFSheet sheet=excel.getSheetAt(0);
            //3.填充数据 到指定的单元格
            sheet.getRow(1).getCell(1).setCellValue(begin+"至"+end);
            sheet.getRow(3).getCell(2).setCellValue(businessDataVO.getTurnover());
            sheet.getRow(3).getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            sheet.getRow(3).getCell(6).setCellValue(businessDataVO.getNewUsers());
            sheet.getRow(4).getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            sheet.getRow(4).getCell(4).setCellValue(businessDataVO.getUnitPrice());
            for(int i=0;i<30;i++){
                LocalDate date=begin.plusDays(i);
                LocalDateTime beginTime=LocalDateTime.of(date, LocalTime.MIN);
                LocalDateTime endTime=LocalDateTime.of(date.plusDays(1), LocalTime.MIN);
                BusinessDataVO businessData= workSpaceService.businessData(beginTime,endTime);
                sheet.getRow(7+i).getCell(1).setCellValue(date.toString());
                sheet.getRow(7+i).getCell(2).setCellValue(businessData.getTurnover());
                sheet.getRow(7+i).getCell(3).setCellValue(businessData.getValidOrderCount());
                sheet.getRow(7+i).getCell(4).setCellValue(businessData.getOrderCompletionRate());
                sheet.getRow(7+i).getCell(5).setCellValue(businessData.getUnitPrice());
                sheet.getRow(7+i).getCell(6).setCellValue(businessData.getNewUsers());
            }
            //4.通过HttpServletResponses输出文件
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String fileName = URLEncoder.encode("运营数据报表.xlsx", StandardCharsets.UTF_8.name()).replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName);
            OutputStream outputStream = response.getOutputStream();
            excel.write(outputStream);
            outputStream.flush();
            excel.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //5.关闭

    }
}
