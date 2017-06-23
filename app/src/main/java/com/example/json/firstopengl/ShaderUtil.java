package com.example.json.firstopengl;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by Json on 2017/6/23.
 */

public class ShaderUtil {

    public static int loadShader(int shaderType,String source){

        int shader = GLES20.glCreateShader(shaderType);//创建一个Shader
        //如果shader创建成功 则加载shader
        if (shader!=0){
            GLES20.glShaderSource(shader,source);//加载shader的源代码
            GLES20.glCompileShader(shader);//编译shader
            int [] complied = new int[1];//存放编译成功shader数量的数组

            GLES20.glGetShaderiv(shader,GLES20.GL_COMPILE_STATUS,complied,0);//获取shader的编译情况
            if (complied[0] ==0){
                //若编译失败则显示错误日志并删除此shader
                Log.e("ES20_ERROR", "Could not compile shader " + shaderType + ":");
                Log.e("ES20_ERROR", GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }

        return shader;
    }

    //创建shader程序的方法
    public static int  createProgram(String vertexSorce,String fragmentSource){
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,vertexSorce);
        if (vertexShader == 0){
            return 0;
        }
        //加载片元着色器
       int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER,fragmentSource);
        if (pixelShader == 0){
            return 0;
        }

        //创建程序
        int program = GLES20.glCreateProgram();
        if (program!=0){
            GLES20.glAttachShader(program,vertexShader);//向程序中加入顶点着色器
            checkGlError("glAttachShader");
            //向程序中加入片源着色器
            GLES20.glAttachShader(program,pixelShader);
            checkGlError("glAttachShader");
            //链接程序
            GLES20.glLinkProgram(program);
            //存放连接成功program 数量的数组
            int[] linkStatus = new int[1];
            //获取program的连接情况
            GLES20.glGetProgramiv(program,GLES20.GL_LINK_STATUS,linkStatus,0);
            //若连接失败则报错并删除程序
            if (linkStatus[0] !=GLES20.GL_TRUE){
                Log.e("ES20_ERROR", "Could not link program: ");
                Log.e("ES20_ERROR", GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }

        return program;

    }

    //检查每一步操作是否有错误的方法
    public static void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e("ES20_ERROR", op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }

    //从sh脚本中加载shader内容的方法
    public static String loadFromAssetsFile(String fname,Resources r) {
        String result=null;
        try {
            InputStream in=r.getAssets().open(fname);
            int ch=0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while((ch=in.read())!=-1)
            {
                baos.write(ch);
            }
            byte[] buff=baos.toByteArray();
            baos.close();
            in.close();
            result=new String(buff,"UTF-8");
            result=result.replaceAll("\\r\\n","\n");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
