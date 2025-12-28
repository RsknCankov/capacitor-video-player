import { WebPlugin } from '@capacitor/core';
import type { CapacitorVideoPlayerPlugin, capVideoPlayerResult, TimeRanges } from './definitions';
export declare class CapacitorVideoPlayerWeb extends WebPlugin implements CapacitorVideoPlayerPlugin {
    seekForward(): Promise<void>;
    seekBackward(): Promise<void>;
    seekStart(): Promise<void>;
    seekEnd(): Promise<void>;
    selectSubtitleStream(options: {
        language: string | null;
    }): Promise<void>;
    getCurrentSubtitleStream(): Promise<{
        subtitles: string;
    }>;
    initPlayer(): Promise<capVideoPlayerResult>;
    isPlaying(): Promise<capVideoPlayerResult>;
    setVideoUrl(_options: {
        url: string;
    }): Promise<capVideoPlayerResult>;
    playerPlay(): Promise<void>;
    playerPause(): Promise<capVideoPlayerResult>;
    getCurrentTime(): Promise<{
        currentTime: number;
    }>;
    getDuration(): Promise<{
        duration: number;
    }>;
    getBuffered(): Promise<TimeRanges | null>;
    playerStop(): Promise<capVideoPlayerResult>;
    exitPlayer(): Promise<capVideoPlayerResult>;
    echo(options: {
        value: string;
    }): Promise<{
        value: string;
    }>;
}
