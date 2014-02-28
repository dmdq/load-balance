package org.dmdq.balance.service.impl;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.dmdq.balance.channel.DefaultResponse;
import org.dmdq.balance.model.LoadProcess;
import org.dmdq.balance.model.LoadServer;
import org.dmdq.balance.service.InfoService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.google.common.collect.Lists;

public class InfoServiceImpl implements InfoService, Job {

	private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(InfoServiceImpl.class);

	public void checkList() {

	}

	public final DefaultResponse getXmlByRegionId(final String regionId) throws JsonGenerationException, JsonMappingException, IOException {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append(String.format("\n<servers id=\"%s\" >", regionId));
		List<LoadServer> list = getServersByRegionId(regionId);
		for (LoadServer server : list) {
			String groupHead = String.format("\n\t<server id=\"%s\" name=\"%s\" version=\"%s\" online=\"%d\" size=\"%d\" >", server.getId(), server.getName(), server.getVersion(), server.getOnline(),
					server.getSize());
			sb.append(groupHead);
			List<LoadProcess> processes = server.sort();
			for (LoadProcess process : processes) {
				String itemHead = String.format("\n\t\t<process id=\"%s\" host=\"%s\" port=\"%d\"  online=\"%d\" usedMemory=\"%d\"  status=\"%d\" config=\"%s\" />", process.getId(),
						process.getHost(), process.getPort(), process.getOnline(), process.getUsedMemory() / 1024 / 1024, process.getStatus(), process.getConfig());
				sb.append(itemHead);
			}
			sb.append("\n\t</server>");
		}
		sb.append("\n</servers>");

		DefaultResponse response = new DefaultResponse(sb.toString());
		response.setContentType("application/xml");
		return response;
	}

	public final List<LoadServer> getServersByRegionId(final String regionId) throws JsonGenerationException, JsonMappingException, IOException {
		return Lists.newArrayList();
	}

	public final List<LoadProcess> getProcessByRegionId(final String regionId) throws JsonGenerationException, JsonMappingException, IOException {
		return null;
	}

	/**
	 * 根据分区id获取服务器列表
	 * 
	 * @param regionId
	 *            分区id
	 */
	public final DefaultResponse getJsonByRegionId(String regionId) throws JsonGenerationException, JsonMappingException, IOException {
		if ("hy".equals(regionId)) {
			regionId = "hy360";// 兼容老版本
		}

		return null;
	}

	public final DefaultResponse getList(final String value) throws Exception {
		if ("json:hyios".equalsIgnoreCase(value)) {
			return getJsonByRegionId("hyios");
		} else if ("json:hy360".equalsIgnoreCase(value)) {
			return getJsonByRegionId("hy360");
		}
		return getJsonByRegionId("hy360");
	}

	public final void execute(final JobExecutionContext context) throws JobExecutionException {
		try {
			checkList();
		} catch (Exception e) {
			LOGGER.error("{}", e);
		}
	}

}
