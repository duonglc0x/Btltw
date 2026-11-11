package web.com.utils;

import web.com.config.Environment;

public class Constant {
	public static final String DIR = Environment.get("UPLOAD_DIR", "C:/upload");
	public static final String MAIL_HOST = Environment.get("MAIL_HOST", "smtp.gmail.com");
	public static final int MAIL_PORT = Integer.parseInt(Environment.get("MAIL_PORT", "587"));
	public static final String MAIL_USERNAME = Environment.get("MAIL_USERNAME", "");
	public static final String MAIL_PASSWORD = Environment.get("MAIL_PASSWORD", "");
	public static final String MAIL_FROM_NAME = Environment.get("MAIL_FROM_NAME", "CRUD Shop");
	
	public static final int OTP_EXPIRE_MINUTES = 5;
	public static final int PRODUCT_PAGE_SIZE = 6;
	public static final int HOME_LATEST_PRODUCT_COUNT = 10;
}
