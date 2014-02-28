package org.dmdq.balance.conf;

public final class RedisKeys {
	/**
	 * 服务器列表
	 */
	public static final String _SERVER_GROUP_LIST = "server:list";
	/**
	 * 服务器组列表
	 */
	public static final String _SERVER_GROUP_RANK = "server:group:balancing:";
	/**
	 * 服务器组info
	 */
	public static final String _SERVER_GROUP_INFO = "server:info:group:";
	/**
	 * 服务器key头
	 */
	public static final String _SERVER_ID_INFO = "server:info:id:";
	/**
	 * 服务器id
	 */
	public static final String SERVER_ID = "id";
	/**
	 * 服务器组
	 */
	public static final String SERVER_GROUP = "group";
	/**
	 * 服务器地址
	 */
	public static final String SERVER_HOST = "host";
	/**
	 * 服务器端口
	 */
	public static final String SERVER_PORT = "port";
	/**
	 * 服务器名
	 */
	public static final String SERVER_NAME = "name";
	/**
	 * 服务器状态
	 */
	public static final String SERVER_STATUS = "status";
	/**
	 * 服务器版本
	 */
	public static final String SERVER_VERSION = "version";
	/**
	 * 用户列表<br/>
	 * 类型: SET <br/>
	 * 操作: SADD _users $user
	 */
	public static final String KEY_LIST_USERS = "_users";
	/**
	 * 用户数据<br/>
	 * 类型: HASH <br/>
	 * 操作: HSET user:data:$userName $field $value
	 */
	public static final String KEY_DATA_USER = "data:user:%s";

	/**
	 * 用户应用列表<br/>
	 * 类型: HASH<br/>
	 * 操作: HET user:app:$userName $appName $appKey
	 */
	public static final String KEY_INDEX_USER_APP = "index:user:app:%s";
	/**
	 * 应用具体的数据<br/>
	 * 类型: HASH<br/>
	 * 操作: HET app:data:$appName $field $value
	 */
	public static final String KEY_DATA_APP = "data:app:%s";
	/**
	 * 应用分区 <br/>
	 * 类型: HASH<br/>
	 * 操作: HSET app:region:$appName
	 */
	public static final String KEY_INDEX_APP_REGION = "index:app:region:%s";
	/**
	 * 分区具体信息<br/>
	 * 类型: HASH<br/>
	 * 操作: HSET region:data:$regionId $field
	 */
	public static final String KEY_DATA_REGION = "data:region:%s";
	/**
	 * 分区下的服务器 <br/>
	 * 类型: HASH<br/>
	 * 操作: HSET region:server:$regionId $serverId $serverName
	 */
	public static final String KEY_INDEX_REGION_SERVER = "index:region:server:%s";
	/**
	 * 服务器具体信息<br/>
	 * 类型: HASH<br/>
	 * 操作: HSET region:data:$regionId $field $value
	 */
	public static final String KEY_DATA_SERVER = "data:server:%s";
	/**
	 * 进程信息配置索引<br/>
	 * 类型: HASH<br/>
	 * 操作: HSET index:process:%serverId $field $value
	 */
	public static final String KEY_INDEX_SERVER_PROCESS = "index:server:process:%s";
	/**
	 * 服务器具体信息<br/>
	 * 类型: HASH<br/>
	 * 操作: HSET data:process:$serverId:$process $field $value
	 */
	public static final String KEY_DATA_PROCESS = "data:process:%s:%s";

	/**
	 * 过滤器黑名单<br/>
	 * 类型: HASH<br/>
	 * 操作: HSET forbidden:black:$id $value $type
	 */
	public static final String KEY_FORBIDDEN_BLACK = "forbidden:black:%s";
	/**
	 * 过滤器白名单<br/>
	 * 类型: HASH<br/>
	 * 操作: HSET forbidden:black:$id $value $type
	 */
	public static final String KEY_FORBIDDEN_WHITE = "forbidden:white:%s";

}
