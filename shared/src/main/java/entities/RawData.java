package entities;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Helper class for parsing and creating packet / payload metadata used by Client and Server.
 *
 * - Fields use Java naming conventions and are private with accessors.
 * - Provides simple validation and convenience methods to get/set the payload as bytes.
 */
public class RawData implements Serializable {

    private static final long serialVersionUID = 1L;

    private String requestType;
    private String clientId;
    private int totalSize;
    private int thisPacketSize;
    private int totalChunk;
    private int chunkNo;
    private String data; // textual payload; use getDataBytes()/setDataFromBytes for binary

    public RawData() {
    }

    public RawData(String requestType, String clientId, int totalSize, int thisPacketSize,
                   int totalChunk, int chunkNo, String data) {
        this.requestType = requestType;
        this.clientId = clientId;
        this.totalSize = totalSize;
        this.thisPacketSize = thisPacketSize;
        this.totalChunk = totalChunk;
        this.chunkNo = chunkNo;
        this.data = data;
    }

    // Getters / setters

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public int getThisPacketSize() {
        return thisPacketSize;
    }

    public void setThisPacketSize(int thisPacketSize) {
        this.thisPacketSize = thisPacketSize;
    }

    public int getTotalChunk() {
        return totalChunk;
    }

    public void setTotalChunk(int totalChunk) {
        this.totalChunk = totalChunk;
    }

    public int getChunkNo() {
        return chunkNo;
    }

    public void setChunkNo(int chunkNo) {
        this.chunkNo = chunkNo;
    }

    /**
     * Textual payload. May be null.
     */
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    // Convenience helpers for binary payloads

    public byte[] getDataBytes() {
        return data == null ? new byte[0] : data.getBytes(StandardCharsets.UTF_8);
    }

    public void setDataFromBytes(byte[] bytes) {
        this.data = bytes == null ? null : new String(bytes, StandardCharsets.UTF_8);
        this.thisPacketSize = bytes == null ? 0 : bytes.length;
    }

    /**
     * Basic sanity checks for required fields and sizes.
     */
    public boolean isValid() {
        if (requestType == null || requestType.isEmpty()) return false;
        if (clientId == null || clientId.isEmpty()) return false;
        if (totalSize < 0 || thisPacketSize < 0 || totalChunk < 0 || chunkNo < 0) return false;
        if (thisPacketSize > totalSize) return false;
        return true;
    }

    @Override
    public String toString() {
        return "RawData{" +
                "requestType='" + requestType + '\'' +
                ", clientId='" + clientId + '\'' +
                ", totalSize=" + totalSize +
                ", thisPacketSize=" + thisPacketSize +
                ", totalChunk=" + totalChunk +
                ", chunkNo=" + chunkNo +
                ", dataLength=" + (data == null ? 0 : data.length()) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RawData rawData = (RawData) o;
        return totalSize == rawData.totalSize &&
                thisPacketSize == rawData.thisPacketSize &&
                totalChunk == rawData.totalChunk &&
                chunkNo == rawData.chunkNo &&
                Objects.equals(requestType, rawData.requestType) &&
                Objects.equals(clientId, rawData.clientId) &&
                Objects.equals(data, rawData.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestType, clientId, totalSize, thisPacketSize, totalChunk, chunkNo, data);
    }
}
