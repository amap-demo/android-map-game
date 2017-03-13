uniform mat4 u_Matrix;

attribute vec3 a_Position;
attribute vec2 a_TextureCoordinates;
varying vec2 v_TextureCoord;

void main()
{                           
    gl_Position = u_Matrix * vec4(a_Position,1);
    v_TextureCoord = a_TextureCoordinates;
//    v_TextureCoord = (a_TextureCoordinates-0.5)*5.0;//将接收的纹理坐标传递给片元着色器
}
