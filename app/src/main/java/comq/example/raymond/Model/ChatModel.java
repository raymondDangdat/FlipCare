package comq.example.raymond.Model;

public class ChatModel {
    private String name, message, uId;
    private long dateSent;

    public ChatModel() {
    }

    public ChatModel(String name, String message, String uId, long dateSent) {
        this.name = name;
        this.message = message;
        this.uId = uId;
        this.dateSent = dateSent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public long getDateSent() {
        return dateSent;
    }

    public void setDateSent(long dateSent) {
        this.dateSent = dateSent;
    }
}
