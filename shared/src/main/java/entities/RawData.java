package entities;

import java.io.Serializable;
import java.util.Objects;

/**
 * Helper class for parsing and carrying ticket-related payloads.
 * Used by client and server code for UDP/JMS communication.
 */
public class RawData implements Serializable {

    private static final long serialVersionUID = 1L;

    private String requestType;
    private String clientId;
    private int totalSize;
    private int packetSize;
    private int totalChunks;
    private int chunkNo;
    private String data;

    public RawData() {
        // default
    }

    public RawData(String requestType, String clientId, int totalSize, int packetSize,
                   int totalChunks, int chunkNo, String data) {
        this.requestType = normalize(requestType);
        this.clientId = normalize(clientId);
        this.totalSize = nonNegative(totalSize);
        this.packetSize = nonNegative(packetSize);
        this.totalChunks = Math.max(0, totalChunks);
        this.chunkNo = Math.max(0, chunkNo);
        this.data = data;
    }

    private static String normalize(String s) {
        return s == null ? null : s.trim();
    }

    private static int nonNegative(int v) {
        return v < 0 ? 0 : v;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = normalize(requestType);
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = normalize(clientId);
    }

    public int getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = nonNegative(totalSize);
    }

    public int getPacketSize() {
        return packetSize;
    }

    public void setPacketSize(int packetSize) {
        this.packetSize = nonNegative(packetSize);
    }

    public int getTotalChunks() {
        return totalChunks;
    }

    public void setTotalChunks(int totalChunks) {
        this.totalChunks = Math.max(0, totalChunks);
    }

    public int getChunkNo() {
        return chunkNo;
    }

    public void setChunkNo(int chunkNo) {
        this.chunkNo = Math.max(0, chunkNo);
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    /**
     * Basic validation helper.
     */
    public boolean isValid() {
        return requestType != null && !requestType.isEmpty()
                && clientId != null && !clientId.isEmpty()
                && (totalSize >= 0) && (packetSize >= 0)
                && chunkNo >= 0 && totalChunks >= 0;
    }

    @Override
    public String toString() {
        return "RawData{" +
                "requestType='" + requestType + '\'' +
                ", clientId='" + clientId + '\'' +
                ", totalSize=" + totalSize +
                ", packetSize=" + packetSize +
                ", totalChunks=" + totalChunks +
                ", chunkNo=" + chunkNo +
                ", dataLength=" + (data == null ? 0 : data.length()) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RawData)) return false;
        RawData rawData = (RawData) o;
        return totalSize == rawData.totalSize &&
                packetSize == rawData.packetSize &&
                totalChunks == rawData.totalChunks &&
                chunkNo == rawData.chunkNo &&
                Objects.equals(requestType, rawData.requestType) &&
                Objects.equals(clientId, rawData.clientId) &&
                Objects.equals(data, rawData.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestType, clientId, totalSize, packetSize, totalChunks, chunkNo, data);
    }
}
