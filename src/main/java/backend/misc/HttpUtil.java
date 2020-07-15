package backend.misc;

import backend.entity.Entity;
import frontend.stage.SimpleMessageStage;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.UnknownHostException;

public abstract class HttpUtil {
	public static void googleRIS(Entity entity) {
		new Thread(() -> {
			try {
				File file = new File(FileUtil.getFileEntity(entity));
				MultipartEntityBuilder meb = MultipartEntityBuilder.create();
				meb.addPart("encoded_image", new InputStreamBody(new FileInputStream(file), file.getName()));
				HttpPost post = new HttpPost("https://www.google.com/searchbyimage/upload");
				post.setEntity(meb.build());
				HttpResponse response = HttpClientBuilder.create().build().execute(post);
				String site = response.getFirstHeader("location").getValue();
				Runtime.getRuntime().exec("cmd /c start " + site);
			} catch (UnknownHostException e) {
				new SimpleMessageStage("Error", "You are offline.");
				e.printStackTrace();
			} catch (IOException e) {
				new SimpleMessageStage("Error", "An error occured while Reverse Image Search.");
				e.printStackTrace();
			}
		}).start();
	}
}
