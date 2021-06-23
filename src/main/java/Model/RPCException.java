package Model;

public class RPCException extends Exception{
    public enum ErrorCode {Core, Runtime}
    private ErrorCode errorCode;
    private String message;
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public RPCException(ErrorCode errorCode,String message)
    {
        super(message);
        this.message = message;
        this.errorCode = errorCode;
    }
}
