/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GplHeader {
	
	String header = "/* Copyright (c) 2013 National Research Institute for Earth Science and\r\n"+
			" * Disaster Prevention (NIED).\r\n"+
			" * This code is licensed under the GPL version 3 license, available at the root\r\n"+
			" * application directory.\r\n"+
			" */\r\n";
	protected String extDir[] = {"\\src\\main\\webapp\\js",
						"\\src\\main\\webapp\\images",
						"\\src\\main\\webapp\\css",
						"\\src\\main\\webapp\\admin-js",
						"\\src\\main\\webapp\\upload",
						"\\src\\main\\webapp\\WEB-INF\\classes",
						"\\.settings",
						"\\.svn",
						"\\doc",
						"\\lib",
						"\\repo",
						"\\licenses",
						};
	protected String extFile[] = {"thickbox", "jquery", "ui."};
	public String topdir = "C:\\work\\develop\\eclipse-kepler-sa\\workspace\\SaigaiTask2";
	
	public void addHeader(File file) 
	{
		if (file.isDirectory()) {
			//除外ディレクトリを検出
			for (String dir : extDir) {
				if (file.getPath().indexOf(dir) != -1)
					return;
			}
			
			//子ディレクトリへ
			File[] cdirs = file.listFiles();
			for (File f : cdirs)
				addHeader(f);
			return;
		}
		
		//除外ファイルを検出
		for (String fn : extFile) {
			if (file.getPath().indexOf(fn) != -1 && (file.getName().indexOf(".js") != -1 || file.getName().indexOf(".css") != -1))
				return;
		}
		
		FileReader fr = null;
		FileWriter fw = null;
		BufferedReader br = null;
		try {
		//ファイルの場合
		String regex = "\\b([A-Za-z_]\\w*)\\.(java|js|css|sql|jsp)\\b";
		if (file.getName().matches(regex)) {
			
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			StringBuffer buff = new StringBuffer();
			String l;
			int i = 0;
			while ((l = br.readLine()) != null) {
				//すでにライセンスがある
				if (i == 0 && l.indexOf("/* Copyright (c)") != -1) return;
				
				buff.append(l);
				buff.append("\r\n");
				i++;
			}
			
			fw = new FileWriter(file, false);
			if (file.getName().matches("\\b([A-Za-z_]\\w*)\\.(jsp)\\b"))
				fw.write("<%");
			fw.write(header);
			if (file.getName().matches("\\b([A-Za-z_]\\w*)\\.(jsp)\\b"))
				fw.write("%>\n");
			fw.write(buff.toString());
		}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) br.close();
				if (fr != null) fr.close();
			} catch (IOException e2) {e2.printStackTrace();}
			try {
				if (fw != null) fw.close();
			} catch (IOException e2) {e2.printStackTrace();}
		}
		
	}
	
	public static void main(String[] args) {
		
		GplHeader gh = new GplHeader();
		File dir = new File(gh.topdir);
		try {
			gh.addHeader(dir);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
