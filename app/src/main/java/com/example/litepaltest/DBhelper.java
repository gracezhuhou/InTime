package com.example.litepaltest;

import android.provider.ContactsContract;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by lenovo on 2018/1/23.
 */

public class DBhelper {
    static int calendarId = 0;
    static int inTimeId = 0;
    static int habitId = 0;

    public DBhelper(){
        LitePal.getDatabase();
    }

    //ToDoList

    //增加待办事项
    public ToDoList addToDoList(String name, int priority, Date alarm, Date deadline, String note, String category){
        //不可重名，判断是否有相同name的待办事项
        if (DataSupport.where("name = ?", name).find(ToDoList.class).size() != 0){
            return null;
        }

        ToDoList toDo = new ToDoList();
        toDo.setName(name);
        toDo.setAlarm(alarm);
        toDo.setCategory(category);
        toDo.setDeadline(deadline);
        toDo.setPriority(priority);
        toDo.setNote(note);
        toDo.setIsfinished(false);
        toDo.setIsRecording(false);
        toDo.save();
        //costTime由计算得，不由用户设定

        return toDo;
    }

    //更新（修改）待办事项
    //点入修改界面时，先使oldname=此时的名字，再跳转页面
    public boolean updateToDoList(String oldname, String name, int priority, Date alarm, Date deadline, String note, String category){
        //检测重名
        if (oldname.equals(name)){
            return false;
        }

        ToDoList toDo = findToDo(oldname);

        toDo.setId(toDo.getId());
        toDo.setName(name);
        toDo.setAlarm(alarm);
        toDo.setCategory(category);
        toDo.setDeadline(deadline);
        toDo.setPriority(priority);
        toDo.setNote(note);
        toDo.updateAll("name = ?", oldname);
        return true;
    }

    //删除待办事项
    public void deleteToDoList(String oldname){
        DataSupport.deleteAll(ToDoList.class, "name = ?", oldname);
    }

    //获取待办事项列表
    public List<ToDoList> gettodolist(){
        List<ToDoList> todolist = DataSupport.order("priority").order("deadline").order("isfinished").find(ToDoList.class);
        return todolist;
    }

    //查找待办事项
    public ToDoList findToDo(String name){
        List<ToDoList> todoList = DataSupport.where("name = ?", name).find(ToDoList.class);
        ToDoList toDo = todoList.get(0);

        return toDo;
    }

    //待办事项完成 id = -1
    public void toDoFinished(String name, boolean isFinished){
        ToDoList todo = findToDo(name);
        if (isFinished){
            todo.setIsfinished(true);
        }
        else{
            todo.setToDefault("isfinished");
        }
        todo.updateAll("name = ?", name);
    }

    //待办事项记录中
    public void toDoRecording(String name, boolean isRecording){
        ToDoList todo = findToDo(name);
        if (isRecording){
            todo.setIsRecording(true);
        }
        else{
            todo.setToDefault("isRecording");
        }
        todo.updateAll("name = ?", name);
    }

    /*
     *
    Calendar
    *
    */

    //增加日程
    //locaition可由定位获得
    public CalendarList addCalendarList(String name, String location, Date startTime, Date endTime, Date alarm, String cycle, String note, String category){
        //判断同一天是否有同名日程
        if (findCalendar(name, startTime) != null){
            return null;
        }

        //结束时间必须大于开始时间
        if (endTime.compareTo(startTime) != 1){
            return null;
        }

        //时间不能和别的日程重叠，判断时间是否重叠
        List<CalendarList> calendarLists = DataSupport.findAll(CalendarList.class);
        for (CalendarList calen: calendarLists){
            if (calen.getEndTime().compareTo(startTime) == 1 && calen.getStartTime().compareTo(endTime) == -1){
                return null;
            }
        }

        CalendarList calendar = new CalendarList();
        calendar.setId(++calendarId);
        calendar.setName(name);
        calendar.setLocation(location);
        calendar.setStartTime(startTime);
        calendar.setEndTime(endTime);
        calendar.setAlarm(alarm);
        calendar.setCycle(cycle);
        calendar.setNote(note);
        calendar.setCategory(category);
        calendar.save();

        return calendar;
    }

