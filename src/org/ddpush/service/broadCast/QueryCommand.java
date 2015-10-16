package org.ddpush.service.broadCast;

import java.math.BigDecimal;

/**
 * 命令体
 * @author taojiaen
 *
 */
public class QueryCommand {
	private BigDecimal lat;
	private BigDecimal lon;
	public BigDecimal getLat() {
		return lat;
	}
	public void setLat(BigDecimal lat) {
		this.lat = lat;
	}
	public BigDecimal getLon() {
		return lon;
	}
	public void setLon(BigDecimal lon) {
		this.lon = lon;
	}
	
}
