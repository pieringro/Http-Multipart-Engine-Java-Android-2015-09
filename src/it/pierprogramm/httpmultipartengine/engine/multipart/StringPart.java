package it.pierprogramm.httpmultipartengine.engine.multipart;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Part della richiesta multipart, dato stringa
 * @author <a href="http://pierprogramm.altervista.org">pierprogramm</a>
 */
public class StringPart extends Part {
    private static final String TAG = StringPart.class.getName();

    private String attachmentName;
    private String data;

    /**
     * Inizializza l'oggetto con il nome del part e la stringa da inviare
     */
    public StringPart(String attachmentName, String data) {
        super();
        this.attachmentName = attachmentName;
        this.data = data;
        headers = new ArrayList<>();

        addHeader("Content-Disposition: form-data; name=\"" + this.attachmentName + "\"");
    }




    /**
     * Setup della richiesta, scrive sullo stream di output
     */
    @Override
    public void setup(DataOutputStream request) throws IOException {
        //scrivo gli headers gia' settati nella lista
        for (String header : headers) {
            request.writeBytes(header);
            System.out.println(TAG + " - Scritto header=" + header);
        }

        if(DEBUG)
        	System.out.println(TAG + " - Fine invio header");

        request.writeBytes(MultipartRequest.CRLF);

        //todo feedback percentuale sull'invio
        request.writeBytes(data);

    }


    /**
     * Restituisce la dimensione totale in byte della stringa dei dati
     */
    @Override
    public long getDataLength() {
        return data.getBytes().length;
    }
}
