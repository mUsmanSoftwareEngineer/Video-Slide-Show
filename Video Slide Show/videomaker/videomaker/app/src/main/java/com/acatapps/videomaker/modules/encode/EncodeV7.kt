package com.acatapps.videomaker.modules.encode

import android.media.*
import android.opengl.EGL14
import android.opengl.EGLConfig
import android.opengl.EGLExt
import android.opengl.GLES20
import android.os.Environment
import android.util.Log
import android.view.Surface
import android.widget.Toast
import com.acatapps.videomaker.application.VideoMakerApplication
import com.acatapps.videomaker.data.RenderImageSlideData
import com.acatapps.videomaker.slide_show_package_2.SlideShowDrawer
import com.acatapps.videomaker.slide_show_package_2.SlideShowForRender
import com.acatapps.videomaker.slide_show_package_2.slide_show_gl_view_2.GLTexture
import com.acatapps.videomaker.slide_show_theme.SlideThemeDrawer
import com.acatapps.videomaker.utils.Logger
import java.io.File
import java.nio.ByteBuffer
import kotlin.math.roundToInt


class EncodeV7(val renderData: RenderImageSlideData) {

    private var mWidth = 1080
    private var mHeight = 1080
    private var mBitRare = 10000000

    var totalTimeForGetFrame = 0L

    private val MIME_TYPE = "video/avc"
    private val FRAME_RATE = 60
    private val IFRAME_INTERVAl = 5
    private val NUMBER_FRAMES = FRAME_RATE * 60

    private var mEncoder = MediaCodec.createEncoderByType(MIME_TYPE)
    private lateinit var mInputSurface: CodecInputSurface
    private lateinit var mMuxer: MediaMuxer

    private var mTrackIndex = 0
    private var mMuxerStarted = false

