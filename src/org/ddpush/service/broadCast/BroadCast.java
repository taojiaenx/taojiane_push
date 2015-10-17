package org.ddpush.service.broadCast;

import java.math.BigDecimal;
import java.sql.Date;
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
	 * 用户上传时ID是用户自己生成的
	 * 服务器下发时，ID是服务端，该广播的id
	 */
	private String broadCastID;
	private String authorUUID;
	/**
	 * 客户端上传数据中不用包括
	 */
	private Date createDate;
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
	public String getBroadCastID() {
		return broadCastID;
	}
	public void setBroadCastID(String packetID) {
		this.broadCastID = packetID;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getAuthorUUID() {
		return authorUUID;
	}
	public void setAuthorUUID(String authorUUID) {
		this.authorUUID = authorUUID;
	}
	
	
}
