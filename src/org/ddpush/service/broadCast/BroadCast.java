package org.ddpush.service.broadCast;

import java.math.BigDecimal;
/**
 * 广播消息体
 * @author taojiaen
 *
 */
public class BroadCast {
	/**
	 * 精度
	 */
	private BigDecimal lat;
	/**
	 * 纬度
	 */
	private BigDecimal lon;
    /**
     * 消息体
     */
	private String body;
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
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
}
