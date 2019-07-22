attribute vec4 vPosition;
uniform mat4 vMatrix;
attribute vec2 aCoordinate;

varying vec2 vCoordinate;

void main()
{
    gl_Position = vMatrix * vPosition;
    //vec2 tmp = aCoordinate.xy;
    //tmp.y = 1.0 - tmp.y;
    //vCoordinate = tmp;
    vCoordinate = aCoordinate.xy;
    vCoordinate.y = 1.0 - vCoordinate.y;
}