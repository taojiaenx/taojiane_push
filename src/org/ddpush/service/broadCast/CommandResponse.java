package org.ddpush.service.broadCast;

/**
 * 命令执行状况的回应
 * 
 * @author taojiaen
 *
 */
public class CommandResponse {
	/**
	 * 客户端发送的包ID
	 */
	private String packetID;
	/**
	 * 执行情况
	 */
	private int res;
	public String getPacketID() {
		return packetID;
	}
	public void setPacketID(String packetID) {
		this.packetID = packetID;
	}
	public int getRes() {
		return res;
	}
	public void setRes(int res) {
		this.res = res;
	}

}
