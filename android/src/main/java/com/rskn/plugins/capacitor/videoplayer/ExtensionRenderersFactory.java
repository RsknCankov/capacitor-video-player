package com.rskn.plugins.capacitor.videoplayer;

import android.content.Context;
import android.os.Handler;

import androidx.media3.common.MimeTypes;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.decoder.ffmpeg.FfmpegLibrary;
import androidx.media3.exoplayer.DefaultRenderersFactory;
import androidx.media3.exoplayer.Renderer;
import androidx.media3.exoplayer.audio.AudioRendererEventListener;
import androidx.media3.exoplayer.audio.AudioSink;
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Plugs in the bundled decoder extensions (FFmpeg audio, libvpx VP9, libgav1 AV1).
 *
 * <p>Audio formats FFmpeg can decode go to FFmpeg, so formats the TV cannot decode (MP2, DTS, ...)
 * still play. AAC, AC-3 and E-AC-3 stay on the device's own decoder (or passthrough to an AV
 * receiver), with FFmpeg as the fallback. Video extensions are software decoders, so they only
 * come after the hardware decoders and are used when the device has no decoder for the format.
 */
@UnstableApi
final class ExtensionRenderersFactory extends DefaultRenderersFactory {

    private static final Set<String> HARDWARE_FIRST_AUDIO = new HashSet<>(Arrays.asList(
            MimeTypes.AUDIO_AAC,
            MimeTypes.AUDIO_AC3,
            MimeTypes.AUDIO_E_AC3,
            MimeTypes.AUDIO_E_AC3_JOC));

    ExtensionRenderersFactory(Context context) {
        super(context);
        setExtensionRendererMode(EXTENSION_RENDERER_MODE_ON);
    }

    @Override
    protected void buildAudioRenderers(
            Context context,
            @ExtensionRendererMode int extensionRendererMode,
            MediaCodecSelector mediaCodecSelector,
            boolean enableDecoderFallback,
            AudioSink audioSink,
            Handler eventHandler,
            AudioRendererEventListener eventListener,
            ArrayList<Renderer> out) {
        // MediaCodec comes first; hiding its decoders for FFmpeg-owned formats hands them to FFmpeg.
        MediaCodecSelector audioSelector = extensionRendererMode == EXTENSION_RENDERER_MODE_OFF
                ? mediaCodecSelector
                : (mimeType, requiresSecureDecoder, requiresTunnelingDecoder) ->
                        !HARDWARE_FIRST_AUDIO.contains(mimeType) && FfmpegLibrary.supportsFormat(mimeType)
                                ? Collections.emptyList()
                                : mediaCodecSelector.getDecoderInfos(
                                        mimeType, requiresSecureDecoder, requiresTunnelingDecoder);
        super.buildAudioRenderers(
                context,
                extensionRendererMode,
                audioSelector,
                enableDecoderFallback,
                audioSink,
                eventHandler,
                eventListener,
                out);
    }
}
