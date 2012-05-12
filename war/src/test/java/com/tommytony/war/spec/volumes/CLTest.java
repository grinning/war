package com.tommytony.war.spec.volumes;

import org.junit.Test;

import com.tommytony.war.War;
import com.tommytony.war.utility.OpenCLUtil;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CLTest {

	@Test
	public void testSetup() {
		War.loadNativeLibraries();
		OpenCLUtil cl = new OpenCLUtil();
		cl.cleanUp();
		assertNull("Teste Success", cl);
	}
}
