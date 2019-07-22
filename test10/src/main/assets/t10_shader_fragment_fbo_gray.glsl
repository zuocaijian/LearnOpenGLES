precision mediump float;
uniform sampler2D vTexture;
varying vec2 vCoordinate;

void main()
{
    vec4 nColor = texture2D(vTexture, vCoordinate);
    //0.299f, 0.587f, 0.114f
    float c = nColor.r * 0.299 + nColor.g * 0.587 + nColor.b * 0.114;
    gl_FragColor = vec4(c, c, c, nColor.a);
}