import type { PluginListenerHandle } from '@capacitor/core';
export interface CapacitorVideoPlayerPlugin {
    /**
     * Initialize a video player
     *
     */
    initPlayer(): Promise<capVideoPlayerResult>;
    /**
     * Return if a given playerId is playing
     *
     */
    isPlaying(): Promise<capVideoPlayerResult>;
    /**
     * Play the current video from a given playerId
     *
     */
    setVideoUrl(options: {
        url: string;
    }): Promise<capVideoPlayerResult>;
    playerPlay(): Promise<void>;
    /**
     * Pause the current video from a given playerId
     *
     */
    playerPause(): Promise<capVideoPlayerResult>;
    seekForward(): Promise<void>;
    seekBackward(): Promise<void>;
    /**
     * Seek to the start of the video
     *
     */
    seekStart(): Promise<void>;
    /**
     * Seek to the end of the video
     *
     */
    seekEnd(): Promise<void>;
    /**
     * Get the current playback position in milliseconds
     *
     */
    getCurrentTime(): Promise<{
        currentTime: number;
    }>;
    /**
     * Get the total duration of the video in milliseconds
     *
     */
    getDuration(): Promise<{
        duration: number;
    }>;
    /**
     * Get the buffered position in milliseconds
     *
     */
    getBuffered(): Promise<TimeRanges | null>;
    playerStop(): Promise<capVideoPlayerResult>;
    /**
     * Exit player
     *
     */
    exitPlayer(): Promise<capVideoPlayerResult>;
    /**
     * Add event listeners for player events.
     *
     * @param eventName The name of the event to listen for.
     * @param listenerFunc The function to call when the event is triggered.
     */
    addListener(eventName: 'CapVideoPlayerBuffering' | 'CapVideoPlayerIdle' | 'CapVideoPlayerPlaying' | 'CapVideoPlayerReady' | 'CapVideoPlayerError' | 'CapVideoPlayerEnd' | 'CapVideoPlayerSelectedSubtitlesStream' | 'CapVideoPlayerCurrentTime' | 'CapVideoPlayerHLSProgramDateTimeTag' | 'CapVideoPlayerHLSTargetDuration' | 'CapVideoPlayerSubtitleStreams', listenerFunc: (event: any) => void): Promise<PluginListenerHandle>;
    removeAllListeners(): Promise<void>;
    selectSubtitleStream(options: {
        language: string | null;
    }): Promise<void>;
}
export interface capEchoOptions {
    /**
     *  String to be echoed
     */
    value?: string;
}
export interface capVideoPlayerOptions {
    url?: string;
    /**
     * The url of subtitle associated with the video
     */
    subtitle?: string;
    /**
     * The language of subtitle
     * see https://github.com/libyal/libfwnt/wiki/Language-Code-identifiers
     */
    language?: string;
    /**
     * SubTitle Options
     */
    subtitleOptions?: SubTitleOptions;
    /**
     * Initial playing rate
     */
    rate?: number;
    /**
     * Headers for the request (iOS, Android)
     * by Manuel García Marín (https://github.com/PhantomPainX)
     */
    headers?: {
        [key: string]: string;
    };
    /**
     * Title shown in the player (Android)
     * by Manuel García Marín (https://github.com/PhantomPainX)
     */
    title?: string;
    /**
     * Subtitle shown below the title in the player (Android)
     * by Manuel García Marín (https://github.com/PhantomPainX)
     */
    smallTitle?: string;
}
export interface capVideoRateOptions {
    /**
     * Rate value
     */
    rate?: number;
}
export interface capVideoVolumeOptions {
    /**
     * Volume value between [0 - 1]
     */
    volume?: number;
}
export interface capVideoTimeOptions {
    /**
     * Video time value you want to seek to
     */
    seektime?: number;
}
export interface capVideoMutedOptions {
    /**
     * Muted value true or false
     */
    muted?: boolean;
}
export interface capVideoListener {
    /**
     * Video current time when listener trigerred
     */
    currentTime?: number;
}
export interface capExitListener {
    /**
     * Dismiss value true or false
     */
    dismiss?: boolean;
    /**
     * Video current time when listener trigerred
     */
    currentTime?: number;
}
export interface capVideoPlayerResult {
    /**
     * result set to true when successful else false
     */
    result?: boolean;
    /**
     * method name
     */
    method?: string;
    /**
     * value returned
     */
    value?: any;
    /**
     * message string
     */
    message?: string;
}
export interface SubTitleOptions {
    /**
     * Foreground Color in RGBA (default rgba(255,255,255,1)
     */
    foregroundColor?: string;
    /**
     * Background Color in RGBA (default rgba(0,0,0,1)
     */
    backgroundColor?: string;
    /**
     * Font Size in pixels (default 16)
     */
    fontSize?: number;
}
export interface TimeRanges {
    /**
     * Number of time ranges
     */
    length: number;
    /**
     * Start time of the specified range in milliseconds
     */
    start(index: number): number;
    /**
     * End time of the specified range in milliseconds
     */
    end(index: number): number;
}
