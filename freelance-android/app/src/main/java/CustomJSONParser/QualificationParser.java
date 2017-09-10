package CustomJSONParser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import Utils.Constants;
import Utils.DateTimeManager;

/**
 * Created by akash on 17/2/17.
 */

public class QualificationParser {

    String insName, board, marks, serverReply;
    int marksunit,yop;

    public QualificationParser(String serverReply) {
        this.serverReply = serverReply;
        parseServerReply(serverReply);
    }

    private void parseServerReply(String serverReply) {
        try {
            JSONObject jsonObject = new JSONObject(serverReply);
            insName = jsonObject.getString("instName");
            board = jsonObject.getString("board");
            marks = jsonObject.getString("marks");
            yop = Arrays.asList(DateTimeManager.yearArray()).indexOf(jsonObject.getString("yop"));
            marksunit = Arrays.asList(Constants.MARKS_UNIT).indexOf(jsonObject.getString("marksType"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getInsName() {
        return insName;
    }

    public String getBoard() {
        return board;
    }

    public String getMarks() {
        return marks;
    }

    public int getMarksunit() {
        return marksunit;
    }

    public int getYop() {
        return yop;
    }

    public static String makeObject(String s, String s1, String s2, String s3, String s4,String s5) {

        try
        {
            JSONObject object = new JSONObject();
            object.put("stage",s);
            object.put("instName",s1);
            object.put("board",s2);
            object.put("marksType",s3);
            object.put("marks",s4);
            object.put("yop",s5);
            return object.toString();
        }
        catch (JSONException e) {

        }
        return null;
    }


}
