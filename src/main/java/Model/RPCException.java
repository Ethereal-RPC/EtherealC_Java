package Model;

public class RPCException extends Exception{
    public enum ErrorCode { Main, Intercepted,NotFoundService, RegisterError, NotFoundNetConfig,NoneAuthority,NotFoundRequest }
    private ErrorCode errorCode;
    public RPCException(String message)
    {
        super(message);
    }
    public RPCException(ErrorCode errorCode,String message)
    {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
