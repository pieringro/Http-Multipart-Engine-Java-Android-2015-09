package it.pierprogramm.httpmultipartengine.engine.multipart;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Part della richiesta multipart, dato file
 * @author <a href="http://pierprogramm.altervista.org">pierprogramm</a>
 */
public class FilePart extends Part {
    private static final String TAG = FilePart.class.getName();
    
    /**
     * Dimensione massima che pu&ograve avere il buffer di ogni chunk
     */
    private static final int maxBufferSizeChunks = 102400;

    private File file;
    private String attachmentName;
    private String attachmentFileName;

    /**
     * Inizializza l'oggetto con nome del part file e l'oggetto file
     */
    public FilePart(String attachmentName, File file) {
        super();
        this.file = file;

        this.attachmentName = attachmentName;
        this.attachmentFileName = file.getName();


        addHeader("Content-Disposition: form-data; name=\"" + this.attachmentName + "\";" +
                "filename=\"" + this.attachmentFileName + "\"");

        countContentLength();
    }

    private void countContentLength() {
        incrementContentLength((int) file.length());
        incrementContentLength(MultipartRequest.CRLF.getBytes().length);
    }


    /**
     * Setup dell'invio della part file. Aggiunge i dati del file allo streaming in input,
     * divide il file in chunk per avere un feedback sull'invio.
     */
    @Override
    public void setup(DataOutputStream request) throws IOException {
        //scrivo gli headers gia' settati nella lista
        for (String header : headers) {
            request.writeBytes(header);
        }


        request.writeBytes(MultipartRequest.CRLF);

        if(DEBUG)
        	System.out.println(TAG+" - setup: ho finito di scrivere gli header. Inizio con i dati");

        putFileInStream(request);

        if(DEBUG)
        	System.out.println(TAG+" - setup: ho finito di scrivere i dati");

    }

    /**
     * Scrive il file nello stream in input, lo divide dapprima in chunks con dimensione massima
     * {@link #maxBufferSizeChunks}.
     * @throws IOException conseguenza dell'utilizzo di uno stream 
     */
    private void putFileInStream(DataOutputStream outputStream) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);

        //la dimensione massima che puo' avere il buffer e' incapsulata nella costante maxBufferSizeChunks

        //byte da leggere
        int bytesAvailable = fileInputStream.available();
        //reale dimensione del buffer (il minimo tra i byte da leggere e il max possibile)
        int bufferSize = Math.min(bytesAvailable, maxBufferSizeChunks);
        byte[] buffer = new byte[bufferSize];//init buffer

        //riempio il buffer e byteRead=byte fin qui letti
        int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        
        if(DEBUG)
        	System.out.println(TAG + " - putFileInStream: Letti i primi " + bytesRead + " byte.");

        while (bytesRead > 0 && !isCancelled()) {
            if(isCancelled()){
            	fileInputStream.close();
                outputStream.close();
                return;
            }

            outputStream.write(buffer, 0, bufferSize);
//            Log.d(TAG, "Letti e inviati " + bytesRead + " byte.");
            bytesAvailable = fileInputStream.available();
//            Log.d(TAG, "Ancora da leggere: " + bytesAvailable + " byte.");
            if (listener != null) {
                listener.progress(bytesAvailable);
            }

            bufferSize = Math.min(bytesAvailable, maxBufferSizeChunks);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

        }
        
        fileInputStream.close();
    }


    /**
     * Restituisce la lunghezza in byte del file
     */
    @Override
    public long getDataLength() {
        return file.length();
    }

}
