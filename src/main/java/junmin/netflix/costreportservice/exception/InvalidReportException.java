package junmin.netflix.costreportservice.exception;

public class InvalidReportException extends Exception{
	private int errorCode;

	public InvalidReportException(int errorCode, String message){
		super(message);
		this.errorCode = errorCode;
	}

	public int getErrorCode(){
		return errorCode;
	}
}
