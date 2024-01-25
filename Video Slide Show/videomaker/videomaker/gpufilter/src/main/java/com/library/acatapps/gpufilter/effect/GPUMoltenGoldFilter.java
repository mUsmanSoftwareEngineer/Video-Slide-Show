package com.library.acatapps.gpufilter.effect;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;

public class GPUMoltenGoldFilter extends GPUImageFilter {
    public static final String FRAGMENT_SHADER = "\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nuniform float texelWidth;\nuniform float texelHeight;\nuniform float paramIntensity;\nuniform float paramSize;\nuniform float paramSmoothness;\nvec3 sampleNeighbor(int diffX, int diffY, vec2 uv) { return texture2D(sTexture, uv + vec2(float(diffX) * texelWidth, float(diffY) * texelHeight)).rgb; }\nfloat luminance(vec3 c) {return dot(c, vec3(.2126, .7152, .0722));}\nvec3 normal(vec2 uv, int offsetX, int offsetY, float depth) {\n    float right = abs(luminance(sampleNeighbor(offsetX, 0, uv)));\n    float left = abs(luminance(sampleNeighbor(-offsetX, 0, uv)));\n    float down = abs(luminance(sampleNeighbor(0, offsetY, uv)));\n    float up = abs(luminance(sampleNeighbor(0, -offsetY, uv)));\n    return normalize(vec3((left-right) * 0.1, (up-down) * 0.1, 1.0 / depth));\n}\nvoid main() {\n    float intensity = 0.75 + (paramIntensity / 100.0)*2.0;\n    float lightSize = 0.4 + (paramSize / 100.0)*2.0;\n    float depth = 12.5 - (paramSmoothness / 100.0)*10.5;\n    vec3 n = normal(vTextureCoord, 2, 2, depth);\n    float diag = 100.0*sqrt(pow(1.0/texelWidth/100.0, 2.0) + pow(1.0/texelHeight/100.0, 2.0));\n    vec3 lightP0 = vec3(0.5 / texelWidth, 0.5 / texelHeight, lightSize*5.0*diag);\n    vec3 lightP1 = vec3(vTextureCoord / vec2(texelWidth, texelHeight), 0.0);\n    vec3 tc1 = sampleNeighbor(0, 0, vTextureCoord) * dot(n, normalize(lightP0 - lightP1));\n    vec3 tc2 = tc1;\n    float e = intensity*60.0;\n    vec3 lightP2 = vec3(0.5 / texelWidth, 0.5 / texelHeight, 200000.0);\n    float t = dot(normalize(reflect(lightP0 - lightP1, n)), normalize(lightP1 - lightP2));\n    tc1 += pow(clamp(t, 0.0, 1.0), e);\n    lightP2 += vec3(0.5 / texelWidth, 0.5 / texelHeight, 380.0);\n    tc2 += pow(clamp(t, 0.0, 0.98), e);\n    gl_FragColor = vec4(tc1 - tc2 + vec3(0.15, 0.04, -0.45), 1.0);\n}\n";
    private float intensity = 60.0f;
    private float size = 20.0f;
    private float smoothness = 40.0f;
    private float texelHeight;
    private float texelWidth;

    private int intensityLocation;
    private int sizeLocation;
    private int smoothnessLocation;
    private int texelWidthLocation;
    private int texelHeightLocation;


    public GPUMoltenGoldFilter() {
        super("attribute vec4 position;\nattribute vec4 inputTextureCoordinate;\nvarying highp vec2 vTextureCoord;\nvoid main() {\ngl_Position = position;\nvTextureCoord = inputTextureCoordinate.xy;\n}\n", FRAGMENT_SHADER);
    }

    @Override
    public void onInit() {
        super.onInit();
        intensityLocation = GLES20.glGetUniformLocation(getProgram(), "paramIntensity");
        sizeLocation = GLES20.glGetUniformLocation(getProgram(), "paramSize");
        smoothnessLocation = GLES20.glGetUniformLocation(getProgram(), "paramSmoothness");
        texelWidthLocation = GLES20.glGetUniformLocation(getProgram(), "texelWidth");
        texelHeightLocation = GLES20.glGetUniformLocation(getProgram(), "texelHeight");
    }

    @Override
    public void onDraw(int textureId, FloatBuffer cubeBuffer, FloatBuffer textureBuffer) {
        super.onDraw(textureId, cubeBuffer, textureBuffer);
        setFloat(intensityLocation, intensity);
        setFloat(sizeLocation, size);
        setFloat(smoothnessLocation, smoothness);
        setFloat(texelWidthLocation, 1f / getOutputWidth());
        setFloat(texelHeightLocation, 1f / getOutputHeight());
    }
}
