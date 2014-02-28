package org.dmdq.balance.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

public final class JedisUtil {

	private final static Logger _logger = LoggerFactory
			.getLogger(JedisUtil.class);

	private static String _host = "127.0.0.1";
	private static int _port = 6379;
	private static String _password = "";
	private static JedisPool _jedisPool;
	private static ShardedJedisPool _shardedJedisPool;

	/**
	 * 初始化池
	 */
	public static final void initialPool() {
		JedisPoolConfig config = new JedisPoolConfig();
		// 控制一个pool最多有多少个状态为idle的jedis实例(-1不限制)
		config.setMaxActive(-1);
		// 最大能够保持空闲状态的对象数
		config.setMaxIdle(-1);
		// 当池中没有jedis实例最大等待时间
		config.setMaxWait(100000L);
		// 在borrow一个jedis实例时，是否提前进行alidate操作；如果为true，则得到的jedis实例均是可用的；
		config.setTestOnBorrow(true);
		// 在还会给pool时，是否提前进行validate操作
		config.setTestOnReturn(true);
		if (_password.length() > 1)
			_jedisPool = new JedisPool(config, _host, _port, 100000, _password);
		else
			_jedisPool = new JedisPool(config, _host, _port, 100000);
	}

	/**
	 * 初始化切片池
	 */
	public static final void initialShardedPool() {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxActive(20);
		config.setMaxIdle(5);
		config.setMaxWait(1000L);
		config.setTestOnBorrow(true);
		List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
		shards.add(new JedisShardInfo(_host, _port, "master"));
		_shardedJedisPool = new ShardedJedisPool(config, shards);
	}

	/**
	 * 获取jedis连接
	 * 
	 * @return Jedis
	 */
	public static final Jedis getJedis() {
		Jedis jedis = null;
		if (_jedisPool != null) {
			try {
				jedis = (Jedis) _jedisPool.getResource();
			} catch (Exception e) {
				_logger.warn("getJedis exception:{}", e);
			}
		}
		return jedis;
	}

	/**
	 * 获取shardedJedis连接
	 * 
	 * @return ShardedJedis
	 */
	public static final ShardedJedis getShardedJedis() {
		ShardedJedis shardedjedis = null;
		if (_shardedJedisPool != null) {
			try {
				shardedjedis = (ShardedJedis) _shardedJedisPool.getResource();
			} catch (Exception e) {
				_logger.warn("ShardedJedis exception:{}", e);
			}
		}
		return shardedjedis;
	}

	/**
	 * 重置连接池
	 */
	public static void jedisPoolReset() {
		try {
			_jedisPool.destroy();
			_shardedJedisPool.destroy();
		} catch (Exception e) {
			_logger.warn("jedisPoolReset exception:{}", e);
		}
		initialPool();
	}

	/**
	 * 配合使用getJedis方法后将jedis对象释放回连接池中
	 * 
	 * @param jedis
	 *            使用完毕的Jedis对象
	 * @return true 释放成功；否则返回false
	 */
	public static final boolean release(Jedis jedis) {
		if (_jedisPool != null && jedis != null) {
			_jedisPool.returnResource(jedis);
			return true;
		}
		return false;
	}

	/**
	 * 当出现异常时 销毁对象
	 * 
	 * @param jedis
	 */
	public static final void exceptionBroken(Throwable t, Jedis jedis) {
		_jedisPool.returnBrokenResource(jedis);
		// jedis.disconnect();
		_logger.warn("===============jedis exception===============", t);
	}

	/**
	 * 通过解析xml来赋值
	 * 
	 * @param attributes
	 */
	public static final void parseXML(Attributes attributes) {
		for (int i = 0; i < attributes.getLength(); i++) {
			String qName = attributes.getQName(i);
			String value = attributes.getValue(i);
			if ("host".equals(qName))
				_host = value;
			else if ("port".equals(qName))
				_port = Integer.parseInt(value);
			else if ("password".equals(qName))
				_password = value;
		}
	}

	/**
	 * 通过解析properties来赋值
	 * 
	 * @param properties
	 */
	public static final void parseProperties(Properties properties) {
		if (properties.containsKey("redis.host")) {
			_host = properties.getProperty("redis.host", "localhost").trim();
		}
		if (properties.containsKey("redis.port")) {
			_port = Integer.valueOf(properties
					.getProperty("redis.port", "6379").trim());
		}
		if (properties.containsKey("redis.password")) {
			_password = properties.getProperty("redis.password", "").trim();
		}
	}

	/**
	 * 初始化参数
	 * 
	 * @param host
	 * @param port
	 * @param password
	 */
	public static final void initConfig(String host, int port, String password) {
		_host = host;
		_port = port;
		_password = password;
	}

}
