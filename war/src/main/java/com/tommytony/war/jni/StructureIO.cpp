#include"StructureIO.h"
#include<iostream>
#include<fstream>
#include<sys/stat.h>
#include<sys/types.h>
#include<string>



JNIEXPORT void JNICALL Java_com_tommytony_war_jni_StructureIO_callJavaMethod (JNIEnv * env,
        jobject obj, jstring path, jstring sep)
{
    
    jclass StructureIO = env->GetObjectClass(obj);
    jmethodID method = env->GetMethodID(StructureIO, "makeFiles", "()V");
    env->CallVoidMethod(obj, method);
    
    std::ofstream spawnStream;
    std::ofstream monStream;
    std::ofstream flagStream;
    std::ofstream bombStream;
    std::ofstream cakeStream;
    std::string pathSpawnDir;
    std::string pathMonDir;
    std::string pathFlagDir;
    std::string pathBombDir;
    std::string pathCakeDir;
    
    const jstring seper;
    seper = sep;
    
    const jbyte *str;
    str = (*env)->GetStringUTFChars(env, path, NULL);
    if(str == NULL) {
        return; //out of memory error thrown
    }
    
    pathSpawnDir = path + sep + "spawn";
    pathMonDir = path + sep + "monument";
    pathFlagDir = path + sep + "flag";
    pathBombDir = path + sep + "bomb";
    pathCakeDir = path + sep + "cake";
    
    mkdir(pathSpawnDir, 0777);
    mkdir(pathMonDir, 0777);
    mkdir(pathFlagDir, 0777);
    mkdir(pathBombDir, 0777);
    mkdir(pathCakeDir, 0777);
    //will add more to these later
    
    spawnStream.open(pathSpawnDir + sep + "tiny.dat");
    spawnStream << "1;1;1;0;"  << std::endl;
    spawnStream << "35,0" << std::endl;
    spawnStream.close()
    
    spawnStream.open(pathSpawnDir + sep + "small.dat");
    spawnStream << "spawn" << std::endl;
    spawnStream.close();
    
    spawnStream.open(pathSpawnDir + sep + "medium.dat");
    spawnStream << "spawn" << std::endl;
    spawnStream.close();
    
    spawnStream.open(pathSpawnDir + sep + "large.dat");
    spawnStream << "spawn" << std::endl;
    spawnStream.close();
    
    monStream.open(pathMonDir + sep + "reg.dat");
    monStream << "monument" << std::endl;
    monStream.close();
    
    monStream.open(pathMonDir + sep + "alt.dat");
    monStream << "monument" << std::endl;
    monStream.close();
    
    flagStream.open(pathFlagDir + sep + "reg.dat");
    flagStream << "flag" << std::endl;
    flagStream.close();
    
    flagStream.open(pathFlagDir + sep + "alt.dat");
    flagStream << "flag" << std::endl;
    flagStream.close();
    
    bombStream.open(pathBombDir + sep + "reg.dat");
    bombStream << "bomb" << std::endl;
    bombStream.close();
    
    bombStream.open(pathBombDir + sep + "alt.dat");
    bombStream << "bomb" << std::endl;
    bombStream.close();
    
    cakeStream.open(pathCakeDir + sep + "reg.dat");
    cakeStream << "cake" << std::endl;
    cakeStream.close();
    
    cakeStream.open(pathCakeDir + sep + "alt.dat");
    cakeStream << "cake" << std::endl;
    cakeStream.close();
    //more needs to be added like complete file data writing.
}


