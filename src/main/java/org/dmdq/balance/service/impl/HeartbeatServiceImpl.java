package org.dmdq.balance.service.impl;

import java.util.Iterator;

import org.dmdq.balance.conf.InfoCode;
import org.dmdq.balance.model.LoadProcess;
import org.dmdq.balance.service.HeartbeatService;
import org.dmdq.balance.util.AtomicLongMap;
import org.dmdq.balance.util.SocketUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *  
 */
public final class HeartbeatServiceImpl implements HeartbeatService, Job {

	private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(HeartbeatServiceImpl.class);

	public final void execute(JobExecutionContext context) throws JobExecutionException {
		doProcess();
	}

	public final void doProcess() {
		AtomicLongMap<LoadProcess> vector = AtomicLongMap.create();
		Iterator<LoadProcess> iterator = vector.asMap().keySet().iterator();
		while (iterator.hasNext()) {
			LoadProcess loadProcess = iterator.next();
			String host = loadProcess.getHost();
			int port = loadProcess.getPort();
			long times = vector.addAndGet(loadProcess, 1);
			if (SocketUtil.isStart(host, port)) {
				LoadProcess target = loadProcess.getParent().getMap().get(loadProcess.getId());
				if (null != target) {
					int beforeStatus = target.getStatus();
					target.setStatus(beforeStatus == InfoCode.SERVER_STOP ? InfoCode.SERVER_NORMAL : beforeStatus);
					vector.remove(loadProcess);
					LOGGER.trace("heartbeat remove.loadProcess={} connect success", loadProcess);
				}
			} else {
				if (times >= 10) {// 心跳连接次数大于10次则不进行心跳检测
					vector.remove(loadProcess);
					LOGGER.trace("heartbeat remove.loadProcess={} conect times={}", loadProcess, times);
				}
			}
		}
	}

}
