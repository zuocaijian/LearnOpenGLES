precision mediump float;
uniform sampler2D vTexture;
varying vec2 vCoordinate;

void main()
{
    gl_FragColor = texture2D(vTexture, vCoordinate);
}