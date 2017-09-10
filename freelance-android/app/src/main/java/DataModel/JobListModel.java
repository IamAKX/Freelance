package DataModel;

/**
 * Created by akash on 22/4/17.
 */

public class JobListModel {
    String id, name, desc,  category, participant, creatAt, projectStatus;

    public JobListModel(String id, String name, String desc, String category, String participant, String creatAt, String projectStatus) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.category = category;
        this.participant = participant;
        this.creatAt = creatAt;
        this.projectStatus = projectStatus;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getCategory() {
        return category;
    }

    public String getParticipant() {
        return participant;
    }

    public String getCreatAt() {
        return creatAt;
    }

    public String getProjectStatus() {
        return projectStatus;
    }
}

