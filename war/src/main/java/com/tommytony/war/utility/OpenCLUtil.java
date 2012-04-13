package com.tommytony.war.utility;

import java.nio.DoubleBuffer;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import org.lwjgl.LWJGLException;
import org.lwjgl.opencl.CL;
import org.lwjgl.opencl.CL10;
import org.lwjgl.opencl.CLCommandQueue;
import org.lwjgl.opencl.CLContext;
import org.lwjgl.opencl.CLDevice;
import org.lwjgl.opencl.CLMem;
import org.lwjgl.opencl.CLPlatform;

public class OpenCLUtil {

	/**
	 * @author grinning
	 * 
	 */
	
	//main stuff
	private CLContext context;
	private CLDevice gpu;
	private CLPlatform platform;
	private CLCommandQueue clCommands;
	
	//memory
	/*<Buffers>*/
	private DoubleBuffer a;
	private DoubleBuffer b;
	/*</Buffers>*/
	private CLMem aBuf;
	private CLMem bBuf;
	
	//constants
	public static final byte WAR_CL_A_BUFFER = 0x1E; //29
	public static final byte WAR_CL_B_BUFFER = 0x1F; //30
	
	/*<CL Functions>*/
	final String addFunc =
			"kernel void "
			+ "sum(global const double *a, "
			+ "global const double *b, "
			+ "global double *answer) { "
			+ "unsigned int vec = get_global_id(0); "
			+ "answer[vec] = a[vec] + b[vec];" 
			+ "}";
	
	final String lookAtFuncYaw =
			"kernel void "
			+ "lookAtYaw(global const double *a, "
			+ "global const double *b, "
			+ "global double *answer) { "
		    + "unsigned int vec = get_global_id(0); "
			+ "answer[vec] = acos((a[vec] / b[vec]) * 180 / " +
			"3.1415926); }";
	
	final String lookAtFuncPitch = 
			"kernel void "
			+ "lookAtPitch(global const double *a, " +
			"global const double *b, " +
			"global double *answer) { " +
			"unsigned int vec = get_global_id(0); " +
			"answer[vec] = acos((a[vec] / b[vec]) * 180 / " +
			"3.1415926 - 90); }";
	/*</CL Functions>*/
	
	public OpenCLUtil() {
		//init openCL and get devices
				try {
					//init
					CL.create();
					platform = CLPlatform.getPlatforms().get(0);
					gpu = platform.getDevices(CL10.CL_DEVICE_TYPE_GPU).get(0);
					context = CLContext.create(platform, platform.getDevices(CL10.CL_DEVICE_TYPE_GPU), null);
					clCommands = CL10.clCreateCommandQueue(context, gpu, CL10.CL_QUEUE_PROFILING_ENABLE, null);
				    //allocation
					aBuf = CL10.clCreateBuffer(context, CL10.CL_MEM_READ_ONLY | CL10.CL_MEM_COPY_HOST_PTR, a, null);
					bBuf = CL10.clCreateBuffer(context, CL10.CL_MEM_READ_ONLY | CL10.CL_MEM_COPY_HOST_PTR, b, null);
					CL10.clEnqueueWriteBuffer(clCommands , aBuf, 1, 0, a, null, null);
					CL10.clEnqueueWriteBuffer(clCommands, bBuf, 1, 0, b, null, null);
					CL10.clFinish(clCommands);
				} catch (LWJGLException e) {
					Bukkit.getServer().getLogger().log(Level.WARNING, "War> Dependency error!");
				}
	}
	
	public void cleanUp() {
		CL10.clReleaseCommandQueue(clCommands);
		CL10.clReleaseContext(context);
		CL.destroy();
	}
	
	public void setBuffer(double[] array, byte type) {
		if(type == OpenCLUtil.WAR_CL_A_BUFFER) {
			this.a = DoubleBuffer.wrap(array);
		} else if(type == OpenCLUtil.WAR_CL_B_BUFFER) {
			this.b = DoubleBuffer.wrap(array);
		} else {
			throw new NullPointerException();
		}
	}
	
	
}

    
