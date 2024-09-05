import { WebPlugin } from '@capacitor/core';
import type { CapacitorVideoPlayerPlugin, capVideoPlayerResult } from './definitions';
export declare class CapacitorVideoPlayerWeb extends WebPlugin implements CapacitorVideoPlayerPlugin {
    initPlayer(): Promise<capVideoPlayerResult>;
    isPlaying(): Promise<capVideoPlayerResult>;
    setVideoUrl(_options: {
        url: string;
    }): Promise<capVideoPlayerResult>;
    playerPlay(): Promise<void>;
    playerPause(): Promise<capVideoPlayerResult>;
    playerStop(): Promise<capVideoPlayerResult>;
    exitPlayer(): Promise<capVideoPlayerResult>;
    echo(options: {
        value: string;
    }): Promise<{
        value: string;
    }>;
}
