package org.dmdq.balance.model.domain;

import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

import org.dmdq.balance.model.LoadForbidden;

import com.google.common.collect.Sets;

public final class BalanceForbidden implements LoadForbidden {

	public static final String DEFAULT_PATTERN = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[0-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
	/**
	 * 白名单列表
	 */
	private Set<String> whites;
	/**
	 * 白名单区间列表
	 */
	private Set<String> whitesBetween;
	/**
	 * 黑名单列表
	 */
	private Set<String> blacks;
	/**
	 * 黑名单区间列表
	 */
	private Set<String> blackBetween;

	public BalanceForbidden() {
		whites = Sets.newHashSet();
		whitesBetween = Sets.newHashSet();
		blacks = Sets.newHashSet();
		blackBetween = Sets.newHashSet();
	}

	@Override
	public final void addWhite(final String value) {
		this.whites.add(value);
	}

	@Override
	public final void addWhitesBetween(final String value) {
		this.whitesBetween.add(value);
	}

	@Override
	public final void addBlack(final String value) {
		this.blacks.add(value);
	}

	@Override
	public final void addBlackBetween(final String value) {
		this.blackBetween.add(value);
	}

	@Override
	public final Set<String> getWhites() {
		return whites;
	}

	@Override
	public final void setWhites(final Set<String> whites) {
		this.whites = whites;
	}

	@Override
	public final Set<String> getWhitesBetween() {
		return whitesBetween;
	}

	@Override
	public final void setWhitesBetween(final Set<String> whitesBetween) {
		this.whitesBetween = whitesBetween;
	}

	@Override
	public final Set<String> getBlacks() {
		return blacks;
	}

	@Override
	public final void setBlacks(final Set<String> black) {
		this.blacks = black;
	}

	@Override
	public final Set<String> getBlackBetween() {
		return blackBetween;
	}

	@Override
	public final void setBlackBetween(final Set<String> blackBetween) {
		this.blackBetween = blackBetween;
	}

	@Override
	public boolean isVaildWhite(final String ip) {
		return isVaild(whites.iterator(), whitesBetween.iterator(), ip);
	}

	@Override
	public boolean isVaildBlack(final String ip) {
		return !isVaild(blacks.iterator(), blackBetween.iterator(), ip);
	}

	/**
	 * 校验是否是IP基本格式
	 * 
	 * @param ip
	 */
	public final boolean isVaild(String ip) {
		return ip.split("[.]").length == 4 && Pattern.compile(DEFAULT_PATTERN).matcher(ip.replace("*", "1")).matches();
	}

	/**
	 * 校验指定IP是否在某个名单或者区间内
	 * 
	 * @param list
	 *            校验的列表
	 * @param between
	 *            校验的区间
	 * @param ip
	 *            等待校验的ip
	 */
	public boolean isVaild(final Iterator<String> list, final Iterator<String> between, final String ip) {
		if (!isVaild(ip)) {
			return false;
		}
		final String fields[] = ip.split("[.]");
		boolean isWhite = false;
		while (list.hasNext()) {
			if (isWhite)
				return isWhite;
			boolean flag = true;
			String value = list.next().trim();
			if (isVaild(value)) {
				String srcs[] = value.split("[.]");
				for (int i = 0; i < 4; i++) {
					String src = srcs[i];
					flag &= "*".equals(src) ? true : src.equals(fields[i]);
				}
				isWhite |= flag;
			}
		}
		while (between.hasNext()) {
			if (isWhite)
				return isWhite;
			String values[] = between.next().trim().split("-");
			String star = values[0].trim();
			String end = values[1].trim();
			if (isVaild(star) && isVaild(end)) {
				String srcStar[] = star.split("[.]");
				String srcEnd[] = end.split("[.]");
				boolean flag = true;
				for (int i = 0; i < 4; i++) {
					if (!flag) {
						break;
					}
					int field = Integer.parseInt(fields[i]);
					String filedStar = srcStar[i];
					flag &= "*".equals(filedStar) ? true : field >= Integer.parseInt(filedStar);
				}
				if (flag) {// 如果最小区间匹配则匹配最大区间
					for (int i = 0; i < 4; i++) {
						int field = Integer.parseInt(fields[i]);
						String filedEnd = srcEnd[i];
						if ("*".equals(filedEnd)) {
							flag &= true;
						} else {
							if (field < Integer.parseInt(filedEnd)) {
								flag &= true;
								break;
							} else if (field == Integer.parseInt(filedEnd)) {
								flag &= true;
							} else {
								flag &= false;
								break;
							}
						}
					}
				}
				isWhite |= flag;
			}
		}
		return isWhite;
	}

	@Override
	public final String build() {
		String pattern = "";
		final Iterator<String> _whites = whites.iterator();
		final Iterator<String> _whitesBetween = whitesBetween.iterator();
		final Iterator<String> _black = blacks.iterator();
		final Iterator<String> _blackBetween = blackBetween.iterator();
		while (_whites.hasNext()) {
			String value = _whites.next().trim();
			if (isVaild(value)) {
			}
		}
		while (_whitesBetween.hasNext()) {
			String values[] = _whitesBetween.next().trim().split("-");
			String star = values[0].trim();
			String end = values[1].trim();
			if (isVaild(star) && isVaild(end)) {
			}
		}
		while (_black.hasNext()) {
			String value = _black.next().trim();
			if (isVaild(value)) {
			}
		}
		while (_blackBetween.hasNext()) {
			String values[] = _blackBetween.next().trim().split("-");
			String star = values[0].trim();
			String end = values[1].trim();
			if (isVaild(star) && isVaild(end)) {
			}
		}
		return pattern;
	}

}
