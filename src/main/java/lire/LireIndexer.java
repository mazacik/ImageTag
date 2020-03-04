/*
 * This file is part of the LIRE misc.project: http://lire-project.net
 * LIRE is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * LIRE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LIRE; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * We kindly ask you to refer the any or one of the following publications in
 * any publication mentioning or employing Lire:
 *
 * Lux Mathias, Savvas A. Chatzichristofis. Lire: Lucene Image Retrieval –
 * An Extensible Java CBIR Library. In proceedings of the 16th ACM International
 * Conference on Multimedia, pp. 1085-1088, Vancouver, Canada, 2008
 * URL: http://doi.acm.org/10.1145/1459359.1459577
 *
 * Lux Mathias. Content Based Image Retrieval with LIRE. In proceedings of the
 * 19th ACM International Conference on Multimedia, pp. 735-738, Scottsdale,
 * Arizona, USA, 2011
 * URL: http://dl.acm.org/citation.cfm?id=2072432
 *
 * Mathias Lux, Oge Marques. Visual Information Retrieval using Java and LIRE
 * Morgan & Claypool, 2013
 * URL: http://www.morganclaypool.com/doi/abs/10.2200/S00468ED1V01Y201301ICR025
 *
 * Copyright statement:
 * ====================
 * (c) 2002-2013 by Mathias Lux (mathias@juggle.at)
 *  http://www.semanticmetadata.net/lire, http://www.lire-project.net
 *
 * Updated: 21.04.13 08:13
 */

/*
 * This file has been modified.
 */

package lire;

import base.CustomList;
import misc.FileUtil;
import net.semanticmetadata.lire.builders.GlobalDocumentBuilder;
import net.semanticmetadata.lire.imageanalysis.features.global.AutoColorCorrelogram;
import net.semanticmetadata.lire.imageanalysis.features.global.CEDD;
import net.semanticmetadata.lire.imageanalysis.features.global.FCTH;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class LireIndexer {
	public LireIndexer(File directory) {
		CustomList<File> files = FileUtil.getSupportedFiles(directory);
		CustomList<String> images = new CustomList<>();
		files.forEach(file -> {
			try {
				images.add(file.getCanonicalPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		
		GlobalDocumentBuilder globalDocumentBuilder = new GlobalDocumentBuilder(false, GlobalDocumentBuilder.HashingMode.BitSampling, true);
		globalDocumentBuilder.addExtractor(CEDD.class);
		globalDocumentBuilder.addExtractor(FCTH.class);
		globalDocumentBuilder.addExtractor(AutoColorCorrelogram.class);
		
		IndexWriterConfig conf = new IndexWriterConfig(new StandardAnalyzer());
		conf.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
		IndexWriter iw = null;
		try {
			iw = new IndexWriter(FSDirectory.open(Paths.get(LireUtil.LIRE_INDEX_PATH)), conf);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (String imageFilePath : images) {
			Logger.getGlobal().info((images.indexOf(imageFilePath) + 1) + "/" + images.size() + "\tIndexing " + imageFilePath);
			try {
				BufferedImage img = ImageIO.read(new FileInputStream(imageFilePath));
				Document document = globalDocumentBuilder.createDocument(img, imageFilePath);
				iw.addDocument(document);
			} catch (Exception e) {
				Logger.getGlobal().info("Error reading the image or indexing it.");
				e.printStackTrace();
			}
		}
		try {
			iw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
