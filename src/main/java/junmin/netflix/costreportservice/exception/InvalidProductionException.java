package junmin.netflix.costreportservice.exception;

public class InvalidProductionException extends Exception{
	private int errorCode;

	public InvalidProductionException(int errorCode, String message){
		super(message);
		this.errorCode = errorCode;
	}

	public int getErrorCode(){
		return errorCode;
	}
}
