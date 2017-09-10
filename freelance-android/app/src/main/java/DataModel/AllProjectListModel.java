package DataModel;

import java.util.ArrayList;

/**
 * Created by akash on 20/4/17.
 */

public class AllProjectListModel
{
    String id, name, decs, category,deadline,status;
    ArrayList<ParticipantForSmallIcons> participantrs;

    public AllProjectListModel(String id, String name, String decs, String category, String deadline, String status, ArrayList<ParticipantForSmallIcons> participantrs) {
        this.id = id;
        this.name = name;
        this.decs = decs;
        this.category = category;
        this.deadline = deadline;
        this.status = status;
        this.participantrs = participantrs;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDecs() {
        return decs;
    }

    public String getCategory() {
        return category;
    }

    public String getDeadline() {
        return deadline;
    }

    public String getStatus() {
        return status;
    }

    public ArrayList<ParticipantForSmallIcons> getParticipantrs() {
        return participantrs;
    }
}
