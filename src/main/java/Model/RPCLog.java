package Model;

public class RPCLog {
    public enum LogCode { Core, Runtime }
    private String message;
    private LogCode code;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LogCode getCode() {
        return code;
    }

    public void setCode(LogCode code) {
        this.code = code;
    }

    public RPCLog(LogCode code,String message) {
        this.message = message;
        this.code = code;
    }
}
