package com.shopme.admin;

import org.junit.jupiter.api.Test;

public class AmazonS3UtilTests {

	@Test
	public void testListFolder() {
		String folderName = "brands-logos/10";
		AmazonS3Util.listFolder(folderName);
	}
}

