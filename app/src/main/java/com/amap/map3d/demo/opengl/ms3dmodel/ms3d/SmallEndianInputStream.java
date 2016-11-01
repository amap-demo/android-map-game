package com.amap.map3d.demo.opengl.ms3dmodel.ms3d;
import java.io.IOException;
import java.io.InputStream;

//自己开发的符合SmallEndian字节顺序的处理流
public class SmallEndianInputStream{
	InputStream is;  //封装的输入流
	public SmallEndianInputStream(InputStream is){
		this.is = is;
	}
	//以SmallEndian字节顺序读取一个32位整数并返回
	public int readInt() throws IOException	{
		byte[] buff = new byte[4];
		is.read(buff);
		return (buff[3] << 24) 
			+ ((buff[2] << 24) >>> 8) 
			+ ((buff[1] << 24) >>> 16) 
			+ ((buff[0] << 24) >>> 24);
	}
	//以SmallEndian字节顺序读取一个16位无符号整数并返回
	public int readUnsignedShort()throws IOException{
		byte[] buff = new byte[2];
		is.read(buff);
		return ((buff[1] << 24) >>> 16) 
				+ ((buff[0] << 24) >>> 24);
	}
	//读取一个字节
	public byte readByte() throws IOException{
		return (byte) is.read();
	}
	//读取一个浮点数
	public final float readFloat() throws IOException{
		return Float.intBitsToFloat(this.readInt());
	}
	public int read(byte[] buff) throws IOException{	//读取字节数组
		int count = this.is.read(buff);
		return count;
	}
	public String readString(int length) throws IOException{//读取字符串
		byte[] buff = new byte[length];
		this.is.read(buff);
		return this.makeSafeString(buff);
	}
	//将字节数组中的数据安全地转化为字符串并返回
	public String makeSafeString(byte buffer[]){
        final int len = buffer.length;
        for (int i = 0; i < len; i++){
            if (buffer[i] == (byte) 0){
                return new String(buffer, 0, i);
            }}
        return new String(buffer).trim();
    }
	public void close() throws IOException{	//关闭流
		if(is != null)
			is.close();
	}}