    //更新（修改）日程
    public boolean updateCalendarList(String oldname, String name, String location, Date startTime, Date endTime, Date alarm, String cycle, String note, String category){
        //判断同一天是否有同名日程
        if (findCalendar(name, startTime) != null){
            return false;
        }

        //结束时间必须大于开始时间
        if (endTime.compareTo(startTime) != 1){
            return false;
        }

        //判断时间是否有冲突
        List<CalendarList> calendarLists = DataSupport.findAll(CalendarList.class);
        for (CalendarList calen: calendarLists){
            if (calen.getEndTime().compareTo(startTime) == 1 && calen.getStartTime().compareTo(endTime) == -1){
                return false;
            }
        }

        CalendarList calendar = new CalendarList();
        calendar.setName(name);
        calendar.setLocation(location);
        calendar.setStartTime(startTime);
        calendar.setEndTime(endTime);
        calendar.setAlarm(alarm);
        calendar.setCycle(cycle);
        calendar.setNote(note);
        calendar.setCategory(category);
        calendar.updateAll("name = ?", oldname);
        return true;
    }

    //删除日程
    public void deleteCalendarList(String oldname){
        DataSupport.deleteAll(CalendarList.class, "name = ?", oldname);
    }

    //查找某日程(一天不能有相同日程)
    public CalendarList findCalendar(String name, Date startTime){
        List<CalendarList> calendarList = DataSupport.where("name = ?", name).find(CalendarList.class);

        Calendar c = Calendar.getInstance();
        c.setTime(startTime);
        c.add(Calendar.DAY_OF_MONTH, 1);// 今天+1天
        Date tomorrow = c.getTime();
        CalendarList calendar;

        for (CalendarList calen: calendarList){
            if (calen.getStartTime().compareTo(startTime) != -1 && calen.getStartTime().compareTo(tomorrow) == -1){
                calendar = calen;
                return calendar;
            }
        }

        return null;
    }

    //获取日程列表(当天日期之后的日程)
    public List<CalendarList> getcalendarlist(){
        Date today =  new Date(System.currentTimeMillis());
        List<CalendarList> calendarList = DataSupport
                .order("startTime")
                .find(CalendarList.class);

        for (int i = 0; i < calendarList.size(); i++) {
            if (calendarList.get(i).getEndTime().compareTo(today) == -1) {
                calendarList.remove(i);
                i--;
            }
            else break;
        }
        return calendarList;
    }

    //获取某年某月某日的日程
    public List<CalendarList> getcalendarDaylist(Date today, Date tommorrow){
        List<CalendarList> calendarDayList = DataSupport
                .findAll(CalendarList.class);

        for (int i = 0; i < calendarDayList.size(); i++) {
            if (calendarDayList.get(i).getEndTime().compareTo(today) == -1 || calendarDayList.get(i).getStartTime().compareTo(tommorrow) == 1) {
                calendarDayList.remove(i);
                i--;
            }
        }

        return calendarDayList;
    }

    //将日程同步至时间轴（在通过定位确认之后）


    /*
    *
    Habit
    *
    */


    //增加习惯
    public HabitList addHabit(String name){
        //不可重名，判断是否有相同name
        if (DataSupport.where("name = ?", name).find(HabitList.class).size() != 0){
            return null;
        }

        HabitList habit = new HabitList();
        habit.setId(++habitId);
        habit.setName(name);
        habit.setToday(false);
        habit.setDays(0);
        habit.save();

        return habit;
    }

    //更新（修改）习惯
    public boolean updateHabit(String oldname, String name){
        //检测重名
        if (oldname.equals(name)){
            return false;
        }

        HabitList habit = new HabitList();
        habit.setName(name);
        habit.updateAll("name = ?", oldname);
        return true;
    }

    //删除习惯
    public void deleteHabit(String oldname){
        DataSupport.deleteAll(HabitList.class, "name = ?", oldname);
    }

    //获取习惯列表
    public List<HabitList> getHabitList(){
        List<HabitList> habitList = DataSupport.findAll(HabitList.class);
        return habitList;
    }

