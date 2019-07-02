attribute vec4 vPosition;
uniform mat4 vMatrix;
attribute vec2 vCoordinate;

varying vec4 gPosition;
varying vec2 aCoordinate;
varying vec4 aPos;

void main()
{
    gl_Position = vMatrix * vPosition;
    aCoordinate = vCoordinate;
}