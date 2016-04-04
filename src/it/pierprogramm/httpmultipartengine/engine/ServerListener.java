package it.pierprogramm.httpmultipartengine.engine;

import java.util.Map;

/**
 * @author <a href="http://pierprogramm.altervista.org">pierprogramm</a>
 */
public interface ServerListener {

    //verra' avviato sul UI thread
    void onCancelled();

    void onRequestComplete(int requestId, Map<String, Object> data);

    void onRequestError(int requestId, Map<String, String> errorMsgs);

    //verra' chiamato nell'UI thread
    void onHaveProgress(int requestId, int percentSent);

}
