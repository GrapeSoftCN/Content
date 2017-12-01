package Model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


import database.DBHelper;
import nlogger.nlogger;
import string.StringHelper;
import time.TimeHelper;

public class ContentInfo {
    private DBHelper db = new DBHelper("mongodb", "cObject_17");

    /**
     * 任务信息
     * 
     * @project SYJJContent
     * @package interfaceApplication
     * @file Content.java
     * 
     * @param searchK
     * @param pageIndex
     * @param pageSize
     * @param startDate
     * @param endDate
     * @return
     *
     */
    @SuppressWarnings("unchecked")
    public String getTask(String searchK, String pageIndex, String pageSize, String startDate, String endDate) throws Exception {
        long start = 0, end = 0, publishData = 0;
        String publishTime = "";
        JSONArray array = null;
        JSONObject object = new JSONObject(), temp;
        int idx = Integer.parseInt(pageIndex);
        int Size = Integer.parseInt(pageSize);
        if (idx > 0 && Size > 0) {
            if (startDate.contains("-")) {
                if (startDate.length() > 10) {
                    start = TimeHelper.dateToStamp(startDate);
                } else {
                    start = DataConvert(startDate);
                }
            } else {
                start = Long.parseLong(startDate);
            }
            if (endDate.contains("-")) {
                if (endDate.length() > 10) {
                    end = TimeHelper.dateToStamp(endDate);
                } else {
                    end = DataConvert(endDate);
                }
            } else {
                end = Long.parseLong(endDate);
            }
            long total = 0;
            String oid = "";
            try {
                db.eq("searchKey", searchK);
                db.gt("publishData", start);
                db.lt("publishData", end);
                array = db.dirty().field("_id,siteName,title,publishData,code").page(idx, Size);
                total = db.count();
                if (array != null && array.size() > 0) {
                    int l = array.size();
                    for (int i = 0; i < l; i++) {
                        temp = (JSONObject) array.get(i);
                        if (temp.containsKey("publishData")) {
                            publishData = temp.getLong("publishData");
                        }
                        publishTime = TimeHelper.stampToDate(publishData);
                        temp.put("id", temp.getMongoID("_id"));
                        temp.put("publishData", StringHelper.InvaildString(publishTime) ? publishTime.split(" ")[0] : "");
                        oid = temp.getMongoID("_id");
                        temp.put("count", String.valueOf(total));
                        temp.put("code", "/SYJJContent/getContent?id=" + oid + "&pageIndex=0");
                        temp.remove("_id");
                        array.set(i, temp);
                    }
                }
            } catch (Exception e) {
                nlogger.logout(e);
                array = null;
            }
        }
        object.put("content", (array != null && array.size() > 0) ? array : new JSONArray());
        return object.toJSONString().replace("\\", "");
    }

    /**
     * 时间转时间戳
     * 
     * @project GrapeInfoCollection
     * @package interfaceApplication
     * @file CollectInfo.java
     * 
     * @param value
     * @param dataType
     * @return
     *
     */
    private long DataConvert(String value) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long ts = 0;
        try {
            Date date = simpleDateFormat.parse(value);
            ts = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ts;
    }

    /**
     * 获取内容信息
     * 
     * @project SYJJContent
     * @package interfaceApplication
     * @file Content.java
     * 
     * @param _id
     *            文章唯一标识符
     * @param idx
     *            当前页
     * @return
     *
     */
    public String getContentInfo(String _id, int idx) {
        String title = "";
        String siteName = "";
        String author = "";
        String publishData = "";
        String contents = "";
        JSONObject object = db.eq("_id", _id).find();
        if (object != null && object.size() > 0) {
            if (object != null && object.size() > 0) {
                if (object.containsKey("siteName")) {
                    siteName = object.getString("siteName");
                }
                if (object.containsKey("title")) {
                    title = object.getString("title");
                }
                if (object.containsKey("author")) {
                    author = object.getString("author");
                }
                if (object.containsKey("publishData")) {
                    publishData = object.getString("publishData");
                    long time = Long.parseLong(publishData);
                    publishData = TimeHelper.stampToDate(time);
                }
                if (object.containsKey("content")) {
                    contents = (String) object.escapeHtmlGet("content");
                    contents = attamUrl(contents);
                }
            }
        }
        return getHtml(siteName, title, author, publishData, contents, idx);
    }

    /**
     * 文件地址拼接
     * 
     * @project SYJJContent
     * @package interfaceApplication
     * @file Content.java
     * 
     * @param contents
     * @return
     *
     */
    private String attamUrl(String contents) {
        String tmphref, tmpoldsrc, realHref, regtemp1, timer;
        List<String> reglist = match(contents, "a", "href", 0);
        String dir = "http://syj.tl.gov.cn/2205/2212/shijcjxx/";
        for (int x = 0; x < reglist.size(); x++) {
            regtemp1 = reglist.get(x);
            tmphref = match(regtemp1, "a", "href");
            tmpoldsrc = match(regtemp1, "a", "oldsrc"); // 文件src
            timer = tmpoldsrc.substring(2, 8); // 文件日期
            realHref = dir + timer + "/" + tmpoldsrc; // href地址拼接
            contents = contents.replace(regtemp1, regtemp1.replace(tmphref, realHref));
        }

        return contents;
    }

    private String getHtml(String siteName, String title, String author, String publishData, String content, int idx) {
        String commonHtml = "<html><head> <title>" + title + "</title>";
        commonHtml += "<meta name=\"subtitle\" content=" + "\"" + siteName + "\"" + "/>";
        commonHtml += "<meta name=\"author\" content=" + "\"" + author + "\"" + "/>";
        commonHtml += "<meta name=\"pubdate\" content=" + "\"" + publishData + "\"" + "/>";
        commonHtml += "<meta name=\"pageSize\" content=" + "\"" + idx + "\"" + "/></head>";
        commonHtml += "<body><meta name=\"ContentStart\"/>" + content;
        commonHtml += "<meta name=\"ContentEnd\"/></body></html>";
        return commonHtml;
    }
    private List<String> match(String source, String element, String attr, int i) {
        List<String> result = new ArrayList<String>();
        String reg = "<" + element + "[^<>]*?\\s" + attr + "=['\"]?(.*?)['\"]?(\\s.*?)?>";
        Matcher m = Pattern.compile(reg).matcher(source);
        while (m.find()) {
            String r = m.group(i);
            result.add(r);
        }
        return result;
    }

    private String match(String source, String element, String attr) {
        String reg = "<" + element + "[^<>]*?\\s" + attr + "=['\"]?(.*?)['\"]?(\\s.*?)?>";
        Matcher m = Pattern.compile(reg).matcher(source);
        m.find();
        return m.group(1);
    }
}
