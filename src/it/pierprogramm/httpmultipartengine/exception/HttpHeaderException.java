package it.pierprogramm.httpmultipartengine.exception;

/**
 * Eccezione che si riferisce all'oggetto HttpURLConnection, incapsula dei messaggi di 
 * errore in costanti pubbliche
 */
public class HttpHeaderException extends Exception {
	private static final long serialVersionUID = 1L;
	
	/**
	 * L'oggetto HttpURLConnection non &egrave stato settato
	 */
	public static final String HTTPURLCONNECTION_NOT_SET = 
			"HttpURLConnection not set. Make sure you have called setup() before";
	
	/**
	 * L'oggetto HttpURLConnection &egrave stato gi&agrave stato aperto
	 */
	public static final String HTTPURLCONNECTION_ALREADY_OPEN = 
			"HttpURLConnection is already open. Call this method before setup().";
	
	

	public HttpHeaderException(String msg){
		super(msg);
	}
	
}
