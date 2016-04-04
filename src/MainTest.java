import java.io.File;
import java.net.MalformedURLException;

import it.pierprogramm.httpmultipartengine.engine.multipart.FilePart;
import it.pierprogramm.httpmultipartengine.engine.multipart.MultipartRequest;
import it.pierprogramm.httpmultipartengine.engine.multipart.Part;
import it.pierprogramm.httpmultipartengine.engine.multipart.ProgressListener;
import it.pierprogramm.httpmultipartengine.engine.multipart.StringPart;
import it.pierprogramm.httpmultipartengine.exception.HttpHeaderException;


public class MainTest {

	public static void main(String[] args) throws MalformedURLException {
		String url = "http://pierprogramm.altervista.org/whathappened/view/api/newPost.php";
		
		MultipartRequest multipartRequest = new MultipartRequest(url, "POST");
		
		String filePath = "/home/pierprogramm/Scrivania/test_da_inviare.txt";
		Part filePart = new FilePart("file", new File(filePath));
		Part stringPart = new StringPart("data", 
			"{"
					+ "\"lat\" : 10.123321,"
					+ "\"lng\" : 40.123321,"
					+ "\"accuracy\" : 100,"
					+ "\"title\" : \"Titolo del post\","
					+ "\"description\" : \"Descrizione del post\","
					+ "\"timestamp\" : \"Y-m-d_H:i:s\""
			+ "}");
		
		multipartRequest.addPart(filePart);
		multipartRequest.addPart(stringPart);
		
		final long totalDataLength = multipartRequest.getTotalDataByteLength();
		multipartRequest.setListener(new ProgressListener() {
		    @Override
		    public void progress(long percentSent) {
		        System.out.println("total byte=" + totalDataLength + ", " +
		                "percentSent=" + percentSent);
		    }
		});
		
		try {
			multipartRequest.setHeader(
					"User-Agent", 
					"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
		} catch (HttpHeaderException e) {
			e.printStackTrace();
		}
		multipartRequest.setup();
		
		
		
		
		String result = multipartRequest.executeRequest();
		
		System.out.println("result="+result);

	}

}
