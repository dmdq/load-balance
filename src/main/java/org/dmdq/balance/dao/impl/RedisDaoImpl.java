package org.dmdq.balance.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dmdq.balance.dao.RedisDao;
import org.dmdq.balance.util.JedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.exceptions.JedisException;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class RedisDaoImpl implements RedisDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(RedisDaoImpl.class);

	private static final Boolean PREFIX = false;

	public void rename(String key, String newKey) {
		Jedis jedis = JedisUtil.getJedis();
		try {
			Pipeline p = jedis.pipelined();
			p.rename(buildKey(key), buildKey(newKey));
			p.sync();
		} catch (JedisException e) {
			JedisUtil.exceptionBroken(e, jedis);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			JedisUtil.release(jedis);
		}
	}

	public void renamenx(String key, String newKey) {
		Jedis jedis = JedisUtil.getJedis();
		try {
			Pipeline p = jedis.pipelined();
			p.renamenx(buildKey(key), buildKey(newKey));
			p.sync();
		} catch (JedisException e) {
			JedisUtil.exceptionBroken(e, jedis);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			JedisUtil.release(jedis);
		}
	}

	public Map<String, String> hgetAll(String key) {
		Jedis jedis = JedisUtil.getJedis();
		Map<String, String> map = Maps.newHashMap();
		try {
			map = jedis.hgetAll(buildKey(key));
		} catch (JedisException e) {
			JedisUtil.exceptionBroken(e, jedis);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			JedisUtil.release(jedis);
		}
		return map;
	}

	public Response<Long> hset(String key, String field, String value) {
		Response<Long> res = null;
		Jedis jedis = JedisUtil.getJedis();
		try {
			Pipeline p = jedis.pipelined();
			res = p.hset(buildKey(key), field, value);
			p.sync();
		} catch (JedisException e) {
			JedisUtil.exceptionBroken(e, jedis);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			JedisUtil.release(jedis);
		}
		return res;
	}

	public void hset(String key, String field, String value, int expire) {
		Jedis jedis = JedisUtil.getJedis();
		try {
			Pipeline p = jedis.pipelined();
			p.hset(buildKey(key), field, value);
			if (expire > 0) {
				p.expire(buildKey(key), expire);
			}
			p.sync();
		} catch (JedisException e) {
			JedisUtil.exceptionBroken(e, jedis);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			JedisUtil.release(jedis);
		}
	}

	public void hmset(String key, java.util.Map<String, String> map) {
		Jedis jedis = JedisUtil.getJedis();
		try {
			Pipeline p = jedis.pipelined();
			p.hmset(key, map);
			p.sync();
		} catch (JedisException e) {
			JedisUtil.exceptionBroken(e, jedis);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			JedisUtil.release(jedis);
		}
	}

	public void set(String key, String value, int expire) {
		Jedis jedis = JedisUtil.getJedis();
		try {
			Pipeline p = jedis.pipelined();
			p.set(buildKey(key), value);
			if (expire > 0) {
				p.expire(buildKey(key), expire);
			}
			p.sync();
		} catch (JedisException e) {
			JedisUtil.exceptionBroken(e, jedis);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			JedisUtil.release(jedis);
		}
	}

	public String get(String key) {
		String value = null;
		Jedis jedis = JedisUtil.getJedis();
		try {
			value = jedis.get(buildKey(key));
		} catch (JedisException e) {
			JedisUtil.exceptionBroken(e, jedis);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			JedisUtil.release(jedis);
		}
		return value;
	}

	public void lrem(String key, long count, String value) {
		Jedis jedis = JedisUtil.getJedis();
		try {
			Pipeline p = jedis.pipelined();
			p.lrem(buildKey(key), count, value);
			p.sync();
		} catch (JedisException e) {
			JedisUtil.exceptionBroken(e, jedis);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			JedisUtil.release(jedis);
		}
	}

	public Long hincrBy(String key, String field, long value) {
		Long index = 1L;
		Jedis jedis = JedisUtil.getJedis();
		try {
			Pipeline p = jedis.pipelined();
			Response<Long> res = p.hincrBy(buildKey(key), field, value);
			p.sync();
			index = res.get();
		} catch (JedisException e) {
			JedisUtil.exceptionBroken(e, jedis);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			JedisUtil.release(jedis);
		}
		return index;
	}

	public String hget(String key, String field) {
		Jedis jedis = JedisUtil.getJedis();
		String value = null;
		try {
			value = jedis.hget(buildKey(key), field);
		} catch (JedisException e) {
			JedisUtil.exceptionBroken(e, jedis);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			JedisUtil.release(jedis);
		}
		return value;
	}

	public void hdel(String key, String field) {
		Jedis jedis = JedisUtil.getJedis();
		try {
			Pipeline p = jedis.pipelined();
			p.hdel(buildKey(key), field);
			p.sync();
		} catch (JedisException e) {
			JedisUtil.exceptionBroken(e, jedis);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			JedisUtil.release(jedis);
		}
	}

	public boolean hexists(String key, String field) {
		boolean flag = false;
		Jedis jedis = JedisUtil.getJedis();
		try {
			flag = jedis.hexists(buildKey(key), field);
		} catch (JedisException e) {
			JedisUtil.exceptionBroken(e, jedis);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			JedisUtil.release(jedis);
		}
		return flag;
	}

	public void zrem(String key, String member) {
		Jedis jedis = JedisUtil.getJedis();
		try {
			Pipeline p = jedis.pipelined();
			p.zrem(buildKey(key), member);
			p.sync();
		} catch (JedisException e) {
			JedisUtil.exceptionBroken(e, jedis);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			JedisUtil.release(jedis);
		}
	}

	public void del(String... keys) {
		Jedis jedis = JedisUtil.getJedis();
		try {
			Pipeline p = jedis.pipelined();
			for (String key : keys) {
				p.del(buildKey(key));
			}
			p.sync();
		} catch (JedisException e) {
			JedisUtil.exceptionBroken(e, jedis);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			JedisUtil.release(jedis);
		}
	}

	public boolean exists(String key) {
		boolean flag = false;
		Jedis jedis = JedisUtil.getJedis();
		try {
			flag = jedis.exists(buildKey(key));
		} catch (JedisException e) {
			JedisUtil.exceptionBroken(e, jedis);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			JedisUtil.release(jedis);
		}
		return flag;
	}

	public Response<Long> rpush(String key, String content) {
		Response<Long> res = null;
		Jedis jedis = JedisUtil.getJedis();
		try {
			Pipeline p = jedis.pipelined();
			res = p.rpush(buildKey(key), content);
			p.sync();
		} catch (JedisException e) {
			JedisUtil.exceptionBroken(e, jedis);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			JedisUtil.release(jedis);
		}
		return res;
	}

	public List<String> lrange(String key, int start, int end) {
		List<String> list = Lists.newArrayList();
		Jedis jedis = JedisUtil.getJedis();
		try {
			list = jedis.lrange(buildKey(key), start, end);
		} catch (JedisException e) {
			JedisUtil.exceptionBroken(e, jedis);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			JedisUtil.release(jedis);
		}
		return list;
	}

	public Long llen(String key) {
		Long len = 0L;
		Jedis jedis = JedisUtil.getJedis();
		try {
			len = jedis.llen(buildKey(key));
		} catch (JedisException e) {
			JedisUtil.exceptionBroken(e, jedis);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			JedisUtil.release(jedis);
		}
		return len;
	}

	public Long hlen(String key) {
		Long size = 0L;
		Jedis jedis = JedisUtil.getJedis();
		try {
			size = jedis.hlen(buildKey(key));
		} catch (JedisException e) {
			JedisUtil.exceptionBroken(e, jedis);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			JedisUtil.release(jedis);
		}
		return size;
	}

	public void zadd(final String key, double score, String member) {
		Jedis jedis = null;
		try {
			jedis = JedisUtil.getJedis();
			Pipeline p = jedis.pipelined();
			p.zadd(buildKey(key), score, member);
			p.sync();
		} catch (JedisException e) {
			JedisUtil.exceptionBroken(e, jedis);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			JedisUtil.release(jedis);
		}
	}

	public Double zscore(final String key, final String member) {
		Double score = 0.0;
		Jedis jedis = null;
		try {
			jedis = JedisUtil.getJedis();
			score = jedis.zscore(buildKey(key), member);
		} catch (JedisException e) {
			JedisUtil.exceptionBroken(e, jedis);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			JedisUtil.release(jedis);
		}
		return score;
	}

	public Long zrevrank(final String key, final String member) {
		Long rank = 99999L;
		Jedis jedis = null;
		try {
			jedis = JedisUtil.getJedis();
			rank = jedis.zrevrank(buildKey(key), member);
		} catch (JedisException e) {
			JedisUtil.exceptionBroken(e, jedis);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			JedisUtil.release(jedis);
		}
		return rank;
	}

	public Set<String> zrevrange(final String key, final long start, final long end) {
		Set<String> list = Sets.newHashSet();
		Jedis jedis = null;
		try {
			jedis = JedisUtil.getJedis();
			list = jedis.zrevrange(buildKey(key), start, end);
		} catch (JedisException e) {
			JedisUtil.exceptionBroken(e, jedis);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			JedisUtil.release(jedis);
		}
		return list;
	}

	public Boolean sismember(final String key, final String member) {
		boolean flag = false;
		Jedis jedis = null;
		try {
			jedis = JedisUtil.getJedis();
			flag = jedis.sismember(buildKey(key), member);
		} catch (JedisException e) {
			JedisUtil.exceptionBroken(e, jedis);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			JedisUtil.release(jedis);
		}
		return flag;
	}

	public Response<Long> sadd(String key, String member, int expire) {
		Response<Long> result = null;
		Jedis jedis = null;
		try {
			jedis = JedisUtil.getJedis();
			Pipeline p = jedis.pipelined();
			result = p.sadd(buildKey(key), member);
			if (expire > 0) {
				p.expire(buildKey(key), expire);
			}
			p.sync();
		} catch (JedisException e) {
			JedisUtil.exceptionBroken(e, jedis);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			JedisUtil.release(jedis);
		}
		return result;
	}

	public Response<Long> sadd(String key, String member) {
		return this.sadd(key, member, 0);
	}

	public Set<String> smembers(String key) {
		Set<String> members = Sets.newHashSet();
		Jedis jedis = null;
		try {
			jedis = JedisUtil.getJedis();
			members = jedis.smembers(buildKey(key));
		} catch (JedisException e) {
			JedisUtil.exceptionBroken(e, jedis);
		} catch (Exception e) {
			LOGGER.error("exception:", e);
		} finally {
			JedisUtil.release(jedis);
		}
		return members;
	}

	public Response<Long> srem(String key, String member) {
		Response<Long> res = null;
		Jedis jedis = null;
		try {
			jedis = JedisUtil.getJedis();
			Pipeline p = jedis.pipelined();
			res = p.srem(buildKey(key), member);
			p.sync();
		} catch (JedisException e) {
			JedisUtil.exceptionBroken(e, jedis);
		} catch (Exception e) {
			LOGGER.error("exception:", e);
		} finally {
			JedisUtil.release(jedis);
		}
		return res;
	}

	/**
	 * 获取key
	 * 
	 * @param key
	 * @return
	 */
	public String buildKey(String key) {
		String prefix = "";
		return PREFIX ? prefix + key : key;
	}

}
