package it.pierprogramm.httpmultipartengine.engine;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import it.pierprogramm.httpmultipartengine.engine.multipart.ProgressListener;
import it.pierprogramm.httpmultipartengine.exception.HttpHeaderException;


/**
 * Incapsula le funzionalit&agrave comuni alle classi che effettuano una richiesta http
 * @author <a href="http://pierprogramm.altervista.org">pierprogramm</a>
 */
public abstract class Request {
    private static final String TAG = Request.class.getName();
    protected static final boolean DEBUG = true;
    
    /**
     * Parola chiave che verr&agrave restituita in caso di annullamento dell'invio.
     */
    public static final String CANCEL_KEYWORD = "cancel";
    
    
    protected URL url;
    protected HttpURLConnection httpConnection;
    protected String method;
    protected Map<String, String> customHeaders = new HashMap<>();

    protected boolean cancel = false;

    
    /**
     * Setta il metodo di invio, GET o POST.
     */
    public final void setMethod(String method){
        this.method = method;
    }
    
    
    
    /**
     * Aggiunge un header alla richiesta, questo metodo scende nel dettaglio
     * della richiesta settando direttamente un header. Viene un po' meno
     * l'information hiding ma si garantisce maggiore supporto per richieste pi&ugrave
     * complesse. Da utilizzare con attenzione, il settaggio di un header sbagliato
     * comporter&agrave l'impossibilit&agrave di invio.<br>
     * <b>N.B.</b>: Deve essere chiamato prima del {@link #setup()}
     * @param header l'header della richiesta, es. User-Agent
     * @param value il valore dell'header, es. Mozilla/5.0
     * @throws HttpHeaderException se la connessione &egrave gi&agrave aperta 
     * (setup gi&agrave eseguito)
     */
    public void setHeader(String header, String value) throws HttpHeaderException{
    	if(httpConnection!=null){
    		throw new HttpHeaderException(HttpHeaderException.HTTPURLCONNECTION_ALREADY_OPEN);
    	}
    	customHeaders.put(header, value);
    }
    
    
    
    /**
     * Setta l'user-agent della richiesta
     * @param value il valore dell'user-agent, es. Mozilla/5.0
     */
    public void setUserAgent(String value){
    	customHeaders.put("User-Agent", value);
    }

    
    
    /**
     * Ottiene la risposta dallo streaming di lettura dal server e lo restituisce come
     * stringa, restituisce null se viene lanciata un'eccezione
     */
    public final String executeRequest() {
        if(cancel){
            return CANCEL_KEYWORD;
        }
        try {
            String response = getResponse();
            
            if(DEBUG)
            	System.out.println(TAG+" - setup: response="+response);
            
            httpConnection.disconnect();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private String getResponse() throws IOException {
        InputStream responseInputStream;

        int status = httpConnection.getResponseCode();

        if (status >= HttpStatus.SC_BAD_REQUEST) {
            responseInputStream = httpConnection.getErrorStream();
        }
        else {
            responseInputStream = httpConnection.getInputStream();
        }

        InputStream responseStream = new BufferedInputStream(responseInputStream);
        
        if(DEBUG)
        	System.out.println(TAG + 
        		" - getResponse: ottenuto BufferedInputStream da httpUrlConnection.getInputStream()");

        BufferedReader responseStreamReader = new BufferedReader(
                new InputStreamReader(responseStream));
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = responseStreamReader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        responseStreamReader.close();

        String response = stringBuilder.toString();
        responseStream.close();
        return response;
    }



    public abstract void setup();

    public abstract boolean setListener(ProgressListener listener);

    /**
     * Annulla l'invio in corso.
     */
    public abstract void cancel();
}
