#include"StructureIO.h"
#include<cstring>
#include<iostream>
#include<fstream>
#include<sys/stat.h>
#include<sys/types.h>
#include<string>

using namespace std;


JNIEXPORT void JNICALL Java_com_tommytony_war_jni_StructureIO_callJavaMethod (JNIEnv * env,
        jobject obj, jstring path, jstring sep)
{
    //I know the path is 22 characters, I will use a 28 byte buffer just in case
    string absolutePath, fileSep;
    int len = (*env)->GetStringLength(env, path);
    int len1 = (*env)->GetStringLength(env, sep);
    
    jclass StructureIO = env->GetObjectClass(obj);
}


