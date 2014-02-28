package org.dmdq.balance.conf;

/**
 * 服务器状态码
 */
public final class InfoCode {
	/** 服务器正常 */
	public final static int SERVER_NORMAL = 101;
	/** 服务器拥挤 */
	public final static int SERVER_CROWD = 102;
	/** 服务器爆满 */
	public final static int SERVER_FULL = 103;
	/** 服务器维护 */
	public final static int SERVER_PUBLISH = 104;
	/** 服务器停止 */
	public final static int SERVER_STOP = 105;
	/** 端口占用 */
	public final static int PORT_IN_USE = 106;
	/** 启动异常 */
	public final static int START_EXCEPTION = 107;
	/** 还剩一个端口 */
	public final static int LEFT_ONE_PORT = 108;
	/** 没有监听该端口 */
	public final static int NO_PORT = 109;
	/** 成功 */
	public final static int SUCCESS = 1;
	/** 失败 */
	public final static int NONE = 0;
	/**
	 * 已经存在一个相同的应用名称
	 */
	public final static int EXISTS_APP_NAME = 1001;
	/**
	 * 不存在一个应用名称
	 */
	public final static int NOT_EXISTS_APP_NAME = 1002;
	/**
	 * 已经存在一个相同的用户名称
	 */
	public final static int EXISTS_USER_NAME = 1003;
	/**
	 * 不存在一个用户名称
	 */
	public final static int NOT_EXISTS_USER_NAME = 1004;
	/**
	 * 存在分区名称
	 */
	public final static int EXISTS_REGION_NAME = 1005;
	/**
	 * 不存在分区名称
	 */
	public final static int NOT_EXISTS_REGION_NAME = 1006;
	/**
	 * 存在服务器名称
	 */
	public final static int EXISTS_SERVER_NAME = 1007;
	/**
	 * 不存在服务器名称
	 */
	public final static int NOT_EXISTS_SERVER_NAME = 1008;
	/**
	 * 存在进程
	 */
	public final static int EXISTS_PROCESS = 1009;
	/**
	 * 不存在进程
	 */
	public final static int NOT_EXISTS_PROCESS = 1010;
	/**
	 * 密码错误
	 */
	public final static int USER_PASSWORD_ERROR = 1011;
	/**
	 * 密码错误
	 */
	public final static int NAME_EXISTS = 1012;
	/**
	 * 存在相同主机地址和端口
	 */
	public final static int EXISTS_HOST_AND_PORT = 1013;

}
