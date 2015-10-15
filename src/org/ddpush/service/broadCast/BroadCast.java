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
	/**
	 * 消息ID
	 */
	private String packetID;
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
	public String getPacketID() {
		return packetID;
	}
	public void setPacketID(String packetID) {
		this.packetID = packetID;
	}
	
}
