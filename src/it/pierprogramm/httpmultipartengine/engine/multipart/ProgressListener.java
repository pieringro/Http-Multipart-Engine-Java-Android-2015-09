package it.pierprogramm.httpmultipartengine.engine.multipart;

/**
 * Listener che verr&agrave notificato a un aggiornamento di updload.
 * @author <a href="http://pierprogramm.altervista.org">pierprogramm</a>
 */
public interface ProgressListener {

    void progress(long byteLeft);

}
