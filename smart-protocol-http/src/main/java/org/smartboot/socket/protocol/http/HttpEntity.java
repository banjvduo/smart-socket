package org.smartboot.socket.protocol.http;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.math.NumberUtils;
import org.smartboot.socket.extension.decoder.DelimiterFrameDecoder;
import org.smartboot.socket.extension.decoder.FixedLengthFrameDecoder;
import org.smartboot.socket.protocol.http.strategy.PostDecodeStrategy;
import org.smartboot.socket.transport.AioSession;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 三刀 on 2017/6/20.
 */
public class HttpEntity {

    public static final String AUTHORIZATION = "Authorization";
    public static final String CACHE_CONTROL = "Cache-Control";
    public static final String CONTENT_DISPOSITION = "Content-Disposition";
    public static final String CONTENT_ENCODING = "Content-Encoding";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_MD5 = "Content-MD5";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String TRANSFER_ENCODING = "Transfer-Encoding";
    public static final String DATE = "Date";
    public static final String ETAG = "ETag";
    public static final String EXPIRES = "Expires";
    public static final String HOST = "Host";
    public static final String LAST_MODIFIED = "Last-Modified";
    public static final String RANGE = "Range";
    public static final String LOCATION = "Location";
    public static final String CONNECTION = "Connection";
    public DelimiterFrameDecoder headDelimiterFrameDecoder = new DelimiterFrameDecoder("\r\n\r\n".getBytes(), 128);
    public FixedLengthFrameDecoder bodyContentDecoder;
//    public StreamFrameDecoder smartHttpInputStream = new StreamFrameDecoder();
    /**
     * 0:消息头
     * 1:消息体
     * 2:结束
     */
    HttpPart partFlag = HttpPart.HEAD;
    PostDecodeStrategy postDecodeStrategy;
    private int contentLength = -1;
    private String method, url, protocol, contentType, decodeError;
    private Map<String, String> headMap = new HashMap<String, String>();
    private Map<String, String> paramMap = new HashMap<String, String>();

    public HttpEntity(AioSession<HttpEntity> session) {
    }

    public void decodeHead() {
        ByteBuffer headBuffer = headDelimiterFrameDecoder.getBuffer();
        String[] headDatas = StringUtils.split(new String(headBuffer.array(), headBuffer.position(), headBuffer.remaining()), "\r\n");
        if (ArrayUtils.isEmpty(headDatas)) {
            throw new RuntimeException("解码异常");
        }
        //请求行解码
        String[] requestLineData = StringUtils.split(headDatas[0], " ");
        if (ArrayUtils.getLength(requestLineData) != 3) {
            throw new RuntimeException("请求行解码异常");
        }
        method = requestLineData[0];
        url = requestLineData[1];
        protocol = requestLineData[2];

        for (int i = 1; i < headDatas.length; i++) {
            String[] lineDatas = StringUtils.split(headDatas[i], ":");
            setHeader(lineDatas[0].trim(), lineDatas[1].trim());
        }
        contentType = headMap.get(CONTENT_TYPE);
        contentLength = NumberUtils.toInt(headMap.get(CONTENT_LENGTH), -1);
    }

//    public InputStream getInputStream() {
//        return smartHttpInputStream.getInputStream();
//    }

    public void setHeader(String name, String value) {
        headMap.put(name, value);
    }

    public Map<String, String> getHeadMap() {
        return headMap;
    }


    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getDecodeError() {
        return decodeError;
    }

    public void setDecodeError(String decodeError) {
        this.decodeError = decodeError;
    }

    public String getContentType() {
        return contentType;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public Map<String, String> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, String> paramMap) {
        this.paramMap = paramMap;
    }
}