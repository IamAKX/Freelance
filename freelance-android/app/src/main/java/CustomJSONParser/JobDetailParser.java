package CustomJSONParser;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import Utils.DateFormatManager;

import static Utils.Constants.USER_CURRENT_PROFILE_IMAGE;

/**
 * Created by akash on 21/4/17.
 */

public class JobDetailParser
{
    String reply, name, desc, duration,deadline, category, ename, eimg, eid, chatroomID,phoneNo,acceptedStatus;

    public JobDetailParser(String reply) {
        this.reply = reply;

        try {
            JSONObject object = new JSONObject(reply);
            name = object.getString("name").toUpperCase();
            desc = object.getString("description");
            category = object.getString("category");
            chatroomID = object.getString("chatroomId");
            acceptedStatus = object.getString("userStatus");
            JSONObject dur = object.getJSONObject("duration");

            duration = "DURATION<br/><b>"+dur.getString("start")+"-"+dur.getString("end")+" "+dur.getString("type")+"</b><br/><br/><br/>";
            deadline = "DEADLINE<br/><b>"+ DateFormatManager.utcTODate(dur.getString("deadline"),"dd MMM,yy")+"</b><br/><br/>";

            JSONObject emp = object.getJSONObject("employer");
            ename = emp.getString("name");
            eimg = USER_CURRENT_PROFILE_IMAGE + emp.getString("image");
            eid = emp.getString("_id");
            phoneNo = emp.getString("phone");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getDuration() {
        return duration;
    }

    public String getDeadline() {
        return deadline;
    }

    public String getCategory() {
        return category;
    }

    public String getEname() {
        return ename;
    }

    public String getEimg() {
        return eimg;
    }

    public String getEid() {
        return eid;
    }

    public String getChatroomID() {
        return chatroomID;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getAcceptedStatus() {
        return acceptedStatus;
    }
}
