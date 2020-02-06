package comq.example.raymond.Model;

public class ContactModel {
    private String userName, uId;
    private long timeSent;

    public ContactModel() {
    }

    public ContactModel(String userName, String uId, long timeSent) {
        this.userName = userName;
        this.uId = uId;
        this.timeSent = timeSent;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public long getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(long timeSent) {
        this.timeSent = timeSent;
    }
}
