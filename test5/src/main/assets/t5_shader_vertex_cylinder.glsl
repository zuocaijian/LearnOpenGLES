attribute vec4 vPosition;
uniform mat4 vMatrix;
varying vec4 vColor;

void main()
{
    gl_Position = vMatrix * vPosition;
    float color;
    if (vPosition.z != 0.0)
    {
        color = 0.0;
    }
    else
    {
        color = 0.9;
    }
    vColor = vec4(color, color, color, 1.0);
}