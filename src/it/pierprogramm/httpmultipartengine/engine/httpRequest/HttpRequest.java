package it.pierprogramm.httpmultipartengine.engine.httpRequest;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import it.pierprogramm.httpmultipartengine.engine.Request;
import it.pierprogramm.httpmultipartengine.engine.multipart.ProgressListener;


/**
 * Richieste http semplici per ora &egrave implementato solo il metodo GET
 * @author <a href="http://pierprogramm.altervista.org">pierprogramm</a>
 */
public class HttpRequest extends Request {
    static final String TAG = HttpRequest.class.getName();

    private String urlStr;

    private Map<String, String> paramsMap;


    public HttpRequest(String url, String method) throws MalformedURLException {
        this.urlStr = url;
        this.method = method;
        this.paramsMap = new HashMap<>();
    }

    /**
     * Aggiunge un parametro alla mappa di parametri da inviare
     */
    public void addParams(String key, String value){
        String valueFixed = value.replace(" ", "+");
        this.paramsMap.put(key, valueFixed);
    }


    /**
     * Setup della richiesta, apre la connessione
     */
    @Override
    public void setup(){
        try {
            setupContent();
            setupRequest();
        } catch (IOException e){
            e.printStackTrace();
        }
    }


    /**
     * TODO Listener not implemented yet
     */
    @Override
    public boolean setListener(ProgressListener listener) {
        return false;
    }


    @Override
    public void cancel() {
        cancel = true;
    }


    private void setupRequest() throws IOException {
        httpConnection = null;
        httpConnection = (HttpURLConnection) url.openConnection();
        httpConnection.setRequestProperty("Accept-Charset", "UTF-8");

        //con chunked dovremmo poter settare la progress bar
        httpConnection.setChunkedStreamingMode(0);

        httpConnection.setRequestMethod(method);
    }


    private void setupContent() throws MalformedURLException  {
        String query = parsingParamsMapGETQuery();
        url = new URL(urlStr + "?" + query);
    }

    /**
     * Parsing della mappa completa chiave-valore dei parametri della richiesta,
     * inizializzata uno a uno con addParams
     */
    private String parsingParamsMapGETQuery() {
        String query = "";
        if(!this.paramsMap.isEmpty()) {
            if (method.equalsIgnoreCase("get")) {
                //parsing della mappa
                Object[] keys = paramsMap.keySet().toArray();
                for (int i = 0; i < paramsMap.keySet().size(); i++) {
                    String key = (String) keys[i];
                    String value = paramsMap.get(key);

                    query += key + "=" + value;
                    if (i != keys.length - 1) {
                        query += "&";
                    }
                }
                query = query.replace(" ", "+");
                if(DEBUG)
                	System.out.println(TAG + " - parsingParamsMapGETQuery GET: query=" + query);

            } else if (method.equalsIgnoreCase("post")) {
                //todo parsing mappa parametri per richiesta post
            } else {
                throw new RuntimeException("Error. unknow method " + method);
            }
        }
        return query;

    }
}