    //习惯完成天数计数，返回天数
    public int addDay(String name, boolean flag){
        HabitList habit = findHabit(name);
        int day;

        if (flag){
            day = habit.getDays() + 1;
            habit.setToday(true);
            habit.setDays(day);
        }
        else {
            day = habit.getDays() - 1;
            habit.setToDefault("today");
            habit.setToDefault("days");
        }

        habit.updateAll("name = ?", name);
        return day;
    }

    public void clearhabitday(){
        List<HabitList> habitList = getHabitList();

        for (HabitList habit: habitList){
            HabitList newhabit = habit;
            newhabit.setToDefault("today");
            newhabit.update(habit.getId());
        }
    }

    //查找习惯
    public HabitList findHabit(String name){
        List<HabitList> habitlist = DataSupport.where("name = ?", name).find(HabitList.class);
        HabitList habit = habitlist.get(0);

        return habit;
    }

    /*
    *
    Time
    *
    */

    //增加时间记录
    public InTimeList addTime(String name, String note, String category, Date startTime, Date endTime, double Lng, double Lat){
        //不可重名，判断是否有相同name
        //if (DataSupport.where("name = ?", name).find(InTimeList.class).size() != 0){
        //    return null;
        //}

        ///结束时间必须大于开始时间
        if (endTime.compareTo(startTime) != 1){
            return null;
        }

        //时间不能和别的时间记录重叠，判断是否重叠
        List<InTimeList> inTimeLists = DataSupport.findAll(InTimeList.class);
        for (InTimeList times: inTimeLists){
            if (times.getEndTime().compareTo(startTime) == 1 && times.getStartTime().compareTo(endTime) == -1){
                return null;
            }
        }

        InTimeList time = new InTimeList();
        //time.setId(++inTimeId);
        time.setName(name);
        time.setNote(note);
        time.setCategory(category);
        time.setStartTime(startTime);
        time.setEndTime(endTime);
        time.setLng(Lng);
        time.setLat(Lat);
        time.save();

        return time;
    }

    //更新（修改）时间记录
    public boolean updateTime(String id, String name, String note, String category, Date startTime, Date endTime) {
        ///结束时间必须大于开始时间
        if (endTime.compareTo(startTime) != 1){
            return false;
        }
        else {
            //时间不能和别的时间记录重叠，判断是否重叠
            List<InTimeList> inTimeLists = DataSupport.findAll(InTimeList.class);
            for (InTimeList times : inTimeLists) {
                if (times.getId() == Integer.parseInt(id)){
                    continue;
                }
                if (times.getEndTime().compareTo(startTime) == 1 && times.getStartTime().compareTo(endTime) == -1) {
                    return false;
                }
            }

            InTimeList time = findTimeById(id);
            time.setName(name);
            time.setNote(note);
            time.setCategory(category);
            time.setStartTime(startTime);
            time.setEndTime(endTime);
            time.updateAll("id = ?", String.valueOf(time.getId()));

            return true;
        }
    }

    //删除时间记录
    public void deleteTime(Date startTime){
        //DataSupport.deleteAll(InTimeList.class, "startTime = ?", startTime);
        List<InTimeList> timeList = DataSupport.findAll(InTimeList.class);
        for (InTimeList time: timeList){
            if(time.getStartTime().compareTo(startTime) == 0){
                DataSupport.delete(InTimeList.class, time.getId());
            }
        }
    }

    //获取时间记录列表
    public List<InTimeList> getTimeList(){
        List<InTimeList> timeList = DataSupport.order("startTime").find(InTimeList.class);
        return timeList;
    }

    //查找时间记录
    public InTimeList findTime(Date startTime){
        //List<InTimeList> timeList = DataSupport.where("name = ?", name).find(InTimeList.class);
        //InTimeList time = timeList.get(0);
        List<InTimeList> timeList = DataSupport.findAll(InTimeList.class);
        for (InTimeList time: timeList){
            if(time.getStartTime().compareTo(startTime) == 0){
                return time;
            }
        }
        return null;
    }

    public InTimeList findTimeById(String id){
        return DataSupport.find(InTimeList.class, Integer.parseInt(id));
    }

