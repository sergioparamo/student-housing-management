package cat.itb.studenthousingweb.models;

import java.io.Serializable;

public class HouseApplication implements Serializable {

    String applicationId;
    String houseId;
    String studentId;
    String studentName;
    String studentEmail;
    String state;

    public HouseApplication() {
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getHouseId() {
        return houseId;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public HouseApplication(String applicationId, String houseId, String studentId, String studentName, String studentEmail, String state) {
        this.applicationId = applicationId;
        this.houseId = houseId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.state = state;
    }

    @Override
    public String toString() {
        return "HouseApplication{" +
                "applicationId='" + applicationId + '\'' +
                ", houseId='" + houseId + '\'' +
                ", studentId='" + studentId + '\'' +
                ", studentName='" + studentName + '\'' +
                ", studentEmail='" + studentEmail + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
