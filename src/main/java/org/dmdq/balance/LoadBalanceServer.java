package org.dmdq.balance;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.dmdq.balance.reflect.FunctionPool;
import org.dmdq.balance.service.ServerService;
import org.dmdq.balance.service.impl.HeartbeatServiceImpl;
import org.dmdq.balance.service.impl.InfoServiceImpl;
import org.dmdq.balance.service.impl.UserServiceImpl;
import org.dmdq.balance.util.BeanUtil;
import org.dmdq.balance.util.JedisUtil;
import org.dmdq.balance.util.ReadParam;
import org.dmdq.balance.util.RootUtil;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * -server -XX:+PrintGCTimeStamps -XX:+PrintGCDetails -Xmx2g -Xms2g -Xmn256m
 * -XX:PermSize=128m -Xss256k -XX:MaxTenuringThreshold=31 -XX:+DisableExplicitGC
 * -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+CMSParallelRemarkEnabled
 * -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m
 * -XX:+UseFastAccessorMethods
 */
public final class LoadBalanceServer {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoadBalanceServer.class);

	private ServerService serverService;

	public static void main(String[] args) throws Exception {
		start();
	}

	public static void start() throws Exception {
		FunctionPool.init();
		new LoadBalanceServer();
	}

	public LoadBalanceServer() throws Exception {
		if (!System.getProperties().containsKey("config.dir")) {
			System.getProperties().put("config.dir", "src/main/resources");
		}
		init();
	}

	private final void init() throws Exception {
		doStartWork();
		doShutDownWork();
		initJedis();
		initData();
		// doTask();
	}

	private final void doStartWork() {
		serverService = BeanUtil.getBean(ServerService.class);
		String port = System.getProperty("port");
		port = null == port ? "8888" : port;
		boolean status = serverService.start(Integer.valueOf(port));
		if (status) {
			LOGGER.info("server start port={} pid={}", port, RootUtil.getProcessID());
		} else {
			System.exit(0);
		}
	}

	private final void doShutDownWork() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				serverService.stop(-1);
			}
		});
	}

	private final void initJedis() throws FileNotFoundException, IOException {
		String configDir = System.getProperty("config.dir", "src/main/resources");
		Properties properties = new ReadParam(configDir + "/redis.properties").getProperties();
		properties.putAll(System.getProperties());
		JedisUtil.parseProperties(properties);
		JedisUtil.initialPool();
	}

	private final void initData() {
		BeanUtil.getBean(UserServiceImpl.class).getFromRedis();
	}

	protected final void doTask() throws SchedulerException {
		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
		JobDetail job = JobBuilder.newJob(InfoServiceImpl.class).withIdentity("job1", "group1").build();
		CronScheduleBuilder cb = CronScheduleBuilder.cronSchedule("");
		CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger1", "group1").withSchedule(cb).build();
		scheduler.scheduleJob(job, trigger);
		scheduler.start();
		job = JobBuilder.newJob(HeartbeatServiceImpl.class).withIdentity("job2", "group1").build();
		CronScheduleBuilder cb1 = CronScheduleBuilder.cronSchedule("0/5 * * ? * *");
		CronTrigger trigger1 = TriggerBuilder.newTrigger().withIdentity("trigger2", "group1").withSchedule(cb1).build();
		scheduler.scheduleJob(job, trigger1);
	}

}
