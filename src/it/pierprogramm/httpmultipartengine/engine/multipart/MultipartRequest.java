package it.pierprogramm.httpmultipartengine.engine.multipart;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.pierprogramm.httpmultipartengine.engine.Request;

/**
 * Esegue richieste multipart complesse.<br>
 * Esempio di utilizzo.
 * <pre language="java">
 * --- inizializzo la richiesta
 * MultipartRequest multipartRequest = new MultipartRequest(url, "POST");
 * 
 * --- inizializzo le part
 * Part filePart = new FilePart("file", new File("path/to/file"));
 * Part stringPart = new StringPart("data", "stringa da inviare");
 * 
 * --- aggiungo le part alla richiesta
 * multipartRequest.addPart(filePart);
 * multipartRequest.addPart(stringPart);
 * 
 * --- inizializzo il listener sulla richiesta
 * final long totalDataLength = multipartRequest.getTotalDataByteLength();
 * multipartRequest.setListener(new ProgressListener() {
 * 
 * 		public void progress(long percentSent) {
 * 			System.out.println("total byte=" + totalDataLength + ", " +
 * 				"percentSent=" + percentSent); 
 * 		} 
 * });
 * 
 * --- setto l'header dell'user agent (nel caso si stia usando android, non &egrave necessario)
 * multipartRequest.setUserAgent("Mozilla/5.0");
 * 
 * --- setup della richiesta
 * multipartRequest.setup();
 * 
 * --- ottengo la risposta del server
 * String result = multipartRequest.executeRequest();
 * 
 * --- banalmente stampo la risposta
 * System.out.println("result="+result);
 * </pre>
 * @author <a href="http://pierprogramm.altervista.org">pierprogramm</a>
 */
public class MultipartRequest extends Request {
	static final String TAG = MultipartRequest.class.getName();

	/**
	 * Separatore nel protocollo http, usato nel package
	 */
	protected static final String CRLF = "\r\n";
	// ------- costanti private per il protocollo http -----------------
	static final String TWO_HYPHENS = "--";
	static final String BOUNDARY = "*****";
	static final String FIRST_BOUNDARY = TWO_HYPHENS + BOUNDARY + CRLF;
	static final String NORMAL_BOUNDARY = CRLF + TWO_HYPHENS + BOUNDARY + CRLF;
	static final String LAST_BOUNDARY = CRLF + TWO_HYPHENS + BOUNDARY
			+ TWO_HYPHENS + CRLF;
	// ------------------------------------------------------
	private List<Part> partList;

	private long totalDataByteLength = 0;
	private long progressByteSent = 0;

	/**
	 * Inizializza l'oggetto con url e metodo
	 * 
	 * @throws MalformedURLException
	 *             dovuta all'inizializzazione dell'oggetto URL, lanciata nel
	 *             caso di url impossibile da parserizzare
	 */
	public MultipartRequest(String url, String method)
			throws MalformedURLException {
		this.url = new URL(url);
		this.setMethod(method);
		this.partList = new ArrayList<>();
	}

	/**
	 * Aggiunge una part alla lista di parti da inviare
	 * 
	 * @param part l'oggetto Part generico che viene aggiunto
	 */
	public void addPart(Part part) {
		final long dataLength = part.getDataLength();
		this.totalDataByteLength += dataLength;

		this.partList.add(part);
	}

	/**
	 * Setta i vari listener uno per ogni part. Il calcolo percentuale lo fara'
	 * sulla dimensione totale delle part. I listener chiamati dai part
	 * incrementeranno il valore di byte.
	 * 
	 * @param listener il listener esterno che verr&agrave chiamato 
	 * dai vari listener di ogni part
	 */
	@Override
	public boolean setListener(final ProgressListener listener) {
		if (this.partList.isEmpty()) {
			return false;
		}

		for (Part aPart : partList) {
			aPart.setProgressListener(new ProgressListener() {
				@Override
				public void progress(long byteLeft) {
					progressByteSent = totalDataByteLength - byteLeft;
					int percentSent = (int) (100 * progressByteSent / totalDataByteLength);

					System.out.println(TAG + " - progress: percentSent="
							+ percentSent + ", " + " progressByteSent="
							+ progressByteSent + ", " + " totalDataByteLength="
							+ totalDataByteLength);

					listener.progress(percentSent);
				}
			});
		}

		return true;
	}

	/**
	 * Setup della richiesta e delle parti. Apre la connessione http e ne setta
	 * i parametri, infine esegue l'invio inserendo dati allo streaming
	 */
	@Override
	public void setup() {
		try {
			setupRequest();
			setupContentWrapper();
			System.out.println(TAG + " - Fine setup");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Setup della richiesta, incapsula le propriet&agrave (header dell'http),
	 * apre la connessione http
	 */
	private void setupRequest() throws IOException {
		httpConnection = null;
		httpConnection = (HttpURLConnection) url.openConnection();

		// setFixedLengthStreamingMode e' disponibile con un long solo per
		// android nuovi (<=19)
		// mentre e' disponibile solo con un int per android vecchi
		// int contentLength = calculateContentLength();
		// httpUrlConnection.setFixedLengthStreamingMode(contentLength);
		// Log.d(TAG, "setupContentWrapper: contentLength settati=" +
		// contentLength);

		// Con fixed non funziona (EPIPE exception)
		// anche se la dimensione passata e' quella giusta (snifferata dal server una
		// richiesta andata a buon fine). Con chunked funziona, adesso lo streaming non viene
		// bufferizzato da HUC prima di inviarlo ma lo invia direttamente, in questo modo
		// possiamo settare la onHaveProgress bar.
		httpConnection.setChunkedStreamingMode(0);

		httpConnection.setUseCaches(false);
		httpConnection.setDoOutput(true);

		httpConnection.setRequestMethod(method);
		httpConnection.setRequestProperty("Connection", "close");
		httpConnection.setRequestProperty("Cache-Control", "no-cache");
		httpConnection.setRequestProperty("Content-Type",
				"multipart/form-data;boundary=" + BOUNDARY);

		if (!customHeaders.isEmpty()) {
			for (Map.Entry<String, String> item : customHeaders.entrySet()) {
				httpConnection.setRequestProperty(item.getKey(),
						item.getValue());
			}
		}

	}

	/**
	 * Setup delle parti, la definizione degli header viene gestita da ogni
	 * oggetto Part. Incapsula il protocollo http multipart.
	 */
	private void setupContentWrapper() throws IOException {

		DataOutputStream request = new DataOutputStream(
				httpConnection.getOutputStream());

		// first boundary
		request.writeBytes(FIRST_BOUNDARY);

		// aggiungo tutte le part
		for (int i = 0; i < partList.size(); i++) {
			Part aPart = partList.get(i);
			System.out.println(TAG + " - Sending part " + i + " ...");

			aPart.setup(request);

			if (cancel) {
				System.out.println(TAG + " - Cancelled");
				request.close();
				return;
			}

			if (i < partList.size() - 1) {// non e' l'ultimo
				request.writeBytes(NORMAL_BOUNDARY);
			}
		}

		// last boundary
		request.writeBytes(LAST_BOUNDARY);

		request.flush();

		request.close();
	}

	/**
	 * Restituisce i byte totali di tutte le part da inviare
	 */
	public long getTotalDataByteLength() {
		return totalDataByteLength;
	}

	/**
	 * Annulla l'invio
	 */
	@Override
	public void cancel() {
		for (Part aPart : partList) {
			aPart.cancel();
		}
		cancel = true;
	}

}
