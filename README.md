# Http Multipart Engine Java-Android
Il presente documento README e' un estratto dall'articolo: http://pierprogramm.altervista.org/wordpress/libreria-java-richieste-http-multipart-httpmultipartengine/

Questa libreria è pensata per applicazioni android, ma è applicabile anche a progetti java generici, in quanto non utilizza librerie android native. In fase di test infatti, l’ho provata anche come applicativo java e si è comportata bene, l’unica accortezza è settare l’header User-Agent, altrimenti il server rigetterà la richiesta, ho implementato un metodo ad hoc al proposito.

In android, il modo migliore per eseguire una richiesta http (naturalmente asincronicamente, android non le permette nel main thread) è estendere la preesistente classe AsyncTask. Vi consiglio di leggere la documentazione e utilizzare AsyncTask al meglio per i vostri progetti, è sicuramente molto meglio usare lo standard invece di implementare da zero una classe per effettuare richieste asincrone.

La libreria incapsula il protocollo http multipart, la sintassi degli header delle richieste, l’aggiunta di part, la divisione del file in parti (detti chunks) in modo da avere un feedback sull’invio.
 
