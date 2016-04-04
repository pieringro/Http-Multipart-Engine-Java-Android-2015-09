package it.pierprogramm.httpmultipartengine.engine;

import java.util.Map;

/**
 * Oggetto che incapsula i dati di una richiesta
 * @author <a href="http://pierprogramm.altervista.org">pierprogramm</a>
 */
public class RequestObject {

    private int requestId;
    private String url;
    private Map<String, Object> paramGet;
    private Map<String, Object> paramPost;
    private String filesMulti;
    private boolean cachable = false;

    public RequestObject(int requestId, String url){
        this.requestId = requestId;
        this.url = url;
    }


    public int getRequestId() {
        return requestId;
    }



    public String getUrl() {
        return url;
    }



    public Map<String, Object> getParamGet() {
        return paramGet;
    }

    public void setParamGet(Map<String, Object> paramGet) {
        this.paramGet = paramGet;
    }

    public Map<String, Object> getParamPost() {
        return paramPost;
    }

    public void setParamPost(Map<String, Object> paramPost) {
        this.paramPost = paramPost;
    }

    public String getFilesMulti() {
        return filesMulti;
    }

    public void setFilesMulti(String filesMulti) {
        this.filesMulti = filesMulti;
    }

    public boolean isCachable() {
        return cachable;
    }

    public void setCachable(boolean cachable) {
        this.cachable = cachable;
    }
}
