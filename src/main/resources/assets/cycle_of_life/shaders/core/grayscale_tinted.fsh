#version 150

#moj_import <fog.glsl>


uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform vec3 PrimaryColor;
uniform vec3 SecondaryColor;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec2 texCoord0;
in vec4 vertexColor;
in vec4 lightMapColor;

out vec4 fragColor;


void main() {
    vec4 texColor = texture(Sampler0, texCoord0);
    if (texColor.a < 0.1) discard;

    texColor *= vertexColor * ColorModulator;


    float brightness = texColor.r;
    texColor.rgb = mix(SecondaryColor, PrimaryColor, brightness);
    texColor *= lightMapColor;

    fragColor = linear_fog(texColor, vertexDistance, FogStart, FogEnd, FogColor);
}