    private val mBufferInfo: MediaCodec.BufferInfo = MediaCodec.BufferInfo()
    val slideShowForRender = SlideShowForRender(
        renderData.imageList,
        renderData.bitmapHashMap,
        renderData.delayTimeSec * 1000
    )
    val slideDrawer = SlideShowDrawer()
    val glTexture = GLTexture()
    val themeDrawer = SlideThemeDrawer(renderData.themeData)
    fun performEncodeVideo(onUpdateProgress: (Float) -> Unit, onComplete:()->Unit) {
        try {
            val start = System.currentTimeMillis()
            prepareEncode()
            mInputSurface.makeCurrent()
            slideDrawer.prepare(renderData.gsTransition)
            glTexture.prepare()
            themeDrawer.prepare()
            var startSessionTime = System.currentTimeMillis()
            for (time in 0 until slideShowForRender.totalDuration step 30) {
                drainEncoder(false)
                GLES20.glClearColor(0f, 0f, 0f, 1f)
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

                GLES20.glEnable(GLES20.GL_BLEND)
                GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA)
                slideDrawer.changeFrameData(slideShowForRender.getFrameByVideoTimeForRender(time))
                slideDrawer.drawFrame()
                themeDrawer.drawFrame()
                mInputSurface.setPresentationTime(time * 1000000L)
                mInputSurface.swapBuffer()

                onUpdateProgress.invoke(time.toFloat() / slideShowForRender.totalDuration)

                if(time % 1200 == 0) {
                    val delta = System.currentTimeMillis()-startSessionTime
                    startSessionTime = System.currentTimeMillis()
                    Logger.e("time left = ${delta*(slideShowForRender.totalDuration-time)/1200}")
                }
            }




            drainEncoder(true)
            onComplete.invoke()

            val delta = System.currentTimeMillis() - start
            Logger.e("total time = $delta  --- ${(delta / 1000f).roundToInt()}")
           releaseEncoder()
            writeAudio()
        } catch (e: Exception) {

        } finally {
           // releaseEncoder()
        }

    }

    fun releaseEncoder() {

        if (mEncoder != null) {
            mEncoder.stop()
            mEncoder.release()

        }
        if (mInputSurface != null) {
            mInputSurface.release()
        }
        if (mMuxer != null) {
            mMuxer.stop()
            mMuxer.release()

        }
    }

    fun swapBuffer(time: Int) {
        mInputSurface.setPresentationTime(computePresentationTimeNsec(time / 30))
        mInputSurface.swapBuffer()
    }

    fun generateSurfaceFrame(index: Int) {

    }

    private fun computePresentationTimeNsec(frameIndex: Int): Long {
        val ONE_BILLION: Long = 1000000000
        return frameIndex * ONE_BILLION / FRAME_RATE
    }
    private var mOutFilePath = ""
    private val OUTPUT_DIR: File? = VideoMakerApplication.getContext().getExternalFilesDir(null)
    private var mAudioTrack = -1
    private var mAudioExtractor = MediaExtractor()
    private fun prepareEncode() {
        val mediaFormat = MediaFormat.createVideoFormat(MIME_TYPE, mWidth, mHeight).apply {
            setInteger(
                MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
            )
            setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE)
            setInteger(MediaFormat.KEY_BIT_RATE, mBitRare)
            setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAl)
        }
        mEncoder.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        mInputSurface = CodecInputSurface(mEncoder.createInputSurface())
        mEncoder.start()
        mOutFilePath = File(OUTPUT_DIR, "encode-$mWidth x $mHeight ${System.currentTimeMillis()}.mp4").absolutePath
        try {
            mMuxer = MediaMuxer(mOutFilePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

        } catch (e: java.lang.Exception) {

        }
        mTrackIndex = -1
        mMuxerStarted = false
    }

    fun drainEncoder(enOfStream: Boolean) {
        val timeOutUSec = 10000L
        if (enOfStream) {
            mEncoder.signalEndOfInputStream()
        }

        var encoderOutputBuffers = mEncoder.outputBuffers
        while (true) {
            val encoderStatus = mEncoder.dequeueOutputBuffer(mBufferInfo, timeOutUSec)
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {

                if (!enOfStream) {
                    break
                }
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {

                encoderOutputBuffers = mEncoder.outputBuffers
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {

                if (mMuxerStarted) {
                    throw RuntimeException("format changed twice")
                }
                val mediaFormat = mEncoder.outputFormat
                mTrackIndex = mMuxer.addTrack(mediaFormat)
                mMuxer.start()
                mMuxerStarted = true
            } else if (encoderStatus < 0) {
                Logger.e("unexpected result from encoder.dequeueOutputBuffer: $encoderStatus")
            } else {

                val encodedData = encoderOutputBuffers[encoderStatus]

                if (encodedData == null) {
                    Logger.e("encoderOutputBuffer $encoderStatus was null")
                    throw RuntimeException("encoderOutputBuffer $encoderStatus was null")
                }

                if ((mBufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    mBufferInfo.size = 0

                }

                if (mBufferInfo.size != 0) {
                    if (!mMuxerStarted) {
                        throw RuntimeException("muxer hasn't started")
                    }

                    encodedData.position(mBufferInfo.offset)
                    encodedData.limit(mBufferInfo.offset + mBufferInfo.size)

                    mMuxer.writeSampleData(mTrackIndex, encodedData, mBufferInfo)
                }
                mEncoder.releaseOutputBuffer(encoderStatus, false)
                if (mBufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                    if (!enOfStream) {
                        Logger.e("reached end of stream unexpectedly")
                    } else {
                        Logger.e("end of stream reached")
                    }
                    break // out of while
                }
            }
        }
    }

    fun writeAudio() {
        var outputFile = "";

        try {

            val file =  File("${VideoMakerApplication.getContext().getExternalFilesDir(null)}/final_${System.currentTimeMillis()}.mp4")
            file.createNewFile();
            outputFile = file.getAbsolutePath()

            Logger.e("out file video = $mOutFilePath")
            val videoExtractor = MediaExtractor()
            videoExtractor.setDataSource(mOutFilePath)

            val audioExtractor = MediaExtractor()
            audioExtractor.setDataSource("/storage/emulated/0/Music/Wiz Khalifa - See You Again ft. Charlie Puth [Offi(M4A_128K).m4a")

            Logger.e("Video Extractor Track Count " + videoExtractor.trackCount)
            Logger.e("Audio Extractor Track Count " + audioExtractor.trackCount)

            val muxer = MediaMuxer(outputFile, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

            videoExtractor.selectTrack(0)
            val videoFormat = videoExtractor.getTrackFormat (0)
            val videoTrack = muxer.addTrack (videoFormat)

            audioExtractor.selectTrack(0)
            val audioFormat = audioExtractor.getTrackFormat (0)
            val audioTrack = muxer.addTrack(audioFormat)

            Logger.e("Video Format " + videoFormat.toString())
            Logger.e("Audio Format " + audioFormat.toString())

            var sawEOS = false
            var frameCount = 0
            var offset = 100
            var sampleSize = 256 * 1024
            var videoBuf = ByteBuffer . allocate (sampleSize)
            var audioBuf = ByteBuffer . allocate (sampleSize)
            var videoBufferInfo =  MediaCodec.BufferInfo()
            var audioBufferInfo =  MediaCodec.BufferInfo()


            videoExtractor.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC)
            audioExtractor.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC)

            muxer.start()

            while (!sawEOS) {
                Logger.e("!sawEOS")
                videoBufferInfo.offset = offset
                videoBufferInfo.size = videoExtractor.readSampleData(videoBuf, offset)


                if (videoBufferInfo.size < 0 || audioBufferInfo.size < 0) {
                    Logger.e( "saw input EOS.")
                    sawEOS = true;
                    videoBufferInfo.size = 0

                } else {
                    videoBufferInfo.presentationTimeUs = videoExtractor.getSampleTime()
                    videoBufferInfo.flags = videoExtractor.getSampleFlags()
                    muxer.writeSampleData(videoTrack, videoBuf, videoBufferInfo)
                    videoExtractor.advance()

                    frameCount++

                }
            }

            var sawEOS2 = false
            var frameCount2 =0
            while (!sawEOS2) {
                Logger.e("!sawEOS__2")
                frameCount2++

                audioBufferInfo.offset = offset;
                audioBufferInfo.size = audioExtractor.readSampleData(audioBuf, offset)

                if (videoBufferInfo.size < 0 || audioBufferInfo.size < 0 ) {
                    Logger.e("saw input EOS.")
                    sawEOS2 = true;
                    audioBufferInfo.size = 0
                } else {
                    audioBufferInfo.presentationTimeUs = audioExtractor.sampleTime
                    audioBufferInfo.flags = audioExtractor.getSampleFlags()
                    muxer.writeSampleData(audioTrack, audioBuf, audioBufferInfo)
                    audioExtractor.advance()
                }
            }

            muxer.stop()
            muxer.release()


        } catch (e:java.lang.Exception) {
            Logger.e(e.printStackTrace().toString())
        } catch (e:java.lang.Exception) {
            Logger.e(e.printStackTrace().toString())
        }
    }
    private class CodecInputSurface(val mSurface: Surface) {
        private val EGL_RECORDABLE_ANDROID = 0x3142

        private var mEGLDisplay = EGL14.EGL_NO_DISPLAY
        private var mEGLContext = EGL14.EGL_NO_CONTEXT
        private var mEGLSurface = EGL14.EGL_NO_SURFACE

        init {
            eglSetup()
        }

        fun eglSetup() {
            mEGLDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
            if (mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
                throw RuntimeException("unable to get EGL14 display")
            }

            val version = IntArray(2)
            if (!EGL14.eglInitialize(mEGLDisplay, version, 0, version, 1)) {
                throw RuntimeException("unable to initialize EGL14");
            }

            val attrList = intArrayOf(
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_ALPHA_SIZE, 8,
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL_RECORDABLE_ANDROID, 1,
                EGL14.EGL_NONE
            )

            val configs: Array<EGLConfig?> = arrayOfNulls(1)
            val numConfigs = IntArray(1)
            EGL14.eglChooseConfig(mEGLDisplay, attrList, 0, configs, 0, configs.size, numConfigs, 0)
            val attr_list = intArrayOf(EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE)
            mEGLContext =
                EGL14.eglCreateContext(mEGLDisplay, configs[0], EGL14.EGL_NO_CONTEXT, attr_list, 0)

            val surfaceAttrs = intArrayOf(EGL14.EGL_NONE)
            mEGLSurface =
                EGL14.eglCreateWindowSurface(mEGLDisplay, configs[0], mSurface, surfaceAttrs, 0)
        }

        fun makeCurrent() {
            EGL14.eglMakeCurrent(mEGLDisplay, mEGLSurface, mEGLSurface, mEGLContext);
        }

        fun swapBuffer(): Boolean {
            return EGL14.eglSwapBuffers(mEGLDisplay, mEGLSurface)
        }

        fun setPresentationTime(nSecs: Long) {
            EGLExt.eglPresentationTimeANDROID(mEGLDisplay, mEGLSurface, nSecs)
        }

        fun release() {
            if (mEGLDisplay !== EGL14.EGL_NO_DISPLAY) {
                EGL14.eglMakeCurrent(
                    mEGLDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,
                    EGL14.EGL_NO_CONTEXT
                )
                EGL14.eglDestroySurface(mEGLDisplay, mEGLSurface)
                EGL14.eglDestroyContext(mEGLDisplay, mEGLContext)
                EGL14.eglReleaseThread()
                EGL14.eglTerminate(mEGLDisplay)
            }
            mSurface.release()
            mEGLDisplay = EGL14.EGL_NO_DISPLAY
            mEGLContext = EGL14.EGL_NO_CONTEXT
            mEGLSurface = EGL14.EGL_NO_SURFACE

        }

    }

}