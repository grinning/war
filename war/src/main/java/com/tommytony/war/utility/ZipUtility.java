package com.tommytony.war.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtility {
	
	private final String archiveName;
	private final String archiveDir;
	private final ArrayList<String> files;

	public ZipUtility(String archiveName, String archiveDir) {
		this.archiveName = archiveName;
		this.archiveDir = archiveDir;
		this.files = new ArrayList<String>();
		this.getFiles(new File(archiveDir));
	}
	
	public void zip() {
		byte[] buffer = new byte[4096]; //4kb
		try {
			ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(archiveName));
			for(String f : this.files) {
				ZipEntry ze = new ZipEntry(f);
				zout.putNextEntry(ze);
				FileInputStream in = new FileInputStream(archiveDir + File.separator + f);
				int len;
				while((len = in.read(buffer)) > 0) {
					zout.write(buffer, 0, len);
				}
				
				in.close();
			}
			
			zout.closeEntry();
			zout.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getFiles(File f) {
		if(f.isFile()) {
			this.files.add(stripPath(f.getAbsoluteFile().toString()));
		} else if(f.isDirectory()) {
			String[] ls = f.list();
			for(String name : ls) {
				getFiles(new File(f, name));
			}
		}
	}
	
	private String stripPath(String path) {
		return path.substring(archiveDir.length() + 1, path.length());
	}
	
	public String getArchivePath() {
		return this.archiveName;
	}
}
