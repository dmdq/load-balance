package org.dmdq.balance.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Response;

public  interface RedisDao {

	Map<String, String> hgetAll(String key) ;
	
	Response<Long> hset(String key, String field, String value);
	
	void hset(String key, String field, String value, int expire) ;
	
	void set(String key, String value, int expire);
	
	Response<Long> srem(String key, String member) ;
	
	Set<String> smembers(String key) ;
	
	Response<Long> sadd(String key, String member) ;
	
	Response<Long> sadd(String key, String member, int expire);
	
	 Boolean sismember(final String key, final String member) ;
	 
	 Set<String> zrevrange(final String key, final long start,
				final long end);
	 
	 Long zrevrank(final String key, final String member) ;
	 
	 Double zscore(final String key, final String member) ;
	 
	 void zadd(final String key, double score, String member);
	 
	 Long hlen(String key);
	 
	 Long llen(String key);
	 
	 List<String> lrange(String key, int start, int end);
	 
	 Response<Long> rpush(String key, String content) ;
	 
	 boolean exists(String key);
	 
	 void del(String... keys);
	 
	 void zrem(String key, String member);
	 
	 boolean hexists(String key, String field) ;
	 
	 void hdel(String key, String field);
	 
	 String hget(String key, String field);
	 
	 Long hincrBy(String key, String field, long value) ;
	 
	 void lrem(String key, long count, String value);
	 
	 String get(String key) ;
	 
	 void hmset(String key,Map<String,String> map);
}
