package junmin.netflix.costreportservice.exception;

public enum ErrorEnum {
	PRODUCTION_NOT_EXIST(1, "PRODUCTION_NOT_EXIST"),
	PRODUCTION_EP_NOT_EXIST(2, "PRODUCTION_EP_NOT_EXIST"),
	INVALID_EP_CODE(3, "INVALID_EP_CODE"),
	EMPTY_REPORT(4, "EMPTY_REPORT"),
	PRODUCTION_NAME_IS_EMPTY(5, "PRODUCTION_NAME_IS_EMPTY");

	private String name;
	private int id;
	private ErrorEnum(int id, String name){
		this.id = id;
		this.name=name;
	};
	public String getName(){
		return name;
	}
	public int getId(){ return id; };
}
