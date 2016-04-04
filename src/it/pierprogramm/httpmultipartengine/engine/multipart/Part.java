package it.pierprogramm.httpmultipartengine.engine.multipart;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parte generica, classe astratta.
 * @author <a href="http://pierprogramm.altervista.org">pierprogramm</a>
 */
public abstract class Part{
    protected static final boolean DEBUG = true;
    
    protected ProgressListener listener = null;
    protected List<String> headers;
    protected int contentLength = 0;

    protected boolean cancel = false;

    public Part(ProgressListener listener){
        this.listener = listener;
        headers = new ArrayList<>();
    }

    public Part() {
        headers = new ArrayList<>();
    }

    /**
     * Setta il listener
     */
    public void setProgressListener(ProgressListener listener){
        this.listener = listener;
    }

    /**
     * Aggiunta di un header alla richiesta http. Da chiamare prima di aggiungerlo
     * come part al MultipartRequest
     * Es.
     *     Content-Disposition: form-data; name="nome_allegato";filename="nome_file"
     *     da dare in input in input
     */
    public void addHeader(String fullHeader){
        headers.add(fullHeader + MultipartRequest.CRLF);
        incrementContentLength(fullHeader.getBytes().length + MultipartRequest.CRLF.getBytes().length);
    }

    /**
     * Incrementa la dimensione totale del part
     */
    protected void incrementContentLength(int increment){
//        Log.d(TAG, "incrementContentLength: increment=" + increment);
        contentLength += increment;
    }

    /**
     * Restituisce la dimensione totale del part finora calcolata
     */
    public int getContentLength(){
        return contentLength;
    }

    /**
     * Controlla se l'invio &egrave stato annullato
     */
    public boolean isCancelled(){
        return cancel;
    }

    /**
     * Annulla l'invio
     */
    public void cancel(){
        cancel = true;
    }

    public abstract void setup(DataOutputStream request) throws IOException;
    public abstract long getDataLength();

}
