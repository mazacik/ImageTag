package server.misc;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.HttpClientBuilder;
import server.base.entity.Entity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}
}
