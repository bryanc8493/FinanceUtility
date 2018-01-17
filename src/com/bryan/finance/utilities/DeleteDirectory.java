package com.bryan.finance.utilities;

import java.io.File;

public class DeleteDirectory {

	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (String child : children) {
				boolean success = deleteDir(new File(dir, child));
				if (!success)
					return false;
			}
		}
		return dir.delete();
	}
}
