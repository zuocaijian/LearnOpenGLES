attribute vec4 vPosition;
uniform mat4 vMatrix;
uniform mat4 vCoordMatrix;
attribute vec2 vCoordinate;

varying vec2 textureCoordinate;

void main()
{
    gl_Position = vMatrix * vPosition;
    //textureCoordinate = vCoordinate;
    textureCoordinate = (vCoordMatrix * vec4(vCoordinate, 0, 1)).xy;
}