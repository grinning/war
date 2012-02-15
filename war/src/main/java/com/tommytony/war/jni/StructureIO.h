/* 
 * File:   StructureIO.h
 * Author: grinning
 *
 * Created on February 15, 2012, 12:55 AM
 */

#include<jni.h>

#ifndef _Included_com_tommytony_war_jni_StructureIO
#define	_Included_com_tommytony_war_jni_StructureIO

#ifdef	__cplusplus
extern "C" {
#endif

//Class: com_tommytony_war_jni_StructureIO
//Method: makeFiles
//Signature: void
//Params: 2 strings

    JNIEXPORT void JNICALL Java_com_tommytony_war_jni_StructureIO_callJavaMethod
    (JNIEnv *, jobject, jstring, jstring);
    

#ifdef	__cplusplus
}
#endif

#endif	/* STRUCTUREIO_H */