    public boolean isTimeConflict(Date timeNow){
        List<InTimeList> timeList = DataSupport.order("endTime desc").find(InTimeList.class);
        Date startTime;
        Date endTime;
        for (InTimeList time: timeList){
            startTime = time.getStartTime();
            endTime = time.getEndTime();
            if (timeNow.compareTo(endTime) != -1){
                return false;
            }
            else if (timeNow.compareTo(startTime) == 1){
                return true;
            }
        }
        return false;
    }

    //不同category时间统计
    public int getCategoryTimeSum(String category){
        List<InTimeList> timeList = DataSupport.where("category = ?", category)
                .select("startTime", "endTime")
                .find(InTimeList.class);
        long sum = 0;
        for (InTimeList time: timeList){
            sum += time.getEndTime().getTime() - time.getStartTime().getTime();
        }

        return (int)(sum*0.0000167);    //返回总毫秒
    }

    //统计总时间
    public long getAllTimeSum(){
        List<InTimeList> timeList = DataSupport.select("startTime", "endTime").find(InTimeList.class);
        long sum = 0;
        for (InTimeList time: timeList){
            sum += time.getEndTime().getTime() - time.getStartTime().getTime();
        }

        return sum;
    }
    //给定时间范围内，统计某category的总时间
    public int getCategoryPeriodTimeSum(String category, Date start, Date end){
        List<InTimeList> timeList = DataSupport.where("category = ?", category)
                .select("startTime", "endTime")
                .find(InTimeList.class);
        long sum = 0;

        for (InTimeList time: timeList){
            Date startTime = time.getStartTime();
            Date endTime = time.getEndTime();

            if (startTime.compareTo(end) == -1 && endTime.compareTo(start) == 1) {
                if (endTime.compareTo(end) == 1) {
                    sum += end.getTime() - startTime.getTime();
                }
                else if (startTime.compareTo(start) == -1){
                    sum += endTime.getTime() - start.getTime();
                }
                else{
                    sum += time.getEndTime().getTime() - time.getStartTime().getTime();
                }
            }
        }
        return (int)(sum*0.0000167);     //返回总毫秒转化为分钟
    }

    /*
    *
    *数据预测
    *
    **/

    //相距最短的点, 选取
    public InTimeList getByLocation(double Lng, double Lat){
        List<InTimeList> timeList = getTimeList();
        List<InTimeList> possibleTimeList = new ArrayList<>();
        List<Integer> number = new ArrayList<>();//相同
        int i;

        for (InTimeList time: timeList){
            double distance = getDistance(time.getLng(), time.getLat(), Lng, Lat);
            if (distance * 1000 < 50){ //距离小于50m
                boolean flag = false;
                for (i = 0; i < possibleTimeList.size(); ++i){  //之前是否有相同名字的50m距离内时间记录
                    if(possibleTimeList.get(i).getName().equals(time.getName())){
                        number.set(i, number.get(i) + 1);
                        flag = true;
                        break;
                    }
                }
                if(!flag) {
                    number.add(1);
                    possibleTimeList.add(time);
                }
            }
        }

        if (number.size()  == 0){
            return null;
        }

        int max = number.get(0);    //最多次数的时间记录
        for (i = 1; i < number.size(); ++i){
            if (max < number.get(i)){
                max = number.get(i);
                break;
            }
        }

        if (max < 3){   //次数少于3视为无效
            return null;
        }

        if (number.size() == 1) {
            return possibleTimeList.get(0);
        }

        return possibleTimeList.get(i);

    }

    //清空
    public void deleteAll(){
        DataSupport.deleteAll(InTimeList.class);
        DataSupport.deleteAll(HabitList.class);
        DataSupport.deleteAll(CalendarList.class);
        DataSupport.deleteAll(ToDoList.class);
    }


    //辅助计算
    private static final double EARTH_RADIUS = 6378.137;

    private double rad(double distance){
        return distance * Math.PI / 180.0;
    }

    private double getDistance(double long1, double lat1, double long2, double lat2) {
        double a, b, d, sa2, sb2;
        lat1 = rad(lat1);
        lat2 = rad(lat2);
        a = lat1 - lat2;
        b = rad(long1 - long2);
        sa2 = Math.sin(a / 2.0);
        sb2 = Math.sin(b / 2.0);String.valueOf(
        d = 2 * EARTH_RADIUS * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1) * Math.cos(lat2) * sb2 * sb2)));
        return d;
    }
}